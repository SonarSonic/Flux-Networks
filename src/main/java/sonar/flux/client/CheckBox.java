package sonar.flux.client;

import java.awt.Color;

import net.minecraft.client.Minecraft;

public class CheckBox extends SmallButton {

	public CheckBox(AbstractGuiTab gui, int id, int x, int y, boolean isChecked, String name) {
		super(gui, id, x, y, isChecked ? 256 : 280, name);
	}	

    @Override
    public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		if (this.visible) {
			gui.drawRect(this.x-1, this.y-1, this.x + sizeX + 1+1, this.y + sizeY + 1+1, gui.common.getNetworkColour().getRGB());
			gui.drawRect(this.x, this.y, this.x + sizeX + 1, this.y + sizeY + 1, Color.BLACK.getRGB());
		}
    	super.drawButton(mc, x, y, partialTicks);
    }
}
