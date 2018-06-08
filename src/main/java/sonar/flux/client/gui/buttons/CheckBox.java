package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import sonar.flux.client.gui.GuiAbstractTab;

import java.awt.*;

public class CheckBox extends SmallButton {

	public CheckBox(GuiAbstractTab gui, int id, int x, int y, boolean isChecked, String name) {
		super(gui, id, x, y, isChecked ? 256 : 280, name);
	}	

    @Override
    public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		if (this.visible) {
			drawRect(this.x-1, this.y-1, this.x + sizeX + 1+1, this.y + sizeY + 1+1, gui.common.getNetworkColour().getRGB());
			drawRect(this.x, this.y, this.x + sizeX + 1, this.y + sizeY + 1, Color.BLACK.getRGB());
		}
    	super.drawButton(mc, x, y, partialTicks);
    }
}
