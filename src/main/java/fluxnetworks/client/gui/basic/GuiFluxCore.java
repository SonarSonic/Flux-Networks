package fluxnetworks.client.gui.basic;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.ConnectionTransferHandler;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.FluxNetworkData;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiFluxCore extends GuiCore {

    public IFluxNetwork network;

    public GuiFluxCore(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        this.network = FluxNetworkCache.instance.getNetwork(tileEntity.networkID);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        super.drawPopupForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    public void renderNetwork(String name, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        GlStateManager.color(f, f1, f2);

        mc.getTextureManager().bindTexture(GUI_BAR);
        drawTexturedModalRect(x, y, 0, 0, 135, 12);
        fontRenderer.drawString(name, x + 4, y + 2, 0xffffff);

        GlStateManager.popMatrix();
    }

    public void renderTransfer(ITransferHandler handler, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);

        fontRenderer.drawString(getTransferInfo(tileEntity.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), handler.getChange()), x, y, color);
        fontRenderer.drawString("Buffer: " + TextFormatting.BLUE + FluxUtils.format(handler.getBuffer(), FluxUtils.TypeNumberFormat.COMMAS, network.getSetting(NetworkSettings.NETWORK_ENERGY), false), x, y + 10, 0xffffff);

        renderItemStack(tileEntity.getDisplayStack(), x - 20, y + 1);

        GlStateManager.popMatrix();
    }

    public void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.enableDepth();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;

        RenderHelper.enableStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, "");
        RenderHelper.disableStandardItemLighting();

        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
        GlStateManager.disableDepth();
    }

    public String getTransferInfo(IFluxConnector.ConnectionType type, EnergyType energyType, long change) {
        if(type.canAddEnergy()) {
            String b = FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            if(change == 0) {
                return "Input: " + TextFormatting.GOLD + b;
            } else {
                return "Input: " + TextFormatting.GREEN + "+" + b;
            }
        }
        if(type.canRemoveEnergy()) {
            String b = FluxUtils.format(-change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            if(change == 0) {
                return "Output: " + TextFormatting.GOLD + b;
            } else {
                return "Output: " + TextFormatting.RED + "-" + b;
            }
        }
        // Storage are inverted
        if(type == IFluxConnector.ConnectionType.STORAGE) {
            if(change == 0) {
                return "Change: " + TextFormatting.GOLD + change + energyType.getUsageSuffix();
            } else if(change > 0) {
                return "Change: " + TextFormatting.RED + "-" + FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            } else {
                return "Change: " + TextFormatting.GREEN + "+" + FluxUtils.format(-change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            }
        }
        return "";
    }
}
