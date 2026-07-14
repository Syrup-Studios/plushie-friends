package net.syrupstudios.plushiefriends.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class PlushieProfileManager {
    private static final Map<String, GameProfile> PROFILE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> SLIM_MODEL_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, CompletableFuture<GameProfile>> IN_FLIGHT = new ConcurrentHashMap<>();
    private static final Map<String, Long> FAILED_UNTIL = new ConcurrentHashMap<>();
    private static final AtomicLong CACHE_GENERATION = new AtomicLong();
    private static final Pattern VALID_PLAYER_NAME = Pattern.compile("^[A-Za-z0-9_]{1,16}$");
    private static final long FAILURE_COOLDOWN_NANOS = TimeUnit.MINUTES.toNanos(10);

    private PlushieProfileManager() {}

    public static void resolveProfileAsync(String ownerName, Consumer<GameProfile> callback) {
        String normalizedName = normalizePlayerName(ownerName);
        if (normalizedName == null) {
            callback.accept(null);
            return;
        }

        String cacheKey = normalizedName.toLowerCase(Locale.ROOT);
        GameProfile cached = PROFILE_CACHE.get(cacheKey);
        if (cached != null) {
            callback.accept(cached);
            return;
        }

        if (isFailureCoolingDown(cacheKey)) {
            callback.accept(null);
            return;
        }

        CompletableFuture<GameProfile> request = new CompletableFuture<>();
        CompletableFuture<GameProfile> existing = IN_FLIGHT.putIfAbsent(cacheKey, request);
        CompletableFuture<GameProfile> result = existing != null ? existing : request;
        result.whenComplete((profile, error) -> callback.accept(error == null ? profile : null));

        if (existing != null) return;

        long generation = CACHE_GENERATION.get();
        try {
            SkullBlockEntity.updateGameprofile(new GameProfile(null, normalizedName), profile -> {
                GameProfile resolved = hasTextures(profile) ? profile : null;
                if (CACHE_GENERATION.get() == generation) {
                    if (resolved != null) {
                        cacheProfile(cacheKey, resolved);
                    } else {
                        recordFailure(cacheKey);
                    }
                }
                request.complete(resolved);
                IN_FLIGHT.remove(cacheKey, request);
            });
        } catch (RuntimeException error) {
            if (CACHE_GENERATION.get() == generation) {
                recordFailure(cacheKey);
            }
            request.complete(null);
            IN_FLIGHT.remove(cacheKey, request);
        }
    }

    public static GameProfile getCachedProfile(String ownerName) {
        return ownerName != null ? PROFILE_CACHE.get(ownerName.toLowerCase(Locale.ROOT)) : null;
    }

    public static GameProfile getOrResolveServerProfile(String ownerName, MinecraftServer server) {
        String normalizedName = normalizePlayerName(ownerName);
        if (normalizedName == null) return null;

        String cacheKey = normalizedName.toLowerCase(Locale.ROOT);
        GameProfile profile = PROFILE_CACHE.get(cacheKey);
        if (profile != null || server == null || isFailureCoolingDown(cacheKey)) {
            return profile;
        }

        try {
            profile = server.getProfileCache().get(normalizedName)
                    .map(cachedProfile -> server.getSessionService().fillProfileProperties(cachedProfile, true))
                    .filter(PlushieProfileManager::hasTextures)
                    .orElse(null);
        } catch (RuntimeException error) {
            profile = null;
        }

        if (profile != null) {
            cacheProfile(cacheKey, profile);
        } else {
            recordFailure(cacheKey);
        }
        return profile;
    }

    public static boolean shouldAttemptResolution(String ownerName) {
        String normalizedName = normalizePlayerName(ownerName);
        if (normalizedName == null) return false;
        String cacheKey = normalizedName.toLowerCase(Locale.ROOT);
        return !PROFILE_CACHE.containsKey(cacheKey)
                && !IN_FLIGHT.containsKey(cacheKey)
                && !isFailureCoolingDown(cacheKey);
    }

    public static boolean getOrCacheIsSlim(String textureValue) {
        return SLIM_MODEL_CACHE.computeIfAbsent(textureValue, PlushieProfileManager::decodeSlimModel);
    }

    private static void cacheModelType(GameProfile profile) {
        for (Property property : profile.getProperties().get("textures")) {
            getOrCacheIsSlim(property.getValue());
        }
    }

    private static boolean decodeSlimModel(String textureValue) {
        try {
            String jsonString = new String(Base64.getDecoder().decode(textureValue), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject textures = root.getAsJsonObject("textures");
            if (textures == null) return false;

            JsonObject skin = textures.getAsJsonObject("SKIN");
            if (skin == null) return false;

            JsonObject metadata = skin.getAsJsonObject("metadata");
            return metadata != null && metadata.has("model")
                    && "slim".equals(metadata.get("model").getAsString());
        } catch (RuntimeException error) {
            return false;
        }
    }

    public static void clearCache() {
        CACHE_GENERATION.incrementAndGet();
        PROFILE_CACHE.clear();
        SLIM_MODEL_CACHE.clear();
        IN_FLIGHT.clear();
        FAILED_UNTIL.clear();
    }

    private static String normalizePlayerName(String ownerName) {
        if (ownerName == null) return null;
        String trimmed = ownerName.trim();
        return VALID_PLAYER_NAME.matcher(trimmed).matches() ? trimmed : null;
    }

    private static boolean hasTextures(GameProfile profile) {
        return profile != null && profile.getProperties().containsKey("textures")
                && !profile.getProperties().get("textures").isEmpty();
    }

    private static void cacheProfile(String requestedKey, GameProfile profile) {
        PROFILE_CACHE.put(requestedKey, profile);
        if (profile.getName() != null) {
            PROFILE_CACHE.put(profile.getName().toLowerCase(Locale.ROOT), profile);
        }
        FAILED_UNTIL.remove(requestedKey);
        cacheModelType(profile);
    }

    private static void recordFailure(String cacheKey) {
        FAILED_UNTIL.put(cacheKey, System.nanoTime() + FAILURE_COOLDOWN_NANOS);
    }

    private static boolean isFailureCoolingDown(String cacheKey) {
        Long failedUntil = FAILED_UNTIL.get(cacheKey);
        if (failedUntil == null) return false;
        if (System.nanoTime() - failedUntil < 0) return true;
        FAILED_UNTIL.remove(cacheKey, failedUntil);
        return false;
    }
}
