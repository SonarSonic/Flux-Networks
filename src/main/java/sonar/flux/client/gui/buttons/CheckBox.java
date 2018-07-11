package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.GuiAbstractTab;

import java.awt.*;
import java.util.function.Supplier;

public class CheckBox extends SmallButton {

	public Supplier<Boolean> isChecked;

	public CheckBox(GuiAbstractTab gui, int id, int x, int y, Supplier<Boolean> isChecked, String name) {
		super(gui, id, x, y, 48, 24, name);
		this.isChecked = isChecked;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(name + ": " + FluxTranslate.translateBoolean(isChecked.get()), x, y);
	}

	@Override
	public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
		if (this.visible) {
			drawRect(this.x - 1, this.y - 1, this.x + sizeX + 2, this.y + sizeY + 2, gui.getNetworkColour());
			drawRect(this.x, this.y, this.x + sizeX + 1, this.y + sizeY + 1, Color.BLACK.getRGB());
		}
		if(isChecked.get()) {
			textureY = 24/2;
		}else{
			textureY = 48/2;
		}
		mc.getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY, sizeX + 1, sizeY + 1);

	}
}
