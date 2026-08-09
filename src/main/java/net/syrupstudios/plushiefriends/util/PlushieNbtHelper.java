package net.syrupstudios.plushiefriends.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
//? if >=1.21
/*import net.minecraft.core.UUIDUtil;*/
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

//? if >=1.21 {
/*import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.component.ResolvableProfile;
import java.util.UUID;
*///?}

public final class PlushieNbtHelper {
    /** Transitional alias for the legacy item container. */
    public static final String BLOCK_ENTITY_TAG = PlushieDataContract.LEGACY_BLOCK_ENTITY_TAG;
    public static final String PLUSHIE_OWNER = PlushieDataContract.OWNER;
    public static final String PLUSHIE_LORE = PlushieDataContract.LORE;
    public static final String PROFILE_NAME = "Name";
    public static final String PROFILE_NAME_1_21 = "name";

    public static final int TAG_STRING = 8;
    public static final int TAG_LIST = 9;
    public static final int TAG_COMPOUND = 10;

    private PlushieNbtHelper() {}

    @Nullable
    public static GameProfile getOwnerFromRoot(CompoundTag rootTag) {
        if (rootTag == null) return null;

        if (rootTag.contains(PLUSHIE_OWNER)) {
            return getOwnerFromBlockEntityTag(rootTag);
        }

        if (rootTag.contains(BLOCK_ENTITY_TAG, TAG_COMPOUND)) {
            return getOwnerFromBlockEntityTag(rootTag.getCompound(BLOCK_ENTITY_TAG));
        }
        return null;
    }

    @Nullable
    public static GameProfile getOwnerFromBlockEntityTag(CompoundTag blockEntityTag) {
        if (blockEntityTag == null) return null;

        if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_COMPOUND)) {
            //? if >=1.21 {
            /*CompoundTag ownerTag = blockEntityTag.getCompound(PLUSHIE_OWNER);
            if (ownerTag.contains(PROFILE_NAME)
                    || ownerTag.contains("Id")
                    || ownerTag.contains("Properties")) {
                return readLegacyGameProfile(ownerTag);
            }
            return ResolvableProfile.CODEC
                    .parse(NbtOps.INSTANCE, ownerTag)
                    .result()
                    .map(ResolvableProfile::gameProfile)
                    .orElse(null);
            *///?} else
            return NbtUtils.readGameProfile(blockEntityTag.getCompound(PLUSHIE_OWNER));
        } else if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_STRING)) {
            String name = blockEntityTag.getString(PLUSHIE_OWNER);
            if (!name.isEmpty()) {
                //? if >=1.21 {
                /*return UUIDUtil.createOfflineProfile(name);
                *///?} else
                return new GameProfile(null, name);
            }
        }
        return null;
    }

    //? if >=1.21 {
    /*// Reads the profile format written by Minecraft 1.20 and earlier.
    @Nullable
    private static GameProfile readLegacyGameProfile(CompoundTag profileTag) {
        try {
            String name = profileTag.contains(PROFILE_NAME, TAG_STRING)
                    ? profileTag.getString(PROFILE_NAME)
                    : null;
            UUID id = profileTag.hasUUID("Id") ? profileTag.getUUID("Id") : null;
            if ((name == null || name.isEmpty()) && id == null) {
                return null;
            }

            GameProfile profile = new GameProfile(id, name);
            if (profileTag.contains("Properties", TAG_COMPOUND)) {
                CompoundTag propertiesTag = profileTag.getCompound("Properties");
                for (String propertyName : propertiesTag.getAllKeys()) {
                    ListTag properties = propertiesTag.getList(propertyName, TAG_COMPOUND);
                    for (int i = 0; i < properties.size(); i++) {
                        CompoundTag propertyTag = properties.getCompound(i);
                        String value = propertyTag.getString("Value");
                        Property property = propertyTag.contains("Signature", TAG_STRING)
                                ? new Property(propertyName, value, propertyTag.getString("Signature"))
                                : new Property(propertyName, value);
                        profile.getProperties().put(propertyName, property);
                    }
                }
            }
            return profile;
        } catch (RuntimeException exception) {
            return null;
        }
    }
    *///?}

    public static String getOwnerNameFromBlockEntityTag(CompoundTag blockEntityTag) {
        if (blockEntityTag == null || !blockEntityTag.contains(PLUSHIE_OWNER)) return "";

        if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_COMPOUND)) {
            CompoundTag ownerTag = blockEntityTag.getCompound(PLUSHIE_OWNER);
            if (ownerTag.contains(PROFILE_NAME, TAG_STRING)) {
                return ownerTag.getString(PROFILE_NAME);
            }
            if (ownerTag.contains(PROFILE_NAME_1_21, TAG_STRING)) {
                return ownerTag.getString(PROFILE_NAME_1_21);
            }
        } else if (blockEntityTag.contains(PLUSHIE_OWNER, TAG_STRING)) {
            return blockEntityTag.getString(PLUSHIE_OWNER);
        }
        return "";
    }

    public static String getOwnerNameFromRoot(CompoundTag rootTag) {
        if (rootTag == null) return "";

        if (rootTag.contains(PLUSHIE_OWNER)) {
            return getOwnerNameFromBlockEntityTag(rootTag);
        }

        if (rootTag.contains(BLOCK_ENTITY_TAG, TAG_COMPOUND)) {
            return getOwnerNameFromBlockEntityTag(rootTag.getCompound(BLOCK_ENTITY_TAG));
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

    public static List<String> getLoreFromRoot(CompoundTag rootTag) {
        if (rootTag == null) return new ArrayList<>();

        if (rootTag.contains(PLUSHIE_LORE, TAG_LIST)) {
            return getLoreFromBlockEntityTag(rootTag);
        }
        if (rootTag.contains(BLOCK_ENTITY_TAG, TAG_COMPOUND)) {
            return getLoreFromBlockEntityTag(rootTag.getCompound(BLOCK_ENTITY_TAG));
        }
        return new ArrayList<>();
    }

    public static boolean hasLoreInRoot(CompoundTag rootTag) {
        if (rootTag == null) return false;
        if (rootTag.contains(PLUSHIE_LORE, TAG_LIST)) return true;
        return rootTag.contains(BLOCK_ENTITY_TAG, TAG_COMPOUND)
                && rootTag.getCompound(BLOCK_ENTITY_TAG).contains(PLUSHIE_LORE, TAG_LIST);
    }

    /**
     * Moves plushie fields out of the legacy {@code BlockEntityTag} container.
     * Canonical root fields always win when both representations are present.
     *
     * @return {@code true} when legacy plushie data was removed
     */
    public static boolean migrateLegacyItemData(CompoundTag rootTag) {
        if (rootTag == null || !rootTag.contains(BLOCK_ENTITY_TAG, TAG_COMPOUND)) {
            return false;
        }

        CompoundTag legacyTag = rootTag.getCompound(BLOCK_ENTITY_TAG);
        boolean migrated = migrateLegacyField(rootTag, legacyTag, PLUSHIE_OWNER);
        migrated |= migrateLegacyField(rootTag, legacyTag, PLUSHIE_LORE);

        if (legacyTag.isEmpty()) {
            rootTag.remove(BLOCK_ENTITY_TAG);
        }
        return migrated;
    }

    private static boolean migrateLegacyField(CompoundTag rootTag, CompoundTag legacyTag, String field) {
        Tag legacyValue = legacyTag.get(field);
        if (legacyValue == null) {
            return false;
        }

        if (!rootTag.contains(field)) {
            rootTag.put(field, legacyValue.copy());
        }
        legacyTag.remove(field);
        return true;
    }

    public static void writeOwnerToBlockEntityTag(CompoundTag blockEntityTag, GameProfile profile) {
        if (blockEntityTag == null || profile == null) return;
        //? if >=1.21 {
        /*ResolvableProfile.CODEC
                .encodeStart(NbtOps.INSTANCE, new ResolvableProfile(profile))
                .result()
                .ifPresent(tag -> blockEntityTag.put(PLUSHIE_OWNER, tag));
        *///?} else {
        CompoundTag profileTag = new CompoundTag();
        NbtUtils.writeGameProfile(profileTag, profile);
        blockEntityTag.put(PLUSHIE_OWNER, profileTag);
        //?}
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
