package sonar.flux.client.gui.buttons;

import net.minecraft.client.gui.FontRenderer;
import sonar.core.client.gui.SonarTextField;

public class FluxTextField extends SonarTextField {

    public String key;
    public int keyWidth;
    public FontRenderer renderer;

    private FluxTextField(String key, int id, FontRenderer renderer, int x, int y, int width, int height, int keyWidth) {
        super(id, renderer, x + keyWidth, y, width - keyWidth, height);
        this.key = key;
        this.keyWidth = keyWidth;
        this.renderer = renderer;
    }

    public static FluxTextField create(String key, int id, FontRenderer renderer, int x, int y, int width, int height) {
        int keyWidth = renderer.getStringWidth(key);
        return new FluxTextField(key, id, renderer, x, y, width, height, keyWidth);
    }
    @Override
    public void drawTextBox() {
        this.setEnableBackgroundDrawing(true);
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(this.x-keyWidth - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, outlineColour);
                drawRect(this.x-keyWidth, this.y, this.x + this.width, this.y + this.height, boxColour);
            }
        }
        this.setEnableBackgroundDrawing(false);
        x += 4;
        y += (this.height - 8) / 2;

        superDrawTextBox();
        renderer.drawString(key, x - keyWidth, y, outlineColour);
        x -= 4;
        y -= (this.height - 8) / 2;

    }

}
