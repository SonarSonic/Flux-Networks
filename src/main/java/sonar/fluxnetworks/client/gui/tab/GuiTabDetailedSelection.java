package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;

public class GuiTabDetailedSelection extends GuiTabSelection {

    public GuiTabDetailedSelection(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 18;
        elementWidth = 146;
    }


    @Override
    public void renderElement(MatrixStack matrixStack, IFluxNetwork element, int x, int y) {
        GlStateManager.enableBlend();
        GlStateManager.enableAlphaTest();
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(ScreenUtils.GUI_BAR);

        int color = element.getNetworkColor();

        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        boolean selected = connector.getNetworkID() == element.getNetworkID();
        boolean isEncrypted = element.getNetworkSecurity().isEncrypted();

        if (isEncrypted) {
            if (selected) {
                blit(matrixStack, x + 131, y, 159, 16, 16, elementHeight);
            } else {
                blit(matrixStack, x + 131, y, 175, 16, 16, elementHeight);
            }
        }

        String text = element.getNetworkName();

        if (selected) {
            RenderSystem.color3f(f, f1, f2);
            blit(matrixStack, x, y, 0, 32, elementWidth, elementHeight);
            minecraft.fontRenderer.drawString(matrixStack, text, x + 4, y + 2, 0xffffff);
        } else {
            RenderSystem.color3f(f * 0.75f, f1 * 0.75f, f2 * 0.75f);
            blit(matrixStack, x, y, 0, 32, elementWidth, elementHeight);
            minecraft.fontRenderer.drawString(matrixStack, text, x + 4, y + 2, 0x404040);
        }

        matrixStack.push();
        matrixStack.scale(0.625f, 0.625f, 0.625f);
        font.drawString(matrixStack, FluxTranslate.CONNECTIONS.t() + ": " + element.getNetworkStats().getConnectionCount() + "  Avg: " + element.getNetworkStats().average_tick_micro + " " + "\u03BC" + "s/t  ", (int) ((x + 4) * 1.6), (int) ((y + 11) * 1.6), selected ? 0xffffff : 0x404040);
        matrixStack.pop();
    }

    @Override
    public void tick() {
        if (timer2 == 1) {
            PacketHandler.CHANNEL.sendToServer(new NetworkUpdateRequestPacket(current, NBTType.NETWORK_STATISTICS));
        }
        super.tick();
    }
}
