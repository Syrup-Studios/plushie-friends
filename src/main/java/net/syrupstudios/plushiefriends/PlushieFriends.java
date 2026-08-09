package net.syrupstudios.plushiefriends;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.syrupstudios.plushiefriends.block.DynamicPlushieBlock;
import net.syrupstudios.plushiefriends.block.entity.DynamicPlushieBlockEntity;
import net.syrupstudios.plushiefriends.item.PlushieBlockItem;
import net.syrupstudios.plushiefriends.loot.SetPlushieFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Loader-neutral mod content. Loader entry points own registration and lifecycle hooks.
 */
public final class PlushieFriends {
    public static final String MOD_ID = "plushie_friends";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static DynamicPlushieBlock PLUSHIE_BLOCK;
    public static BlockItem PLUSHIE_ITEM;
    public static BlockEntityType<DynamicPlushieBlockEntity> PLUSHIE_BLOCK_ENTITY;
    public static LootItemFunctionType SET_PLUSHIE_FUNCTION;

    private PlushieFriends() {
    }

    public static DynamicPlushieBlock createPlushieBlock() {
        if (PLUSHIE_BLOCK == null) {
            PLUSHIE_BLOCK = new DynamicPlushieBlock(
                    //? if >=1.21 {
                    /*BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion()
                    *///?} else
                    BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()
            );
        }
        return PLUSHIE_BLOCK;
    }

    public static BlockItem createPlushieItem() {
        if (PLUSHIE_ITEM == null) {
            PLUSHIE_ITEM = new PlushieBlockItem(
                    createPlushieBlock(),
                    new net.minecraft.world.item.Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
            );
        }
        return PLUSHIE_ITEM;
    }

    public static BlockEntityType<DynamicPlushieBlockEntity> createPlushieBlockEntity() {
        if (PLUSHIE_BLOCK_ENTITY == null) {
            PLUSHIE_BLOCK_ENTITY =
                    new BlockEntityType<>(DynamicPlushieBlockEntity::new, Set.of(createPlushieBlock()), null);
        }
        return PLUSHIE_BLOCK_ENTITY;
    }

    public static LootItemFunctionType createSetPlushieFunction() {
        if (SET_PLUSHIE_FUNCTION == null) {
            SET_PLUSHIE_FUNCTION =
                    //? if >=1.21 {
                    /*new LootItemFunctionType(SetPlushieFunction.CODEC);
                    *///?} else
                    new LootItemFunctionType(new SetPlushieFunction.Serializer());
        }
        return SET_PLUSHIE_FUNCTION;
    }

    public static void createContent() {
        createPlushieBlock();
        createPlushieItem();
        createPlushieBlockEntity();
        createSetPlushieFunction();
    }

    public static ResourceLocation id(String path) {
        //? if >=1.21 {
        /*return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
        *///?} else
        return new ResourceLocation(MOD_ID, path);
    }

    public static void initialized(String loader) {
        LOGGER.info("Plushie Friends initialized on {}.", loader);
    }
}
