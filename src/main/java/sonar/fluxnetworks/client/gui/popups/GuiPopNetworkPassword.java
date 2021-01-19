package sonar.fluxnetworks.client.gui.popups;

import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.TextboxButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabSelection;
import sonar.fluxnetworks.api.network.NetworkSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class GuiPopNetworkPassword extends GuiPopCore<GuiTabSelection> {

    public TextboxButton password;

    public GuiPopNetworkPassword(GuiTabSelection host, EntityPlayer player, INetworkConnector connector) {
        super(host, player, connector);
    }


    @Override
    public void initGui() {
        super.initGui();
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 24, 86, 48, 12, 11));
        popButtons.add(new NormalButton(FluxTranslate.CONNECT.t(), 102, 86, 48, 12, 12));

        password = TextboxButton.create(this, "", 5, fontRenderer, 70, 66, 81, 12);
        password.setTextInvisible();
        password.setMaxStringLength(16);

        popBoxes.add(password);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if(host.selectedNetwork != null) {
            drawCenteredString(fontRenderer, FluxTranslate.CONNECTING_TO.t() + " " + host.selectedNetwork.getSetting(NetworkSettings.NETWORK_NAME), 88, 50, 0xffffff);
        }
        drawCenteredString(fontRenderer, FluxTranslate.NETWORK_PASSWORD.t() + ":", 40, 68, 0xffffff);

        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 110, 0xffffff);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 11) {
                        host.closePopUp();
                        return;
                    }
                    if(button.id == 12) {
                        if(password.getText().length() > 0) {
                            host.setConnectedNetwork(host.selectedNetwork.getNetworkID(), password.getText());
                            password.setText("");
                        }
                        return;
                    }
                }
            }
        }
    }
}
