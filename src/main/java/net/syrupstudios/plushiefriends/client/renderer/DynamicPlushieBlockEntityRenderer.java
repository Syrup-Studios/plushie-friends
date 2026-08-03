package net.syrupstudios.plushiefriends.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.syrupstudios.plushiefriends.PlushieFriends;
import net.syrupstudios.plushiefriends.block.DynamicPlushieBlock;
import net.syrupstudios.plushiefriends.block.entity.DynamicPlushieBlockEntity;
import net.syrupstudios.plushiefriends.client.PlushieProfileCache;

public class DynamicPlushieBlockEntityRenderer implements BlockEntityRenderer<DynamicPlushieBlockEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            PlushieFriends.id("plushie"), "main"
    );

    private final PlushieModel model;

    public DynamicPlushieBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new PlushieModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    public void render(DynamicPlushieBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        GameProfile owner = blockEntity.getOwner();
        if ((owner == null || !owner.getProperties().containsKey("textures")) && !blockEntity.isSafeToForceRender()) return;

        BlockState state = blockEntity.getBlockState();
        int rotation = DynamicPlushieBlock.getRotation(state);

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.0D, 0.5D);

        float rotationAngle = -RotationSegment.convertToDegrees(rotation);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        float globalScale = 0.5F;
        poseStack.scale(globalScale, globalScale, globalScale);

        PlushieProfileCache.Skin skin = PlushieProfileCache.getSkin(blockEntity.getOwner());

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(skin.textureLocation()));
        this.model.render(poseStack, vertexConsumer, combinedLight, combinedOverlay, skin.slim());

        poseStack.popPose();
    }
}
