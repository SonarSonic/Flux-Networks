package fluxnetworks.client.gui;

import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.ConnectionTransferHandler;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
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

    public void renderTransfer(ConnectionTransferHandler handler, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);

        fontRenderer.drawString(getTransferInfo(tileEntity.getConnectionType(), tileEntity.getNetwork().getSetting(NetworkSettings.NETWORK_ENERGY), handler.getChange()), x, y, color);
        fontRenderer.drawString("Buffer: " + TextFormatting.BLUE + FluxUtils.format(handler.getBuffer(), FluxUtils.TypeNumberFormat.COMMAS, tileEntity.getNetwork().getSetting(NetworkSettings.NETWORK_ENERGY).getStorageSuffix()), x, y + 10, 0xffffff);

        GlStateManager.enableDepth();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        if (tileEntity.getConnectionType().canAddEnergy()) {
            RenderHelper.enableStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(FluxUtils.FLUX_PLUG, x - 20, y - 1);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, FluxUtils.FLUX_PLUG, x - 20, y - 1, "");
            RenderHelper.disableStandardItemLighting();
        }
        if (tileEntity.getConnectionType().canRemoveEnergy()) {
            RenderHelper.enableStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(FluxUtils.FLUX_POINT, x - 20, y - 1);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, FluxUtils.FLUX_POINT, x - 20, y - 1, "");
            RenderHelper.disableStandardItemLighting();
        }
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
        GlStateManager.disableDepth();

        GlStateManager.popMatrix();
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
        return "";
    }
}
