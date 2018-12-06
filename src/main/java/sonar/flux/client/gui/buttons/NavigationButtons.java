package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.helpers.FontHelper;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.scale;

@SideOnly(Side.CLIENT)
public class NavigationButtons extends ImageButton {
	public int id;
	public GuiTabAbstract gui;
	public EnumGuiTab tab;

	public NavigationButtons(GuiTabAbstract gui, EnumGuiTab tab, int id, int x, int y) {
		super(id, x, y, GuiTabAbstract.navigation, tab.texX / 2, tab.texY / 2, 16, 16);
		this.id = id;
		this.tab = tab;
		this.gui = gui;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(FontHelper.translate(tab.getClientName()), x, y);
	}

	public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		if (visible) {
			color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = x >= this.x && y >= this.y && x < this.x + width + 1 && y < this.y + height + 1;
			mc.getTextureManager().bindTexture(texture);
			scale(0.5, 0.5, 0.5);
			drawTexturedModalRect((float) (this.x / 0.5), (float) (this.y / 0.5), textureX, gui.getCurrentTab() == tab ? textureY : textureY + 32, sizeX * 2, sizeY * 2);
			scale(1.0 / 0.5, 1.0 / 0.5, 1.0 / 0.5);
		}
	}
}