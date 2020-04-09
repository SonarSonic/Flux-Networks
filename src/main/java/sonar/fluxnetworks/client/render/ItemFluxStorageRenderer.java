package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.item.ItemStack;

public class ItemFluxStorageRenderer extends ItemStackTileEntityRenderer {

    public static final ItemFluxStorageRenderer INSTANCE = new ItemFluxStorageRenderer();

    /* TODO FIX  FLUX STORAGE RENDERER
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel model = blockrendererdispatcher.getModelForState(Block.getBlockFromItem(stack.getItem()).getDefaultState());
        GlStateManager.translated(0.5, 0.5, 0.5);
        net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, FluxStorageModel.CURRENT_TRANSFORM, true);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, model);
        GlStateManager.translated(-0.5, -0.5, -0.5);

        FluxStorageBlock block = (FluxStorageBlock) Block.getBlockFromItem(stack.getItem());
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if(tag != null) {
            int colour = FluxColorHandler.getOrRequestNetworkColor(tag.getInt(FluxNetworkData.NETWORK_ID));
            TileFluxStorageRenderer.render(tag.getInt("energy"), block.getMaxStorage(), colour, 0.0D, 0.0D, 0.0D);
        }
    }

     */
}
