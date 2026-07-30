package sonar.fluxnetworks.client.gui.button;

import sonar.fluxnetworks.client.gui.basic.GuiFocusable;

public class CustomColorButton extends ColorButton {
    public static final int DEFAULT_COLOR = 0xFFFFFF;

    public CustomColorButton(GuiFocusable screen, int x, int y) {
        super(screen, x, y, DEFAULT_COLOR);
    }
}