package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import sonar.fluxnetworks.api.gui.EnumChargingTypes;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiDraw;
import sonar.fluxnetworks.client.gui.tab.GuiTabWireless;
import sonar.fluxnetworks.api.network.NetworkSettings;
import net.minecraft.client.Minecraft;

public class InventoryButton extends GuiButtonCore {

    public EnumChargingTypes chargeType;
    private int texX, texY;
    public GuiTabWireless host;

    public InventoryButton(EnumChargingTypes chargeType, GuiTabWireless host, int x, int y, int texX, int texY, int width, int height) {
        super(x, y, width, height, chargeType.ordinal());
        this.chargeType = chargeType;
        this.texX = texX;
        this.texY = texY;
        this.host = host;
        this.text = chargeType.getTranslatedName();
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int guiLeft, int guiTop) {
        //TODO MINOR - FIX FLICKERING, WHEN HOVERING
        int colour = host.network.getSetting(NetworkSettings.NETWORK_COLOR);
        ScreenUtils.INSTANCE.setGuiColouring(colour);
        boolean hover = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);

        mc.getTextureManager().bindTexture(ScreenUtils.INVENTORY);
        ScreenUtils.INSTANCE.blit(x, y, texX, texY + height * (host.settings[id] ? 1 : 0), width, height);

        if(hover) {
            mc.fontRenderer.drawString(text, x + (width - mc.fontRenderer.getStringWidth(text)) / 2 + 1, y - 9, 0xFFFFFF);
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
        ScreenUtils.INSTANCE.resetGuiColouring();
    }
}
