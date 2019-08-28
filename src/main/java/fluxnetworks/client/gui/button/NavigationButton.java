package fluxnetworks.client.gui.button;

import fluxnetworks.FluxNetworks;
import fluxnetworks.client.gui.main.GuiFluxHome;
import fluxnetworks.client.gui.tab.*;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class NavigationButton extends GuiButtonCore {

    private static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID + ":textures/gui/navigation_button.png");

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
        mc.getTextureManager().bindTexture(TEXTURES);
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
                return "Home";
            case 1:
                return "Network Selection";
            case 2:
                return "Network Transfers";
            case 3:
                return "Network Connections";
            case 4:
                return "Network Statistics";
            case 5:
                return "Network Members";
            case 6:
                return "Network Settings";
            case 7:
                return "Create New Network";
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
                FMLCommonHandler.instance().showGuiScreen(new GuiTabConnection(player, tileEntity));
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
