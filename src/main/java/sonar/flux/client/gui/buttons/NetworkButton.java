package sonar.flux.client.gui.buttons;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;

@SideOnly(Side.CLIENT)
public class NetworkButton extends ImageButton {
	public static final ResourceLocation bground = new ResourceLocation("fluxnetworks:textures/gui/fluxPlug.png");

	public NetworkButton(int id, int x, int y) {
		super(id, x, y, bground, 0, 190, 154, 11);
	}
}
