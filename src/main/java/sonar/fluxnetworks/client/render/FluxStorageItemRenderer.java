package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FluxStorageItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, @Nonnull ItemCameraTransforms.TransformType transformType, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        int color; // 0xRRGGBB
        long energy;
        boolean syncedOnly = false;
        CompoundNBT tag = stack.getChildTag(FluxConstants.TAG_FLUX_DATA);
        if (tag != null) {
            if (tag.getBoolean(FluxConstants.FLUX_COLOR)) {
                // GUI display
                Screen screen = Minecraft.getInstance().currentScreen;
                if (screen instanceof GuiFluxCore) {
                    GuiFluxCore gui = (GuiFluxCore) screen;
                    color = gui.network.getNetworkColor();
                } else {
                    color = FluxConstants.INVALID_NETWORK_COLOR;
                }
            } else if (tag.contains(FluxConstants.CLIENT_COLOR)) {
                // TheOneProbe
                color = tag.getInt(FluxConstants.CLIENT_COLOR);
                syncedOnly = true;
            } else {
                // ItemStack inventory
                color = FluxClientCache.getNetwork(tag.getInt(FluxConstants.NETWORK_ID)).getNetworkColor();
            }
            energy = tag.getLong(FluxConstants.ENERGY);
        } else {
            color = FluxConstants.INVALID_NETWORK_COLOR;
            energy = 0;
        }

        FluxStorageBlock block = (FluxStorageBlock) Block.getBlockFromItem(stack.getItem());
        BlockState renderState = block.getDefaultState();

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel ibakedmodel = dispatcher.getModelForState(renderState);

        float r = ScreenUtils.getRed(color), g = ScreenUtils.getGreen(color), b = ScreenUtils.getBlue(color);
        dispatcher.getBlockModelRenderer()
                .renderModel(matrix.getLast(), buffer.getBuffer(Atlases.getCutoutBlockType()),
                        renderState, ibakedmodel, r, g, b, light, overlay, EmptyModelData.INSTANCE);
        FluxStorageTileRenderer.render(matrix, buffer.getBuffer(FluxStorageRenderType.getDiffuse(syncedOnly)),
                r, g, b, overlay, energy, block.getEnergyCapacity());
    }
}
