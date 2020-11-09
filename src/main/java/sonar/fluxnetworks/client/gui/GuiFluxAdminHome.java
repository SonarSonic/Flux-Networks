package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.common.network.CGuiPermissionMessage;
import sonar.fluxnetworks.common.network.CNetworkUpdateMessage;
import sonar.fluxnetworks.common.network.CSuperAdminMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;

import javax.annotation.Nonnull;

public class GuiFluxAdminHome extends GuiTabCore {

    public InvisibleButton redirectButton;

    private int timer;
    public SlidedSwitchButton detailedNetworkView, superAdmin;

    public GuiFluxAdminHome(@Nonnull FluxMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        screenUtils.renderNetwork(matrixStack, network.getNetworkName(), network.getNetworkColor(), 20, 8);
        drawCenterText(matrixStack, TextFormatting.RED + FluxClientCache.getFeedbackText(), 89, 150, 0xffffff);

        font.drawString(matrixStack, AccessLevel.SUPER_ADMIN.getName(), 20, 30, network.getNetworkColor());
        font.drawString(matrixStack, FluxTranslate.DETAILED_VIEW.t(), 20, 42, network.getNetworkColor());
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12,
                EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
        addButton(redirectButton);

        superAdmin = new SlidedSwitchButton(140, 30, 0, guiLeft, guiTop, FluxClientCache.superAdmin);
        switches.add(superAdmin);

        detailedNetworkView = new SlidedSwitchButton(140, 42, 1, guiLeft, guiTop, FluxClientCache.detailedNetworkView);
        switches.add(detailedNetworkView);
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && button instanceof SlidedSwitchButton) {
            SlidedSwitchButton switchButton = (SlidedSwitchButton) button;
            switchButton.switchButton();
            switch (switchButton.id) {
                case 0:
                    NetworkHandler.INSTANCE.sendToServer(new CSuperAdminMessage());
                    break;
                case 1:
                    FluxClientCache.detailedNetworkView = switchButton.toggled;
                    break;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            NetworkHandler.INSTANCE.sendToServer(new CNetworkUpdateMessage(network.getNetworkID(), FluxConstants.TYPE_NET_BASIC));
            NetworkHandler.INSTANCE.sendToServer(new CGuiPermissionMessage(network.getNetworkID()));
        }
        timer++;
        timer %= 100;
    }
}
