package fluxnetworks.client.gui.button;

import fluxnetworks.FluxTranslate;
import fluxnetworks.client.gui.basic.GuiCore;
import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.GuiFluxHome;
import fluxnetworks.client.gui.tab.*;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class NavigationButton extends GuiButtonCore {

    public int buttonNavigationId;
    public boolean isCurrentTab = false;

    public NavigationButton(int x, int y, int button) {
        super(x, y, 16, 16, 0);
        buttonNavigationId = button;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0f);
        mc.getTextureManager().bindTexture(GuiCore.BUTTONS);
        drawTexturedModalRect(x, y, 16 * buttonNavigationId, 16 * getCorrectHoverState(mc, mouseX, mouseY), 16, 16);

        if(isMouseHovered(mc, mouseX, mouseY)) {
            FontRenderer fontRenderer = mc.fontRenderer;
            String text = getTagName(buttonNavigationId);
            fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2 + 8, y - 10, 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

    private int getCorrectHoverState(Minecraft mc, int mouseX, int mouseY) {
        if(isCurrentTab)
            return 1;
        else
            return getHoverState(isMouseHovered(mc, mouseX, mouseY));
    }

    public NavigationButton setMain() {
        isCurrentTab = true;
        return this;
    }

    private String getTagName(int id) {

        switch(id) {
            case 0:
                return FluxTranslate.TAB_HOME;
            case 1:
                return FluxTranslate.TAB_SELECTION;
            case 2:
                return FluxTranslate.TAB_TRANSFER;
            case 3:
                return FluxTranslate.TAB_CONNECTION;
            case 4:
                return FluxTranslate.TAB_STATISTICS;
            case 5:
                return FluxTranslate.TAB_MEMBER;
            case 6:
                return FluxTranslate.TAB_SETTING;
            case 7:
                return FluxTranslate.TAB_CREATE;
        }
        return "";
    }

    public void switchTab(int destId, EntityPlayer player, TileFluxCore tileEntity) {

        switch (destId) {
            case 0:
                FMLCommonHandler.instance().showGuiScreen(new GuiFluxHome(player, tileEntity));
                break;
            case 1:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabSelection(player, tileEntity));
                break;
            case 2:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabTransfer(player, tileEntity));
                break;
            case 3:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabConnections(player, tileEntity));
                break;
            case 4:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabStatistics(player, tileEntity));
                break;
            case 5:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabMembers(player, tileEntity));
                break;
            case 6:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabSettings(player, tileEntity));
                break;
            case 7:
                FMLCommonHandler.instance().showGuiScreen(new GuiTabCreate(player, tileEntity));
                break;
        }

    }

}
