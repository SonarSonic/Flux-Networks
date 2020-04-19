package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.popups.PopUpNetworkPassword;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import net.minecraft.util.text.TextFormatting;

import java.util.Comparator;

public class GuiTabSelection extends GuiTabPages<IFluxNetwork> {

    public InvisibleButton redirectButton;

    public IFluxNetwork selectedNetwork;

    protected int timer2;

    public GuiTabSelection(PlayerEntity player, INetworkConnector connector) {
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
            font.drawString(amount, 158 - font.getStringWidth(amount), 10, 0xffffff);
            font.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + sortType.getTranslatedName(), 19, 10, 0xffffff);
            if (!hasActivePopup()) {
                drawCenteredString(font, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 150, 0xffffff);
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
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_SELECTION, navigationTabs);
        if(!networkValid){
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20, EnumNavigationTabs.TAB_CREATE.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_CREATE, player, connector));
            addButton(redirectButton);
        }
    }

    @Override
    public void renderElement(IFluxNetwork element, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlphaTest();
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(ScreenUtils.GUI_BAR);

        int color = element.getSetting(NetworkSettings.NETWORK_COLOR);

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        boolean selected = connector.getNetworkID() == element.getNetworkID();
        boolean isEncrypted = element.getSetting(NetworkSettings.NETWORK_SECURITY).isEncrypted();

        if(isEncrypted) {
            if(selected) {
                blit(x + 131, y, 159, 16, 16, elementHeight);
            } else {
                blit(x + 131, y, 175, 16, 16, elementHeight);
            }
        }

        String text = element.getSetting(NetworkSettings.NETWORK_NAME);

        if(selected) {
            RenderSystem.color3f(f, f1, f2);
            blit(x, y, 0, 16, elementWidth, elementHeight);
            minecraft.fontRenderer.drawString(text, x + 4, y + 2, 0xffffff);
        } else {
            RenderSystem.color3f(f * 0.75f, f1 * 0.75f, f2 * 0.75f);
            blit(x, y, 0, 16, elementWidth, elementHeight);
            minecraft.fontRenderer.drawString(text, x + 4, y + 2, 0x404040);
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
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        super.mouseClickedMain(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            if (mouseX > guiLeft + 45 && mouseX < guiLeft + 75 && mouseY > guiTop + 10 && mouseY < getGuiTop() + 17) {
                sortType = FluxUtils.incrementEnum(sortType, SortType.values());
                sortGrids(sortType);
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(timer2 == 0) {
            refreshPages(FluxNetworkCache.instance.getAllClientNetworks());
        }
        if(selectedNetwork != null && FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.SUCCESS) {
           closePopUp();
           if(connector instanceof FluxConfiguratorItem.ContainerProvider){
               FluxConfiguratorItem.ContainerProvider networkConnector = (FluxConfiguratorItem.ContainerProvider)connector;
               networkConnector.network = selectedNetwork;
               networkConnector.networkID = selectedNetwork.getNetworkID();

               this.network = selectedNetwork;
               this.networkValid = !selectedNetwork.isInvalid();
           }
        }
        if(FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.PASSWORD_REQUIRE) {
            openPopUp(new PopUpNetworkPassword(this, player, connector));
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
