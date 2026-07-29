package net.syrupstudios.plushiefriends.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
//?} else if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.client.renderer.DynamicPlushieBlockEntityRenderer;
import net.syrupstudios.plushiefriends.client.renderer.PlushieModel;
import net.syrupstudios.plushiefriends.item.PlushieItemData;
import net.syrupstudios.plushiefriends.util.PlushieNbtHelper;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;

//? if neoforge
/*@EventBusSubscriber(modid = PlushieFriends.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)*/
public final class PlushieFriendsClient
        //? if fabric
        implements ClientModInitializer
{
    private static PlushieModel itemModel;

    //? if fabric {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(
                DynamicPlushieBlockEntityRenderer.LAYER_LOCATION,
                PlushieModel::createLayer
        );
        BlockEntityRendererRegistry.register(
                PlushieFriends.PLUSHIE_BLOCK_ENTITY,
                DynamicPlushieBlockEntityRenderer::new
        );
        BuiltinItemRendererRegistry.INSTANCE.register(
                PlushieFriends.PLUSHIE_ITEM,
                (stack, displayContext, poseStack, buffers, light, overlay) ->
                        renderItem(stack, poseStack, buffers, light, overlay)
        );
    }
    //?} else if neoforge {
    /*@SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DynamicPlushieBlockEntityRenderer.LAYER_LOCATION, PlushieModel::createLayer);
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                PlushieFriends.PLUSHIE_BLOCK_ENTITY,
                DynamicPlushieBlockEntityRenderer::new
        );
    }
    *///?}

    public static void renderItem(
            ItemStack stack, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay
    ) {
        if (itemModel == null) {
            itemModel = new PlushieModel(
                    Minecraft.getInstance().getEntityModels()
                            .bakeLayer(DynamicPlushieBlockEntityRenderer.LAYER_LOCATION)
            );
        }

        CompoundTag tag = PlushieItemData.read(stack);
        GameProfile owner = PlushieNbtHelper.getOwnerFromRoot(tag);
        if (owner != null && !owner.getProperties().containsKey("textures")) {
            GameProfile cached = PlushieProfileManager.getCachedProfile(owner.getName());
            if (cached != null && cached.getProperties().containsKey("textures")) {
                owner = cached;
            }
        }

        PlushieProfileCache.Skin skin = PlushieProfileCache.getSkin(owner);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);

        VertexConsumer vertices = buffers.getBuffer(RenderType.entityCutoutNoCull(skin.textureLocation()));
        itemModel.render(poseStack, vertices, light, overlay, skin.slim());
        poseStack.popPose();
    }
}
