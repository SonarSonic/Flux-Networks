package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
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
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FluxStorageItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, ItemCameraTransforms.TransformType transformType, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        int color = FluxColorHandler.NO_NETWORK_COLOR;
        int energy = FluxConstants.INVALID_NETWORK_COLOR;
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if (tag != null) {
            color = FluxClientCache.getNetwork(tag.getInt(FluxNetworkData.NETWORK_ID)).getNetworkColor();
            energy = tag.getInt("energy");
        }

        FluxStorageBlock block = (FluxStorageBlock) Block.getBlockFromItem(stack.getItem());
        BlockState renderState = block.getDefaultState();

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel ibakedmodel = dispatcher.getModelForState(renderState);

        float r = ScreenUtils.getRed(color), g = ScreenUtils.getGreen(color), b = ScreenUtils.getBlue(color);
        dispatcher.getBlockModelRenderer().renderModel(matrix.getLast(), buffer.getBuffer(RenderType.getCutout()), renderState, ibakedmodel, r, g, b, light, overlay, EmptyModelData.INSTANCE);
        //TODO minor issue - the renderer culls parts of the block model, could it have something to do with the Renderers render type.
        FluxStorageTileRenderer.render(matrix, buffer, overlay, energy, block.getMaxStorage(), color);
    }
}
