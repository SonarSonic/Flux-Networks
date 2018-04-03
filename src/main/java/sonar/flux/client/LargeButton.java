package sonar.flux.client;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.scale;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.helpers.FontHelper;

@SideOnly(Side.CLIENT)
public class LargeButton extends ImageButton {
	public int id;
	public AbstractGuiTab gui;
	public String hover;

	public LargeButton(AbstractGuiTab gui, String hover, int id, int x, int y, int texX, int texY) {
		super(id, x, y, AbstractGuiTab.large_buttons, texX, texY, 17, 17);
		this.id = id;
		this.hover = hover;
		this.gui = gui;
	}

	public void drawButtonForegroundLayer(int x, int y) {
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