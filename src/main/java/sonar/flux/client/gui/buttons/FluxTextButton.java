package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import sonar.flux.client.FluxColourHandler;

public class FluxTextButton extends GuiButton {

    public int outlineColour;

    public FluxTextButton(int buttonId, int x, int y, int width, int height, String buttonText, int outlineColour) {
        super(buttonId, x, y, buttonText);
        this.outlineColour = outlineColour;
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
        hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, outlineColour);
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, hovered ? outlineColour : FluxColourHandler.NO_NETWORK_COLOUR);
    }
}
