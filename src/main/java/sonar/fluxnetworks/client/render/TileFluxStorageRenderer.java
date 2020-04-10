package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

public class TileFluxStorageRenderer extends TileEntityRenderer<TileFluxStorage> {

    public static final ResourceLocation ENERGY_TEXTURE = new ResourceLocation(FluxNetworks.MODID, "textures/model/flux_storage_energy.png");
    public static final float startX = 2F/16, startY = 2F/16, offsetZ = 1F/16, width = 12F/16, height = 13F/16;
    public static final float alpha = 150F / 255.0F;


    public TileFluxStorageRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileFluxStorage tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        render(partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, tile.energyStored, tile.maxEnergyStorage, tile.color | 0xff000000);
    }

    public static void render(float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int light, int overlay, int energyStored, int energyMax, int networkColour) {
        if (energyStored == 0 || energyMax == 0) {
            return;
        }
        float r = ScreenUtils.getRed(networkColour);
        float g = ScreenUtils.getGreen(networkColour);
        float b = ScreenUtils.getBlue(networkColour);
        float energyPercentage = ((float)energyStored)/energyMax;
        float renderHeight = height * energyPercentage;
        float renderWidth = width;

        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getEntityTranslucent(ENERGY_TEXTURE));
        renderSide(matrix, builder, Direction.NORTH, startX, startY, offsetZ, renderWidth, renderHeight, r, g, b, alpha, light, overlay, energyPercentage);
        renderSide(matrix, builder, Direction.SOUTH, startX, startY, offsetZ, renderWidth, renderHeight, r, g, b, alpha, light, overlay, energyPercentage);
        renderSide(matrix, builder, Direction.EAST, startX, startY, offsetZ, renderWidth, renderHeight, r, g, b, alpha, light, overlay, energyPercentage);
        renderSide(matrix, builder, Direction.WEST, startX, startY, offsetZ, renderWidth, renderHeight, r, g, b, alpha, light, overlay, energyPercentage);
        if(energyPercentage != 1) {
            renderSide(matrix, builder, Direction.DOWN, 1F / 16, 1F / 16, offsetZ + height - renderHeight, 14F / 16, 14F / 16, r, g, b, alpha, light, overlay, energyPercentage);
        }
    }

    public static void renderSide(MatrixStack matrix, IVertexBuilder builder, Direction dir, float x, float y, float z, float width, float height, float r, float g, float b, float a, int light, int overlay, float fillPercentage){
        float minU = 0, minV = 0, maxU = 1, maxV = 1 * fillPercentage;
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.rotate(dir.getRotation());
        matrix.rotate(new Quaternion(-90, 0, 0, true));
        matrix.translate(-0.5, -0.5, -0.5);
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        Matrix3f normal = matrix.getLast().getNormal();
        builder.pos(matrix4f, x, y + height, z).color(r, g, b, a).tex(minU, maxV).overlay(overlay).lightmap(light).normal(normal, 0, 0, 0).endVertex();
        builder.pos(matrix4f, x + width, y + height, z).color(r, g, b, a).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(normal, 0, 0, 0).endVertex();
        builder.pos(matrix4f, x + width, y, z).color(r, g, b, a).tex(maxU, minV).overlay(overlay).lightmap(light).normal(normal, 0, 0, 0).endVertex();
        builder.pos(matrix4f, x, y, z).color(r, g, b, a).tex(minU, minV).overlay(overlay).lightmap(light).normal(normal, 0, 0, 0).endVertex();
        matrix.pop();
    }

}
