package sonar.flux.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.helpers.FontHelper;

@SideOnly(Side.CLIENT)
public class SmallButton extends ImageButton {
	public int id;
	public String name;
	public AbstractGuiTab gui;

	public SmallButton(AbstractGuiTab gui, int id, int x, int y, int texX, String name) {
		super(id, x, y, AbstractGuiTab.small_buttons, texX / 2, 0, 11, 11);
		this.gui = gui;
		this.id = id;
		this.name = name;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(FontHelper.translate(name), x, y);
	}
}