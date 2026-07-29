package net.syrupstudios.plushiefriends.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.plushiefriends.block.entity.DynamicPlushieBlockEntity;
import net.syrupstudios.plushiefriends.util.PlushieNbtHelper;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

//? if >=1.21 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
*///?}

//? if neoforge {
/*import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.syrupstudios.plushiefriends.client.NeoForgePlushieItemRenderer;
*///?}

public final class PlushieBlockItem extends BlockItem {
    public PlushieBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    //? if >=1.21 {
    /*@Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        ResolvableProfile profile = stack.get(DataComponents.PROFILE);
        if (profile != null && !profile.isResolved()) {
            profile.resolve().thenAcceptAsync(
                    resolved -> stack.set(DataComponents.PROFILE, resolved),
                    SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR
            );
        }
    }

    *///?}

    //? if neoforge {
    /*@Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final NeoForgePlushieItemRenderer renderer = new NeoForgePlushieItemRenderer();

            @Override
            public NeoForgePlushieItemRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    *///?}

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state
    ) {
        boolean updated = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        CompoundTag itemData = PlushieItemData.read(stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DynamicPlushieBlockEntity plushie) {
            updated |= plushie.applyItemData(itemData);
            applyCachedOwner(plushie);
        }
        return updated;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide) {
            return;
        }

        CompoundTag tag = PlushieItemData.read(stack);
        GameProfile owner = PlushieNbtHelper.getOwnerFromRoot(tag);
        if (owner == null || owner.getName() == null || owner.getName().isEmpty()
                || owner.getProperties().containsKey("textures")) {
            return;
        }

        GameProfile cached = PlushieProfileManager.getCachedProfile(owner.getName());
        if (cached != null && cached.getProperties().containsKey("textures")) {
            PlushieItemData.update(stack, data -> PlushieNbtHelper.writeOwnerToBlockEntityTag(data, cached));
        } else if (PlushieProfileManager.shouldAttemptResolution(owner.getName())) {
            PlushieProfileManager.resolveProfileAsync(owner.getName(), profile -> {});
        }
    }

    //? if >=1.21 {
    /*@Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        appendPlushieTooltip(stack, tooltip);
    }
    *///?} else {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendPlushieTooltip(stack, tooltip);
    }
    //?}

    private static void appendPlushieTooltip(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = PlushieItemData.read(stack);
        String ownerName = PlushieNbtHelper.getOwnerNameFromRoot(tag);
        if (!ownerName.isEmpty()) {
            tooltip.add(Component.literal(ownerName).withStyle(ChatFormatting.AQUA));
        }
        for (String line : PlushieNbtHelper.getLoreFromRoot(tag)) {
            tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
        }
    }

    private static void applyCachedOwner(DynamicPlushieBlockEntity plushie) {
        GameProfile owner = plushie.getOwner();
        if (owner == null || owner.getName() == null || owner.getProperties().containsKey("textures")) {
            return;
        }
        GameProfile cached = PlushieProfileManager.getCachedProfile(owner.getName());
        if (cached != null && cached.getProperties().containsKey("textures")) {
            plushie.setOwner(cached);
        }
    }
}
