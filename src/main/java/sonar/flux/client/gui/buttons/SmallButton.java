package sonar.flux.client.gui.buttons;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.helpers.FontHelper;
import sonar.flux.client.gui.GuiTabAbstract;

@SideOnly(Side.CLIENT)
public class SmallButton extends ImageButton {
	public int id;
	public String name;
	public GuiTabAbstract gui;

	public SmallButton(GuiTabAbstract gui, int id, int x, int y, int texX, int texY, String name) {
		super(id, x, y, GuiTabAbstract.small_buttons, texX / 2, texY / 2, 11, 11);
		this.gui = gui;
		this.id = id;
		this.name = name;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(FontHelper.translate(name), x, y);
	}
}