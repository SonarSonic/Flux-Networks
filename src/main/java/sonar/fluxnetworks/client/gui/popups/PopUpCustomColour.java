package sonar.fluxnetworks.client.gui.popups;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;

public class PopUpCustomColour extends PopUpCore<GuiFluxCore> {

    public FluxTextWidget customColor;
    public NormalButton colorApply;
    public int currentColour;

    public PopUpCustomColour(GuiFluxCore host, int currentColour, PlayerEntity player, INetworkConnector connector) {
        super(host, player, connector);
        this.currentColour = currentColour;
    }

    @Override
    public void init() {
        super.init();
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 86, 36, 12, 11));
        colorApply = new NormalButton(FluxTranslate.APPLY.t(), 100, 86, 36, 12, 12);
        popButtons.add(colorApply);

        customColor = FluxTextWidget.create("0x", font, guiLeft + 57, guiTop + 64, 64, 12).setHexOnly();
        customColor.setMaxStringLength(6);
        customColor.setText(Integer.toHexString(currentColour));
        customColor.setResponder(string -> colorApply.clickable = string.length() == 6);
        addButton(customColor);
    }


    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        //screenUtils.drawRectWithBackground(30, 44, 60, 118, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        drawCenteredString(matrixStack, font, FluxTranslate.CUSTOM_COLOR.t(), 88, 48, 0xffffff);
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
                        currentColour = customColor.getIntegerFromHex();
                        host.closePopUp();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
