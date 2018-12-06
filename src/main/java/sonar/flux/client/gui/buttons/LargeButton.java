package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.flux.client.gui.GuiTabAbstract;

import static net.minecraft.client.renderer.GlStateManager.color;

@SideOnly(Side.CLIENT)
public class LargeButton extends ImageButton {
	public int id;
	public GuiTabAbstract gui;
	public String hover;

	public LargeButton(GuiTabAbstract gui, String hover, int id, int x, int y, int texX, int texY) {
		super(id, x, y, GuiTabAbstract.large_buttons, texX, texY, 17, 17);
		this.id = id;
		this.hover = hover;
		this.gui = gui;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		if (!hover.isEmpty())
			gui.drawSonarCreativeTabHoveringText(hover, x, y);
	}

	public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		if (visible) {
			color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = x >= this.x && y >= this.y && x < this.x + width + 1 && y < this.y + height + 1;
			mc.getTextureManager().bindTexture(texture);
			drawTexturedModalRect(this.x, this.y, textureX, hovered ? textureY : textureY + 17, sizeX, sizeY);
		}
	}
}