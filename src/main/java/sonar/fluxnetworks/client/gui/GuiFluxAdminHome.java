package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.CSuperAdminMessage;
import sonar.fluxnetworks.common.network.GUIPermissionRequestPacket;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;

public class GuiFluxAdminHome extends GuiTabCore {

    public InvisibleButton redirectButton;

    private int timer;
    public SlidedSwitchButton detailedNetworkView, superAdmin;

    public GuiFluxAdminHome(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        screenUtils.renderNetwork(matrixStack, network.getNetworkName(), network.getNetworkColor(), 20, 8);
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        font.drawString(matrixStack, AccessLevel.SUPER_ADMIN.getName(), 20, 30, network.getNetworkColor());
        font.drawString(matrixStack, "Detailed Network View", 20, 42, network.getNetworkColor());
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
        addButton(redirectButton);

        superAdmin = new SlidedSwitchButton(140, 30, 0, guiLeft, guiTop, FluxClientCache.superAdmin);
        switches.add(superAdmin);

        detailedNetworkView = new SlidedSwitchButton(140, 42, 1, guiLeft, guiTop, FluxClientCache.detailedNetworkView);
        switches.add(detailedNetworkView);
    }


    public void onSuperAdminChanged() {
        super.onSuperAdminChanged();
        superAdmin.slideControl = FluxClientCache.superAdmin;
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
                    FluxClientCache.detailedNetworkView = switchButton.slideControl;
                    break;
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            PacketHandler.CHANNEL.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_GENERAL));
            PacketHandler.CHANNEL.sendToServer(new GUIPermissionRequestPacket(network.getNetworkID(), player.getUniqueID()));
        }
        timer++;
        timer %= 100;
    }
}
