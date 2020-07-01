package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.EmptyModelData;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class FluxStorageItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        int colour = FluxColorHandler.NO_NETWORK_COLOR;
        int energy = 0;
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if(tag != null) {
            colour = FluxColorHandler.getOrRequestNetworkColor(tag.getInt(FluxNetworkData.NETWORK_ID));
            energy = tag.getInt("energy");
        }

        FluxStorageBlock block = (FluxStorageBlock) Block.getBlockFromItem(stack.getItem());
        BlockState renderState = block.getDefaultState();

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel ibakedmodel = dispatcher.getModelForState(renderState);

        float r = ScreenUtils.getRed(colour), g = ScreenUtils.getGreen(colour), b = ScreenUtils.getBlue(colour);
        dispatcher.getBlockModelRenderer().renderModel(matrix.getLast(), buffer.getBuffer(RenderTypeLookup.getRenderType(renderState)), renderState, ibakedmodel, r, g, b, light, overlay, EmptyModelData.INSTANCE);
        //TODO minor issue - the renderer culls parts of the block model, could it have something to do with the Renderers render type.
        FluxStorageTileRenderer.render(matrix, buffer, overlay, energy, block.getMaxStorage(), colour);
    }
}
