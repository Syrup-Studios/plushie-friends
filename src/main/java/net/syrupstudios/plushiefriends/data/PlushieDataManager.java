package net.syrupstudios.plushiefriends.data;

import com.mojang.authlib.GameProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class PlushieDataManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<ResourceLocation, PlushieDefinition> PLUSHIES = new HashMap<>();

    public PlushieDataManager() {
        super(GSON, "plushies");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        PLUSHIES.clear();
        PlushieProfileManager.clearCache();

        prepared.forEach((id, jsonElement) -> {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String ownerName = jsonObject.has("owner_name") ? jsonObject.get("owner_name").getAsString() : "";

                List<String> lore = new ArrayList<>();
                if (jsonObject.has("lore")) {
                    jsonObject.getAsJsonArray("lore").forEach(element -> lore.add(element.getAsString()));
                }

                PLUSHIES.put(id, new PlushieDefinition(ownerName, lore));
            } catch (Exception e) {
                PlushieFriends.LOGGER.error("Failed to parse plushie data pack entry for {}", id, e);
            }
        });
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(PlushieFriends.MOD_ID, "plushies");
    }

    public static PlushieDefinition get(ResourceLocation id) {
        return PLUSHIES.get(id);
    }

    public static void preloadProfiles() {
        PLUSHIES.values().stream()
                .map(PlushieDefinition::ownerName)
                .distinct()
                .forEach(PlushieProfileManager::preloadOwner);
    }

    public static GameProfile getResolvedProfile(String ownerName, MinecraftServer server) {
        return PlushieProfileManager.getOrResolveServerProfile(ownerName, server);
    }

    public record PlushieDefinition(String ownerName, List<String> lore) {}
}
