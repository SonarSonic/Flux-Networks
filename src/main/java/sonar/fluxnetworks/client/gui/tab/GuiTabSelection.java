package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.popup.PopUpNetworkPassword;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.misc.ContainerConnector;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class GuiTabSelection extends GuiTabPages<IFluxNetwork> {

    public InvisibleButton redirectButton;

    public IFluxNetwork selectedNetwork;

    protected int timer2;

    public GuiTabSelection(@Nonnull ContainerConnector container, @Nonnull PlayerEntity player) {
        super(container, player);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 12;
        elementWidth = 146;
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_SELECTION;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (elements.size() == 0) {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_NETWORK.t(), FluxTranslate.TAB_CREATE.t());
        } else {
            String amount = FluxTranslate.TOTAL.t() + ": " + elements.size();
            font.drawString(matrixStack, amount, 158 - font.getStringWidth(amount), 10, 0xffffff);
            font.drawString(matrixStack, FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + sortType.getTranslatedName(), 19, 10, 0xffffff);
            if (!hasActivePopup()) {
                drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getText(), 88, 150, 0xffffff);
            }
        }
    }

    @Override
    protected void onElementClicked(IFluxNetwork element, int mouseButton) {
        if (mouseButton == 0) {
            selectedNetwork = element;
            setConnectedNetwork(element.getNetworkID(), "");
        }
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_SELECTION, navigationTabs);
        if (FluxClientCache.getAllNetworks().isEmpty()) {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_CREATE.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_CREATE));
            addButton(redirectButton);
        }
        refreshPages(FluxClientCache.getAllNetworks());
    }

    @Override
    public void renderElement(MatrixStack matrixStack, IFluxNetwork element, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlphaTest();
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(ScreenUtils.GUI_BAR);

        int color = element.getNetworkColor();

        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        boolean selected = connector.getNetworkID() == element.getNetworkID();
        boolean isEncrypted = element.getSecurity().isEncrypted();

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
            blit(matrixStack, x, y, 0, 16, elementWidth, elementHeight);
            minecraft.fontRenderer.drawString(matrixStack, text, x + 4, y + 2, 0xffffff);
        } else {
            RenderSystem.color3f(f * 0.75f, f1 * 0.75f, f2 * 0.75f);
            blit(matrixStack, x, y, 0, 16, elementWidth, elementHeight);
            minecraft.fontRenderer.drawString(matrixStack, text, x + 4, y + 2, 0x404040);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(MatrixStack matrixStack, IFluxNetwork element, int mouseX, int mouseY) {
        if (hasActivePopup())
            return;
        /*GlStateManager.pushMatrix();
        GlStateManager.popMatrix();*/
    }

    @Override
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        super.mouseClickedMain(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
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
        if (timer2 == 9) {
            refreshPages(FluxClientCache.getAllNetworks());
        }
        if (selectedNetwork != null && FluxClientCache.getFeedback(true) == FeedbackInfo.SUCCESS) {
            closePopUp();
            if (connector instanceof ItemFluxConfigurator.NetworkConnector) {
                ItemFluxConfigurator.NetworkConnector networkConnector = (ItemFluxConfigurator.NetworkConnector) connector;
                networkConnector.networkID = selectedNetwork.getNetworkID();

                this.network = selectedNetwork;
                this.networkValid = selectedNetwork.isValid();
            }
        }
        if (FluxClientCache.getFeedback(true) == FeedbackInfo.PASSWORD_REQUIRE) {
            openPopUp(new PopUpNetworkPassword(this, player, connector));
            FluxClientCache.setFeedback(FeedbackInfo.NONE, true);
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
