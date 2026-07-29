package net.syrupstudios.plushiefriends.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.plushiefriends.util.PlushieNbtHelper;

import java.util.function.Consumer;

//? if >=1.21 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
*///?}

/**
 * Adapts the version-specific item storage format to one logical NBT compound.
 */
public final class PlushieItemData {
    private PlushieItemData() {
    }

    public static CompoundTag read(ItemStack stack) {
        //? if >=1.21 {
        /*CompoundTag result = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag legacy = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        copyMissingField(result, legacy, PlushieNbtHelper.PLUSHIE_OWNER);
        copyMissingField(result, legacy, PlushieNbtHelper.PLUSHIE_LORE);
        ResolvableProfile profile = stack.get(DataComponents.PROFILE);
        if (!result.contains(PlushieNbtHelper.PLUSHIE_OWNER) && profile != null) {
            PlushieNbtHelper.writeOwnerToBlockEntityTag(result, profile.gameProfile());
        }
        return result;
        *///?} else {
        CompoundTag tag = stack.getTag();
        return tag == null ? new CompoundTag() : tag.copy();
        //?}
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
        CompoundTag tag = read(stack);
        updater.accept(tag);
        PlushieNbtHelper.migrateLegacyItemData(tag);

        //? if >=1.21 {
        /*CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        *///?} else {
        stack.setTag(tag);
        //?}
    }

    public static boolean hasData(ItemStack stack) {
        return !read(stack).isEmpty();
    }

    private static void copyMissingField(CompoundTag target, CompoundTag source, String field) {
        if (!target.contains(field) && source.contains(field) && source.get(field) != null) {
            target.put(field, source.get(field).copy());
        }
    }
}
