package sonar.flux.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.common.block.SonarBlock;
import sonar.flux.FluxNetworks;
import sonar.flux.common.block.FluxStorage;
import sonar.flux.common.item.ItemNetworkConnector;

@SideOnly(Side.CLIENT)
public class RenderFluxStorageItem extends TileEntityItemStackRenderer {

    public static final RenderFluxStorageItem INSTANCE = new RenderFluxStorageItem();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks){
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = blockrendererdispatcher.getModelForState(FluxNetworks.fluxStorage.getDefaultState());
        GlStateManager.translate(0.5, 0.5, 0.5);
        net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, FluxStorageModel.CURRENT_TRANSFORM, true);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        FluxStorage block = (FluxStorage) Block.getBlockFromItem(stack.getItem());
        NBTTagCompound tag = stack.getSubCompound(SonarBlock.DROP_TAG_NAME);
        if(tag != null) {
            int colour = FluxColourHandler.getOrRequestNetworkColour(tag.getInteger(ItemNetworkConnector.NETWORK_ID_TAG));
            RenderFluxStorageTile.render(tag.getInteger("energy"), block.getMaxStorage(), colour, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        }
    }
}
