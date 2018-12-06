package sonar.flux.client.gui.buttons;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.EnumActivationType;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.NetworkSettings;

import java.awt.*;
import java.util.function.Supplier;

public class RedstoneSignalButton extends SmallButton {

	public Supplier<EnumActivationType> redstoneType;

	public RedstoneSignalButton(GuiTabAbstract gui, int id, int x, int y, Supplier<EnumActivationType> redstoneType, String name) {
		super(gui, id, x, y, 24, 24, name);
		this.redstoneType = redstoneType;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		TileFlux flux = FluxNetworks.proxy.getFluxTile();
		if(flux != null) {
			boolean active = true;
			switch (redstoneType.get()) {
				case ACTIVATED:
					active = true;
					break;
				case DISACTIVATED:
					active = false;
					break;
				case POSITIVE_SIGNAL:
					active = flux.redstone_power.getValue();
					break;
				case NEGATIVE_SIGNAL:
					active = !flux.redstone_power.getValue();
					break;
				default:
					active = true;
					break;
			}
			gui.drawSonarCreativeTabHoveringText(Lists.newArrayList(redstoneType.get().comment.t(), FluxTranslate.ACTIVATION_CURRENT_STATE.t() + ": " + FluxTranslate.translateActivation(active)), x, y);
		}else{
			gui.drawSonarCreativeTabHoveringText(Lists.newArrayList(redstoneType.get().comment.t(), FluxTranslate.ACTIVATION_CURRENT_STATE.t()), x, y);
		}
	}

    @Override
    public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
		if (this.visible) {
			drawRect(this.x - 1, this.y - 1, this.x + sizeX + 2, this.y + sizeY + 2, NetworkSettings.NETWORK_COLOUR.getValue(gui.common).getRGB());
			drawRect(this.x, this.y, this.x + sizeX + 1, this.y + sizeY + 1, Color.BLACK.getRGB());
		}
		EnumActivationType type = redstoneType.get();
		textureY = type.getTextureY();
		mc.getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY, sizeX + 1, sizeY + 1);
	}
}
