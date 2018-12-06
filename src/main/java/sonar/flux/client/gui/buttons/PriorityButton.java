package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import sonar.flux.FluxTranslate;
import sonar.flux.api.EnumPriorityType;
import sonar.flux.client.gui.GuiTabAbstract;

import java.awt.*;
import java.util.function.Supplier;

public class PriorityButton extends SmallButton {

	public Supplier<EnumPriorityType> priorityType;

	public PriorityButton(GuiTabAbstract gui, int id, int x, int y, Supplier<EnumPriorityType> priorityType, String name) {
		super(gui, id, x, y, 120, 0, name);
		this.priorityType = priorityType;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(FluxTranslate.PRIORITY_MODE.t() + ": " + priorityType.get(), x, y);
	}

    @Override
    public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
		if (this.visible) {
			drawRect(this.x - 1, this.y - 1, this.x + sizeX + 2, this.y + sizeY + 2, gui.getNetworkColour());
			drawRect(this.x, this.y, this.x + sizeX + 1, this.y + sizeY + 1, Color.BLACK.getRGB());
		}
		EnumPriorityType type = priorityType.get();
		switch(type){
			case NORMAL:
				textureY = 12;
				break;
			case SURGE:
				textureY = 24;
				break;
		}
		mc.getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY, sizeX + 1, sizeY + 1);
	}
}
