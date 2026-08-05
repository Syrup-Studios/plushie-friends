package net.syrupstudios.plushiefriends.client;

//? if neoforge {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class NeoForgePlushieItemRenderer extends BlockEntityWithoutLevelRenderer {
    public NeoForgePlushieItemRenderer() {
        super(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay
    ) {
        PlushieFriendsClient.renderItem(stack, displayContext, poseStack, buffers, light, overlay);
    }
}
*///?}
