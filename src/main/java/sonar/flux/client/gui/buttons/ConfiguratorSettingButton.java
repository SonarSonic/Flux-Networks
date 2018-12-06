package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import sonar.core.client.gui.GuiSonar;

public class ConfiguratorSettingButton extends GuiButton {

    public GuiSonar gui;
    public int outlineColour;
    public String key;
    public String value;

    public ConfiguratorSettingButton(GuiSonar gui, int buttonId, int x, int y, int width, int height, int outlineColour, String key, String value) {
        super(buttonId, x, y, width, height, "");
        this.outlineColour = outlineColour;
        this.key = key;
        this.value = value;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, outlineColour);
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        int i = mc.fontRenderer.drawString(key, x + 4, y + 2, outlineColour);
        mc.fontRenderer.drawString(mc.fontRenderer.trimStringToWidth(value, width - (i - x)), i, y + 2, 14737632);
    }

    public void drawIcon(){

    }


    public void drawButtonForegroundLayer(int mouseX, int mouseY){
        gui.drawSonarCreativeTabHoveringText(key + value, mouseX, mouseY);
    }
}
