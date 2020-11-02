package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

import javax.annotation.Nonnull;

public class FluxStorageTileRenderer extends TileEntityRenderer<TileFluxStorage> {

    private static final float START_X = 2.0f / 16.0f;
    private static final float START_Y = 2.0f / 16.0f;
    private static final float OFFSET_Z = 1.0f / 16.0f;
    private static final float WIDTH = 12.0f / 16.0f;
    private static final float HEIGHT = 13.0f / 16.0f;

    private static final float ALPHA = 150.0f / 255.0f;

    private static final int FULL_LIGHT = 0x00f000f0; // (240, 240)

    public FluxStorageTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(@Nonnull TileFluxStorage tile, float partialTicks, @Nonnull MatrixStack matrixStackIn,
                       @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int color = tile.clientColor;
        float r = ScreenUtils.getRed(color);
        float g = ScreenUtils.getGreen(color);
        float b = ScreenUtils.getBlue(color);
        render(matrixStackIn, bufferIn.getBuffer(FluxStorageRenderType.getType()), r, g, b,
                combinedOverlayIn, tile.getTransferBuffer(), tile.getMaxTransferLimit());
    }

    static void render(MatrixStack matrix, IVertexBuilder builder, float r, float g, float b, int overlay, long energy, long capacity) {
        if (energy <= 0 || capacity <= 0) {
            return;
        }
        float fillPercentage = (float) Math.min((double) energy / capacity, 1.0);
        float renderHeight = HEIGHT * fillPercentage;

        renderSide(matrix, builder, Direction.NORTH, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b, overlay, fillPercentage);
        renderSide(matrix, builder, Direction.SOUTH, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b, overlay, fillPercentage);
        renderSide(matrix, builder, Direction.EAST, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b, overlay, fillPercentage);
        renderSide(matrix, builder, Direction.WEST, START_X, START_Y, OFFSET_Z, WIDTH, renderHeight, r, g, b, overlay, fillPercentage);
        if (fillPercentage < 1.0f) {
            renderSide(matrix, builder, Direction.DOWN, 1.0f / 16.0f, 1.0f / 16.0f, OFFSET_Z + HEIGHT - renderHeight,
                    14.0f / 16.0f, 14.0f / 16.0f, r, g, b, overlay, fillPercentage);
        }
    }

    private static void renderSide(@Nonnull MatrixStack matrix, @Nonnull IVertexBuilder builder, @Nonnull Direction dir,
                           float x, float y, float z, float width, float height,
                           float r, float g, float b, int overlay, float fillPercentage) {
        float minU = 0, minV = 0, maxU = 1, maxV = 1 * fillPercentage;
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.rotate(dir.getRotation());
        matrix.rotate(new Quaternion((float) (Math.PI * -0.5), 0, 0, false));
        matrix.translate(-0.5, -0.5, -0.5);
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        Matrix3f normal = matrix.getLast().getNormal();
        builder.pos(matrix4f, x, y + height, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .tex(minU, maxV).overlay(overlay).lightmap(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        builder.pos(matrix4f, x + width, y + height, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .tex(maxU, maxV).overlay(overlay).lightmap(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        builder.pos(matrix4f, x + width, y, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .tex(maxU, minV).overlay(overlay).lightmap(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        builder.pos(matrix4f, x, y, z).color(r, g, b, FluxStorageTileRenderer.ALPHA)
                .tex(minU, minV).overlay(overlay).lightmap(FluxStorageTileRenderer.FULL_LIGHT).normal(normal, 0, 0, 0).endVertex();
        matrix.pop();
    }
}
