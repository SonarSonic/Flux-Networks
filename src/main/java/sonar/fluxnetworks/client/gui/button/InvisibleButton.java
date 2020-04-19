package sonar.fluxnetworks.client.gui.button;

import net.minecraft.client.gui.widget.button.Button;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;

public class InvisibleButton extends Button {

    public InvisibleButton(int widthIn, int heightIn, int width, int height, String text, IPressable onPress) {
        super(widthIn, heightIn, width, height, text, onPress);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {}

}
