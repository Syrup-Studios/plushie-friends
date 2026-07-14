package net.syrupstudios.plushiefriends.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;
import java.util.HashMap;
import java.util.Map;

public final class PlushieProfileCache {
    private static final Map<String, Skin> SKIN_CACHE = new HashMap<>();

    private PlushieProfileCache() {}

    public static Skin getSkin(GameProfile profile) {
        if (profile != null && profile.getProperties().containsKey("textures")) {
            String key = getTextureKey(profile);
            Skin cached = SKIN_CACHE.get(key);
            if (cached == null) {
                ResourceLocation texture = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile);
                boolean slim = isSlimSkin(profile);
                cached = new Skin(texture, slim);
                SKIN_CACHE.put(key, cached);
            }
            return cached;
        }
        return new Skin(DefaultPlayerSkin.getDefaultSkin(), false);
    }

    private static String getTextureKey(GameProfile profile) {
        for (Property property : profile.getProperties().get("textures")) {
            return property.getValue();
        }
        return profile.getName() != null ? profile.getName() : profile.toString();
    }

    private static boolean isSlimSkin(GameProfile profile) {
        for (Property property : profile.getProperties().get("textures")) {
            return PlushieProfileManager.getOrCacheIsSlim(property.getValue());
        }
        return false;
    }

    public record Skin(ResourceLocation textureLocation, boolean slim) {}
}
