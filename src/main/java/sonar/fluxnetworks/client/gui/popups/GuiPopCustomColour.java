package sonar.fluxnetworks.client.gui.popups;

import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.client.gui.basic.GuiTextField;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.TextboxButton;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class GuiPopCustomColour extends GuiPopCore<GuiFluxCore> {

    public TextboxButton customColor;
    public NormalButton colorApply;
    public int currentColour;

    public GuiPopCustomColour(GuiFluxCore host, int currentColour, EntityPlayer player, INetworkConnector connector) {
        super(host, player, connector);
        this.currentColour = currentColour;
    }

    @Override
    public void initGui() {
        super.initGui();
        popBoxes.clear();
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 86, 36, 12, 11));
        colorApply = new NormalButton(FluxTranslate.APPLY.t(), 100, 86, 36, 12, 12);
        popButtons.add(colorApply);

        customColor = TextboxButton.create(this, "0x", 7, fontRenderer, 57, 64, 64, 12).setHexOnly();
        customColor.setMaxStringLength(6);
        customColor.setText(Integer.toHexString(currentColour));

        popBoxes.add(customColor);
    }


    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(30, 44, 60, 118, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, FluxTranslate.CUSTOM_COLOR.t(), 88, 48, 0xffffff);
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 11) {
                        host.closePopUp();
                    }
                    if(button.id == 12) {
                        currentColour = customColor.getIntegerFromHex();
                        host.closePopUp();
                    }
                    return;
                }
            }
        }
    }



    @Override
    public void keyTyped(char c, int k) {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                host.closePopUp();
            }
        }
        for(TextboxButton text : popBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                colorApply.clickable = text.getText().length() == 6;
            }
        }
    }
}
