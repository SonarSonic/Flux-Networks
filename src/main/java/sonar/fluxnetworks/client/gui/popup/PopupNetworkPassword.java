package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabSelection;

import javax.annotation.Nonnull;

public class PopupNetworkPassword extends PopupCore<GuiTabSelection> {

    public FluxTextWidget password;

    public PopupNetworkPassword(GuiTabSelection host, PlayerEntity player) {
        super(host, player);
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
    public void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        if (host.selectedNetwork != null) {
            drawCenterText(matrixStack, FluxTranslate.CONNECTING_TO.t() + " " + host.selectedNetwork.getNetworkName(), 88, 50, 0xffffff);
        }
        drawCenterText(matrixStack, FluxTranslate.NETWORK_PASSWORD.t() + ":", 40, 68, 0xffffff);

        drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 88, 110, FluxClientCache.getFeedbackColor());
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
