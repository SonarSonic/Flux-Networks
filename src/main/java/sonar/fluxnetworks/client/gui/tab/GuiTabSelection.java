package sonar.fluxnetworks.client.gui.tab;

import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.gui.basic.GuiDraw;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.popups.GuiPopNetworkPassword;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.item.ItemConfigurator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.Comparator;

public class GuiTabSelection extends GuiTabPages<IFluxNetwork> {

    public IFluxNetwork selectedNetwork;

    protected int timer2;

    public GuiTabSelection(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 12;
        elementWidth = 146;
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_SELECTION;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(elements.size() == 0) {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_NETWORK.t(), FluxTranslate.TAB_CREATE.t());
        } else {
            String amount = FluxTranslate.TOTAL.t() + ": " + elements.size();
            fontRenderer.drawString(amount, 158 - fontRenderer.getStringWidth(amount), 10, 0xffffff);
            fontRenderer.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + sortType.getTranslatedName(), 19, 10, 0xffffff);
            if (!hasActivePopup()) {
                drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 150, 0xffffff);
            }
        }
    }

    @Override
    protected void onElementClicked(IFluxNetwork element, int mouseButton) {
        if(mouseButton == 0) {
            selectedNetwork = element;
            setConnectedNetwork(element.getNetworkID(), "");
         }
    }

    @Override
    public void initGui() {
        super.initGui();
        configureNavigationButtons(EnumNavigationTabs.TAB_SELECTION, navigationTabs);
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
            drawTexturedModalRect(x, y, 0, 16, elementWidth, elementHeight);
            mc.fontRenderer.drawString(text, x + 4, y + 2, 0xffffff);
        } else {
            GlStateManager.color(f * 0.75f, f1 * 0.75f, f2 * 0.75f);
            drawTexturedModalRect(x, y, 0, 16, elementWidth, elementHeight);
            mc.fontRenderer.drawString(text, x + 4, y + 2, 0x404040);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(IFluxNetwork element, int mouseX, int mouseY) {
        if(hasActivePopup())
            return;
        /*GlStateManager.pushMatrix();
        GlStateManager.popMatrix();*/
    }
    @Override
    public void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            if (mouseX > guiLeft + 45 && mouseX < guiLeft + 75 && mouseY > guiTop + 10 && mouseY < getGuiTop() + 17) {
                sortType = FluxUtils.incrementEnum(sortType, SortType.values());
                sortGrids(sortType);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer2 == 0) {
            refreshPages(FluxNetworkCache.instance.getAllClientNetworks());
        }
        if(FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.SUCCESS) {
           closePopUp();
           if(connector instanceof ItemConfigurator.NetworkConnector){
               ItemConfigurator.NetworkConnector networkConnector = (ItemConfigurator.NetworkConnector)connector;
               networkConnector.network = selectedNetwork;
               networkConnector.networkID = selectedNetwork.getNetworkID();

               this.network = selectedNetwork;
               this.networkValid = !selectedNetwork.isInvalid();
           }
        }
        if(FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.PASSWORD_REQUIRE) {
            openPopUp(new GuiPopNetworkPassword(this, player, connector));
            FluxNetworks.proxy.setFeedback(EnumFeedbackInfo.NONE, true);
        }
        timer2++;
        timer2 %= 10;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        switch (sortType) {
            case ID:
                elements.sort(Comparator.comparing(IFluxNetwork::getNetworkID));
                refreshCurrentPageInternal();
                break;
            case NAME:
                elements.sort(Comparator.comparing(IFluxNetwork::getNetworkName));
                refreshCurrentPageInternal();
                break;
        }
    }
}
