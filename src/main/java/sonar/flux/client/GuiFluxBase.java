package sonar.flux.client;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxError;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.PacketFluxButton;
import sonar.flux.network.PacketFluxButton.Type;

public abstract class GuiFluxBase extends GuiSonar {

	public static final ResourceLocation select = new ResourceLocation("FluxNetworks:textures/gui/networkSelect.png");
	public static final ResourceLocation connections = new ResourceLocation("FluxNetworks:textures/gui/connections.png");
	public static final ResourceLocation bground = new ResourceLocation("FluxNetworks:textures/gui/fluxPlug.png");
	public static final ResourceLocation buttons = new ResourceLocation("fluxnetworks:textures/gui/buttons/buttons.png");
	public static final ResourceLocation navigation = new ResourceLocation("fluxnetworks:textures/gui/navigation.png");
	public static final CustomColour[] colours = new CustomColour[] { new CustomColour(41, 94, 138), new CustomColour(52, 52, 119), new CustomColour(88, 42, 114), new CustomColour(136, 45, 96), new CustomColour(170, 57, 57), new CustomColour(170, 111, 57), new CustomColour(198, 185, 0), new CustomColour(96, 151, 50) };

	public static final int midBlue = FontHelper.getIntFromColor(41, 94, 138);
	public static final int lightBlue = FontHelper.getIntFromColor(90, 180, 255);
	public static final int darkBlue = FontHelper.getIntFromColor(37, 61, 81);

	public int errorDisplayTicks = 0;
	public int errorDisplayTime = 300;

	public static GuiState state = GuiState.INDEX;

	public TileEntityFlux tile;

	public ResourceLocation getBackgroundFromState(GuiState state) {
		if (state == GuiState.CONNECTIONS)
			return connections;
		if (state == GuiState.NETWORK_SELECT || state == GuiState.PLAYERS)
			return GuiFlux.select;
		return GuiFlux.bground;
	}

	public GuiFluxBase(Container container, TileEntityFlux tile) {
		super(container, tile);
		this.tile = tile;
		this.tile.error = FluxError.NONE;
	}

	public void renderFlux(IFlux network, boolean isSelected, int x, int y) {
		// int rgb = network.getNetworkColour().getRGB();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int colour = midBlue;
		switch (network.getConnectionType()) {
		case POINT:
			colour = new CustomColour(136, 40, 40).getRGB();
			break;
		case PLUG:
			colour = colours[7].getRGB();
			break;
		case STORAGE:
			colour = midBlue;
			break;
		case CONTROLLER:
			colour = new CustomColour(100, 100, 120).getRGB();
			break;
		default:
			break;
		}

		drawRect(x, y, x + 154, y + 12, colour);
		Minecraft.getMinecraft().getTextureManager().bindTexture(getBackgroundFromState(GuiState.NETWORK_SELECT));
		drawTexturedModalRect(x, y, 0, /* isSelected ? 178 : 166 */166, 154, 12);
		FontHelper.text(network.getCustomName(), x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
	}

	public void renderNetwork(IFluxCommon network, boolean isSelected, int x, int y) {
		int rgb = network.getNetworkColour().getRGB();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawRect(x, y, x + 154, y + 12, rgb);
		Minecraft.getMinecraft().getTextureManager().bindTexture(getBackgroundFromState(GuiState.NETWORK_SELECT));
		drawTexturedModalRect(x, y, 0, /* isSelected ? 178 : 166 */166, 154, 12);
		FontHelper.text(network.getNetworkName(), x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
	}

	public void renderNetworkInFull(IFluxCommon network, boolean isSelected, int x, int y) {
		int rgb = network.getNetworkColour().getRGB();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawRect(x, y, x + 154, y + 24, rgb);
		drawRect(x + 1, y + 1, x + 154 - 1, y + 24 - 1, Color.BLACK.getRGB());
		FontHelper.text(TextFormatting.BOLD + network.getNetworkName(), x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
		FontHelper.text(FontHelper.translate("network.accessSetting") + ": " + TextFormatting.AQUA + FontHelper.translate(network.getAccessType().getName()), x + 3, y + 13, 0);
	}

	public void renderEnergyBar(int x, int y, long stored, long max, int startCol, int endCol) {
		drawRect(x, y, xSize - x, y + 16, Color.DARK_GRAY.getRGB());
		drawRect(x + 1, y + 1, xSize - x - 1, y + 15, Color.BLACK.getRGB());
		if (max != 0 && stored != 0) {
			long k = (stored * (xSize - (x * 2))) / max;
			drawGradientRect(x + 1, y + 1, (int) (x - 1 + k), y + 15, startCol, endCol);
		}
		FontHelper.textCentre("" + FontHelper.formatStorage(stored) + "/" + FontHelper.formatStorage(max), xSize, y + 4, -1);
	}

	public void renderNavigationPrompt(String error, String prompt) {
		FontHelper.textCentre(FontHelper.translate(error), xSize, 10, Color.GRAY.getRGB());
		GL11.glScaled(0.75, 0.75, 0.75);
		FontHelper.textCentre("Click" + TextFormatting.AQUA + " " + prompt + " " + TextFormatting.RESET + "Above", (int) (xSize * 1.0 / 0.75), (int) (20 * 1.0 / 0.75), Color.GRAY.getRGB());
		GL11.glScaled(1.0 / 0.75, 1.0 / 0.75, 1.0 / 0.75);
	}

	protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
		if (tile.error == FluxError.NONE) {
			super.drawCreativeTabHoveringText(tabName, mouseX, mouseY);
		}
	}

	protected void drawHoveringText(List<String> textLines, int x, int y) {
		if (tile.error == FluxError.NONE) {
			super.drawHoveringText(textLines, x, y);
		}
	}

	protected void drawError(int x, int y) {
		if (tile.error != FluxError.NONE) {
			if (this.errorDisplayTicks < this.errorDisplayTime) {
				errorDisplayTicks++;
				drawHoveringText(Arrays.<String>asList(new String[] { TextFormatting.RED + "" + TextFormatting.BOLD + FontHelper.translate(tile.error.getErrorMessage()) }), x, y, fontRendererObj);
			} else {
				errorDisplayTicks = 0;
				tile.error = FluxError.NONE;
			}
		}
	}

	/// STATE
	public void switchState(GuiState state) {
		FluxNetworks.network.sendToServer(new PacketFluxButton(Type.STATE_CHANGE, tile.getPos(), state));
		this.state = state;
		reset();
	}

	public int getNetworkID() {
		return tile.networkID.getObject();
	}

	/// NETWORKS
	public List<? extends IFluxCommon> getNetworks() {
		return FluxNetworks.getClientCache().getAllNetworks();
	}

	public void setNetwork(IFluxCommon network) {
		if (network.getNetworkID() != -1) {
			FluxNetworks.network.sendToServer(new PacketFluxButton(Type.SET_NETWORK, tile.getPos(), network.getNetworkID(), network.getCachedPlayerName()));
		}
	}

	public void changeNetworkName(int networkID, String name) {
		if (networkID != -1 && name != null && !name.isEmpty()) {
			FluxNetworks.network.sendToServer(new PacketFluxButton(Type.EDIT_NETWORK, tile.getPos(), networkID, name));
		}
	}

	public ResourceLocation getBackground() {
		return getBackgroundFromState(state);
	}

	/// CUSTOM BUTTONS
	@SideOnly(Side.CLIENT)
	public class ConfigNetworkButton extends ImageButton {

		public ConfigNetworkButton(int id, int x, int y) {
			super(id, x, y, new ResourceLocation("fluxnetworks:textures/gui/buttons/buttons.png"), 0, 0, 16, 16);
		}

		public void drawButton(Minecraft mc, int x, int y) {
			super.drawButton(mc, x, y);
			this.hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
			if (hovered) {
				this.textureY = 0;
			} else {
				this.textureY = 17;
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			drawCreativeTabHoveringText("Configure Network", x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public class SelectButtons extends ImageButton {
		public int id;
		public String name;

		public SelectButtons(int id, int x, int y, int texX, String name) {
			super(id, x, y, buttons, texX / 2, 0, 11, 11);
			this.id = id;
			this.name = name;
		}

		public void drawButtonForegroundLayer(int x, int y) {
			drawCreativeTabHoveringText(FontHelper.translate(name), x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public class NavigationButtons extends ImageButton {
		public int id;
		public GuiState buttonState;

		public NavigationButtons(GuiState state, int id, int x, int y, int texX) {
			super(id, x, y, navigation, texX / 2, 0, 16, 16);
			this.id = id;
			this.buttonState = state;
		}

		public void drawButtonForegroundLayer(int x, int y) {
			drawCreativeTabHoveringText(FontHelper.translate(buttonState.getClientName()), x, y);
		}

		public void drawButton(Minecraft mc, int x, int y) {
			if (visible) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				hovered = x >= xPosition && y >= yPosition && x < xPosition + (width + 1) && y < yPosition + (height + 1);
				short short1 = 219;
				int k = 0;

				if (!enabled) {
					k += width * 2;
				} else if (bool) {
					k += width * 1;
				} else if (hovered) {
					k += width * 3;
				}

				mc.getTextureManager().bindTexture(texture);
				GL11.glScaled(0.5, 0.5, 0.5);
				drawTexturedModalRect((float) (xPosition / 0.5), (float) (yPosition / 0.5), textureX, state == buttonState ? textureY : textureY + 32, sizeX * 2, sizeY * 2);
				GL11.glScaled(1.0 / 0.5, 1.0 / 0.5, 1.0 / 0.5);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public class NetworkButton extends ImageButton {

		public NetworkButton(int id, int x, int y) {
			super(id, x, y, bground, 0, 190, 154, 11);
		}
	}
	
}
