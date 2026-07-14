package net.syrupstudios.plushiefriends;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.syrupstudios.plushiefriends.block.DynamicPlushieBlock;
import net.syrupstudios.plushiefriends.block.entity.DynamicPlushieBlockEntity;
import net.syrupstudios.plushiefriends.data.PlushieDataManager;
import net.syrupstudios.plushiefriends.loot.SetPlushieFunction;
import net.syrupstudios.plushiefriends.util.PlushieNbtHelper;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class PlushieFriends implements ModInitializer {
	public static final String MOD_ID = "plushie_friends";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static LootItemFunctionType SET_PLUSHIE_FUNCTION;

	public static final DynamicPlushieBlock PLUSHIE_BLOCK = new DynamicPlushieBlock(
			BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()
	);

	public static final BlockItem PLUSHIE_ITEM = new BlockItem(PLUSHIE_BLOCK, new FabricItemSettings().stacksTo(1)) {
		@Override
		protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
			boolean updated = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
			applyCachedOwner(level, pos, state);
			return updated;
		}

		@Override
		public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
			super.inventoryTick(stack, level, entity, slotId, isSelected);
			if (!level.isClientSide) {
				applyCachedOwner(stack);
			}
		}

		@Override
		public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
			super.appendHoverText(stack, level, tooltip, flag);

			if (stack.hasTag()) {
				CompoundTag tag = stack.getTag();
				if (tag != null && tag.contains(PlushieNbtHelper.BLOCK_ENTITY_TAG, PlushieNbtHelper.TAG_COMPOUND)) {
					CompoundTag blockEntityTag = tag.getCompound(PlushieNbtHelper.BLOCK_ENTITY_TAG);

					String ownerName = PlushieNbtHelper.getOwnerNameFromBlockEntityTag(blockEntityTag);
					if (!ownerName.isEmpty()) {
						tooltip.add(Component.literal(ownerName).withStyle(ChatFormatting.AQUA));
					}

					List<String> lore = PlushieNbtHelper.getLoreFromBlockEntityTag(blockEntityTag);
					for (String line : lore) {
						tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
					}
				}
			}
		}
	};

	private static void applyCachedOwner(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(PlushieNbtHelper.BLOCK_ENTITY_TAG, PlushieNbtHelper.TAG_COMPOUND)) {
			return;
		}

		CompoundTag blockEntityTag = tag.getCompound(PlushieNbtHelper.BLOCK_ENTITY_TAG);
		String ownerName = PlushieNbtHelper.getOwnerNameFromBlockEntityTag(blockEntityTag);
		if (ownerName.isEmpty()) {
			return;
		}

		if (blockEntityTag.contains(PlushieNbtHelper.PLUSHIE_OWNER, PlushieNbtHelper.TAG_COMPOUND)) {
			CompoundTag ownerCompound = blockEntityTag.getCompound(PlushieNbtHelper.PLUSHIE_OWNER);
			if (ownerCompound.contains("Properties", PlushieNbtHelper.TAG_COMPOUND)) {
				return;
			}
		}

		GameProfile cachedProfile = PlushieProfileManager.getCachedProfile(ownerName);
		if (cachedProfile == null || !cachedProfile.getProperties().containsKey("textures")) {
			if (PlushieProfileManager.shouldAttemptResolution(ownerName)) {
				PlushieProfileManager.resolveProfileAsync(ownerName, profile -> {});
			}
			return;
		}

		PlushieNbtHelper.writeOwnerToBlockEntityTag(blockEntityTag, cachedProfile);
	}

	private static void applyCachedOwner(Level level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof DynamicPlushieBlockEntity plushieBlockEntity)) {
			return;
		}

		GameProfile owner = plushieBlockEntity.getOwner();
		if (owner == null || owner.getProperties().containsKey("textures")) {
			return;
		}

		GameProfile cachedProfile = PlushieProfileManager.getCachedProfile(owner.getName());
		if (cachedProfile == null || !cachedProfile.getProperties().containsKey("textures")) {
			return;
		}

		plushieBlockEntity.setOwner(cachedProfile);
	}

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, "plushie"), PLUSHIE_BLOCK);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "plushie"), PLUSHIE_ITEM);

		DynamicPlushieBlockEntity.TYPE = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				new ResourceLocation(MOD_ID, "plushie_be"),
				net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(DynamicPlushieBlockEntity::new, PLUSHIE_BLOCK).build()
		);

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PlushieDataManager());

		SET_PLUSHIE_FUNCTION = Registry.register(
				BuiltInRegistries.LOOT_FUNCTION_TYPE,
				new ResourceLocation(MOD_ID, "set_plushie"),
				new LootItemFunctionType(new SetPlushieFunction.Serializer())
		);

		LOGGER.info("Plushie Friends initialized successfully!");
	}
}
