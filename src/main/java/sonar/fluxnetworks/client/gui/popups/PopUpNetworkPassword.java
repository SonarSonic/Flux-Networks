package sonar.fluxnetworks.client.gui.popups;

import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.tab.GuiTabSelection;
import sonar.fluxnetworks.api.network.NetworkSettings;
import net.minecraft.util.text.TextFormatting;

public class PopUpNetworkPassword extends PopUpCore<GuiTabSelection> {

    public FluxTextWidget password;

    public PopUpNetworkPassword(GuiTabSelection host, PlayerEntity player, INetworkConnector connector) {
        super(host, player, connector);
    }


    @Override
    public void init() {
        super.init();
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 24, 86, 48, 12, 11));
        popButtons.add(new NormalButton(FluxTranslate.CONNECT.t(), 102, 86, 48, 12, 12));

        password = FluxTextWidget.create("",  font, 70, 66, 81, 12);
        password.setTextInvisible();
        password.setMaxStringLength(16);

        addButton(password);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if(host.selectedNetwork != null) {
            drawCenteredString(font, FluxTranslate.CONNECTING_TO.t() + " " + host.selectedNetwork.getSetting(NetworkSettings.NETWORK_NAME), 88, 50, 0xffffff);
        }
        drawCenteredString(font, FluxTranslate.NETWORK_PASSWORD.t() + ":", 40, 68, 0xffffff);

        drawCenteredString(font, TextFormatting.RED + FluxNetworks.PROXY.getFeedback(false).getInfo(), 88, 110, 0xffffff);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
                    if(button.id == 11) {
                        host.closePopUp();
                        return true;
                    }
                    if(button.id == 12) {
                        if(password.getText().length() > 0) {
                            host.setConnectedNetwork(host.selectedNetwork.getNetworkID(), password.getText());
                            password.setText("");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
