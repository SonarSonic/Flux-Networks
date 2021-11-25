package sonar.fluxnetworks.client.gui.popup;

/*import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;

import javax.annotation.Nonnull;

public class PopupCustomColor extends PopupCore<GuiFluxCore> {

    public FluxTextWidget customColor;
    public NormalButton colorApply;
    public int currentColor;

    public PopupCustomColor(GuiFluxCore host, PlayerEntity player, int currentColor) {
        super(host, player);
        this.currentColor = currentColor;
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
        customColor.setText(Integer.toHexString(currentColor));
        customColor.setResponder(string -> colorApply.clickable = string.length() == 6);
        addButton(customColor);
    }


    @Override
    public void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        //screenUtils.drawRectWithBackground(30, 44, 60, 118, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        drawCenterText(matrixStack, FluxTranslate.CUSTOM_COLOR.t(), 88, 48, 0xffffff);
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
                        currentColor = customColor.getIntegerFromHex();
                        host.closePopUp();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}*/
