package net.syrupstudios.plushiefriends.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class PlushieNbtHelper {
    /** Transitional alias for the legacy item container. */
    public static final String BLOCK_ENTITY_TAG = PlushieDataContract.LEGACY_BLOCK_ENTITY_TAG;
    public static final String PLUSHIE_OWNER = PlushieDataContract.OWNER;
    public static final String PLUSHIE_LORE = PlushieDataContract.LORE;
    public static final String PROFILE_NAME = "Name";

    public static final int TAG_STRING = 8;
    public static final int TAG_LIST = 9;
    public static final int TAG_COMPOUND = 10;

    private PlushieNbtHelper() {}

    @Nullable
    public static GameProfile getOwnerFromRoot(CompoundTag rootTag) {
        if (rootTag == null || !rootTag.contains(BLOCK_ENTITY_TAG, TAG_COMPOUND)) return null;
        return getOwnerFromBlockEntityTag(rootTag.getCompound(BLOCK_ENTITY_TAG));
    }

    @Nullable
    public static GameProfile getOwnerFromBlockEntityTag(CompoundTag blockEntityTag) {
        if (blockEntityTag == null) return null;

        if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_COMPOUND)) {
            return NbtUtils.readGameProfile(blockEntityTag.getCompound(PLUSHIE_OWNER));
        } else if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_STRING)) {
            String name = blockEntityTag.getString(PLUSHIE_OWNER);
            if (!name.isEmpty()) {
                return new GameProfile(null, name);
            }
        }
        return null;
    }

    public static String getOwnerNameFromBlockEntityTag(CompoundTag blockEntityTag) {
        if (blockEntityTag == null || !blockEntityTag.contains(PLUSHIE_OWNER)) return "";

        if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_COMPOUND)) {
            CompoundTag ownerTag = blockEntityTag.getCompound(PLUSHIE_OWNER);
            if (ownerTag.contains(PROFILE_NAME, TAG_STRING)) {
                return ownerTag.getString(PROFILE_NAME);
            }
        } else if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_STRING)) {
            return blockEntityTag.getString(PLUSHIE_OWNER);
        }
        return "";
    }

    public static List<String> getLoreFromBlockEntityTag(CompoundTag blockEntityTag) {
        List<String> lore = new ArrayList<>();
        if (blockEntityTag != null && blockEntityTag.contains(PLUSHIE_LORE, TAG_LIST)) {
            ListTag loreList = blockEntityTag.getList(PLUSHIE_LORE, TAG_STRING);
            for (int i = 0; i < loreList.size(); i++) {
                lore.add(loreList.getString(i));
            }
        }
        return lore;
    }

    public static void writeOwnerToBlockEntityTag(CompoundTag blockEntityTag, GameProfile profile) {
        if (blockEntityTag == null || profile == null) return;
        CompoundTag profileTag = new CompoundTag();
        NbtUtils.writeGameProfile(profileTag, profile);
        blockEntityTag.put(PLUSHIE_OWNER, profileTag);
    }

    public static void writeOwnerStringToBlockEntityTag(CompoundTag blockEntityTag, String ownerName) {
        if (blockEntityTag == null || ownerName == null) return;
        blockEntityTag.putString(PLUSHIE_OWNER, ownerName);
    }

    public static void writeLoreToBlockEntityTag(CompoundTag blockEntityTag, List<String> lore) {
        if (blockEntityTag == null || lore == null || lore.isEmpty()) return;
        ListTag loreList = new ListTag();
        for (String line : lore) {
            loreList.add(StringTag.valueOf(line));
        }
        blockEntityTag.put(PLUSHIE_LORE, loreList);
    }
}
