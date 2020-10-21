package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabSelection;

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

        password = FluxTextWidget.create("", font, 70, 66, 81, 12);
        password.setTextInvisible();
        password.setMaxStringLength(16);

        addButton(password);
    }

    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        if (host.selectedNetwork != null) {
            drawCenteredString(matrixStack, font, FluxTranslate.CONNECTING_TO.t() + " " + host.selectedNetwork.getNetworkName(), 88, 50, 0xffffff);
        }
        drawCenteredString(matrixStack, font, FluxTranslate.NETWORK_PASSWORD.t() + ":", 40, 68, 0xffffff);

        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getInfo(), 88, 110, 0xffffff);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (NormalButton button : popButtons) {
                if (button.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                    if (button.id == 11) {
                        host.closePopUp();
                        return true;
                    }
                    if (button.id == 12) {
                        if (password.getText().length() > 0) {
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
