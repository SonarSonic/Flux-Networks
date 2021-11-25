package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import sonar.fluxnetworks.common.blockentity.FluxStorageEntity;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class FluxStorageTileRenderer implements BlockEntityRenderer<FluxStorageEntity> {

    private static final float START_X = 2.0f / 16.0f;
    private static final float START_Y = 2.0f / 16.0f;
    private static final float OFFSET_Z = 1.0f / 16.0f;
    private static final float WIDTH = 12.0f / 16.0f;
    private static final float HEIGHT = 13.0f / 16.0f;

    private static final float ALPHA = 150.0f / 255.0f;

    private static final int FULL_LIGHT = 0x00f000f0; // (240, 240)

    public FluxStorageTileRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(@Nonnull FluxStorageEntity entity, float partialTick, @Nonnull PoseStack poseStack,
                       @Nonnull MultiBufferSource source, int packedLight, int packedOverlay) {
        int color = entity.mBlockTint;
        float r = FluxUtils.getRed(color);
        float g = FluxUtils.getGreen(color);
        float b = FluxUtils.getBlue(color);
        render(poseStack, source.getBuffer(FluxStorageRenderType.getType()), r, g, b,
                packedOverlay, entity.getTransferBuffer(), entity.getMaxTransferLimit());
    }

    static void render(PoseStack matrix, VertexConsumer builder, float r, float g, float b, int overlay, long energy,
                       long capacity) {
        if (energy <= 0 || capacity <= 0) {
            return;
        }
        float fillPercentage = (float) Math.min((double) energy / capacity, 1.0);
        float renderHeight = HEIGHT * fillPercentage;

        renderSide(matrix, builder, Direction.NORTH, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b,
                overlay, fillPercentage);
        renderSide(matrix, builder, Direction.SOUTH, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b,
                overlay, fillPercentage);
        renderSide(matrix, builder, Direction.EAST, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b, overlay
                , fillPercentage);
        renderSide(matrix, builder, Direction.WEST, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b, overlay
                , fillPercentage);
        if (fillPercentage < 1.0f) {
            renderSide(matrix, builder, Direction.DOWN, 1.0f / 16.0f, 1.0f / 16.0f, OFFSET_Z + HEIGHT - renderHeight,
                    14.0f / 16.0f, 14.0f / 16.0f, r, g, b, overlay, fillPercentage);
        }
    }

    private static void renderSide(@Nonnull PoseStack matrix, @Nonnull VertexConsumer builder, @Nonnull Direction dir,
                                   float x, float y, float z, float width, float height,
                                   float r, float g, float b, int overlay, float maxV) {
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(dir.getRotation());
        matrix.mulPose(new Quaternion((float) (Math.PI * -0.5), 0, 0, false));
        matrix.translate(-0.5, -0.5, -0.5);
        Matrix4f matrix4f = matrix.last().pose();
        Matrix3f normal = matrix.last().normal();
        builder.vertex(matrix4f, x, y + height, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .uv(0, maxV).overlayCoords(overlay).uv2(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        builder.vertex(matrix4f, x + width, y + height, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .uv(1, maxV).overlayCoords(overlay).uv2(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        builder.vertex(matrix4f, x + width, y, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .uv(1, 0).overlayCoords(overlay).uv2(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        builder.vertex(matrix4f, x, y, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .uv(0, 0).overlayCoords(overlay).uv2(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        matrix.popPose();
    }
}
