package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxTranslate;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.client.gui.basic.GuiDraw;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketNetworkUpdateRequest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTabDetailedSelection extends GuiTabSelection {

    public GuiTabDetailedSelection(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
        gridStartX = 15; gridStartY = 22;
        gridHeight = 19; gridPerPage = 7;
        elementHeight = 18; elementWidth = 146;
    }


    @Override
    public void renderElement(IFluxNetwork element, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(GuiDraw.GUI_BAR);

        int color = element.getSetting(NetworkSettings.NETWORK_COLOR);

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        boolean selected = connector.getNetworkID() == element.getNetworkID();
        boolean isEncrypted = element.getSetting(NetworkSettings.NETWORK_SECURITY).isEncrypted();

        if(isEncrypted) {
            if(selected) {
                drawTexturedModalRect(x + 131, y, 159, 16, 16, elementHeight);
            } else {
                drawTexturedModalRect(x + 131, y, 175, 16, 16, elementHeight);
            }
        }

        String text = element.getSetting(NetworkSettings.NETWORK_NAME);

        if(selected) {
            GlStateManager.color(f, f1, f2);
            drawTexturedModalRect(x, y, 0, 32, elementWidth, elementHeight);
            mc.fontRenderer.drawString(text, x + 4, y + 2, 0xffffff);
        } else {
            GlStateManager.color(f * 0.75f, f1 * 0.75f, f2 * 0.75f);
            drawTexturedModalRect(x, y, 0, 32, elementWidth, elementHeight);
            mc.fontRenderer.drawString(text, x + 4, y + 2, 0x404040);
        }

        GlStateManager.scale(0.625, 0.625, 0.625);
        fontRenderer.drawString(FluxTranslate.CONNECTIONS.t() +": " + element.getSetting(NetworkSettings.NETWORK_STATISTICS).getConnectionCount() + "  Avg: " + element.getSetting(NetworkSettings.NETWORK_STATISTICS).average_tick_micro + " μs/t  ", (int) ((x + 4) * 1.6), (int) ((y + 11) * 1.6), selected ? 0xffffff : 0x404040);
        GlStateManager.scale(1.6, 1.6, 1.6);

        GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        if (timer2 == 1) {
            PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(current, NBTType.NETWORK_STATISTICS));
        }
        super.updateScreen();
    }
}
