package sonar.flux.client;

import java.awt.Color;
import java.util.Collections;
import java.util.List;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.GuiSonarTile;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.FluxError;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;
import static net.minecraft.client.renderer.GlStateManager.*;

public abstract class GuiFluxBase extends GuiSonarTile {

	public static final ResourceLocation select = new ResourceLocation("fluxnetworks:textures/gui/networkSelect.png");
	public static final ResourceLocation connections = new ResourceLocation("fluxnetworks:textures/gui/connections.png");
	public static final ResourceLocation bground = new ResourceLocation("fluxnetworks:textures/gui/fluxPlug.png");
	public static final ResourceLocation buttons = new ResourceLocation("fluxnetworks:textures/gui/buttons/buttons.png");
	public static final ResourceLocation navigation = new ResourceLocation("fluxnetworks:textures/gui/navigation.png");
	public static final CustomColour[] colours = new CustomColour[] { new CustomColour(41, 94, 138), new CustomColour(52, 52, 119), new CustomColour(88, 42, 114), new CustomColour(136, 45, 96), new CustomColour(170, 57, 57), new CustomColour(170, 111, 57), new CustomColour(198, 185, 0), new CustomColour(96, 151, 50) };

	public static final int midBlue = FontHelper.getIntFromColor(41, 94, 138);
	public static final int lightBlue = FontHelper.getIntFromColor(90, 180, 255);
	public static final int darkBlue = FontHelper.getIntFromColor(37, 61, 81);

    public int errorDisplayTicks;
	public int errorDisplayTime = 300;

	public static GuiState state = GuiState.INDEX;

	public TileEntityFlux tile;

	public GuiFluxBase(Container container, TileEntityFlux tile) {
		super(container, tile);
		this.tile = tile;
		this.tile.error = FluxError.NONE;
	}

	public void renderFlux(IFlux network, boolean isSelected, int x, int y) {
		color(1.0F, 1.0F, 1.0F, 1.0F);
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(state.getBackground());
		drawTexturedModalRect(x, y, 0, /* isSelected ? 178 : 166 */166, 154, 12);
		FontHelper.text(network.getCustomName(), x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
	}

    public void renderNetwork(String networkName, AccessType access, int rgb, boolean isSelected, int x, int y) {
		color(1.0F, 1.0F, 1.0F, 1.0F);
		drawRect(x, y, x + 154, y + 12, rgb);
        Minecraft.getMinecraft().getTextureManager().bindTexture(select);
		drawTexturedModalRect(x, y, 0, /* isSelected ? 178 : 166 */166, 154, 12);
        FontHelper.text(networkName, x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
	}

    public void renderNetworkInFull(String networkName, AccessType access, int rgb, boolean isSelected, int x, int y) {
		color(1.0F, 1.0F, 1.0F, 1.0F);
		drawRect(x, y, x + 154, y + 24, rgb);
		drawRect(x + 1, y + 1, x + 154 - 1, y + 24 - 1, Color.BLACK.getRGB());
        FontHelper.text(TextFormatting.BOLD + networkName, x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
        FontHelper.text(FontHelper.translate("network.accessSetting") + ": " + TextFormatting.AQUA + FontHelper.translate(access.getName()), x + 3, y + 13, 0);
	}

	public void renderEnergyBar(int x, int y, long stored, long max, int startCol, int endCol) {
		drawRect(x, y, xSize - x, y + 16, Color.DARK_GRAY.getRGB());
		drawRect(x + 1, y + 1, xSize - x - 1, y + 15, Color.BLACK.getRGB());
		if (max != 0 && stored != 0) {
            long k = stored * (xSize - x * 2) / max;
			drawGradientRect(x + 1, y + 1, (int) (x - 1 + k), y + 15, startCol, endCol);
		}
        FontHelper.textCentre(FontHelper.formatStorage(stored) + '/' + FontHelper.formatStorage(max), xSize, y + 4, -1);
	}

	public void renderNavigationPrompt(String error, String prompt) {
		FontHelper.textCentre(FontHelper.translate(error), xSize, 10, Color.GRAY.getRGB());
		scale(0.75, 0.75, 0.75);
        FontHelper.textCentre("Click" + TextFormatting.AQUA + ' ' + prompt + ' ' + TextFormatting.RESET + "Above", (int) (xSize * 1.0 / 0.75), (int) (20 * 1.0 / 0.75), Color.GRAY.getRGB());
		scale(1.0 / 0.75, 1.0 / 0.75, 1.0 / 0.75);
	}

    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
		if (tile.error == FluxError.NONE) {
            drawSonarCreativeTabHoveringText(tabName, mouseX, mouseY);
		}
	}

    public void drawHoveringText(List<String> textLines, int x, int y) {
		if (tile.error == FluxError.NONE) {
			super.drawHoveringText(textLines, x, y);
		}
	}

	protected void drawError(int x, int y) {
		if (tile.error != FluxError.NONE) {
			if (this.errorDisplayTicks < this.errorDisplayTime) {
				errorDisplayTicks++;
                drawHoveringText(Collections.singletonList(TextFormatting.RED + "" + TextFormatting.BOLD + FontHelper.translate(tile.error.getErrorMessage())), x, y, fontRendererObj);
			} else {
				errorDisplayTicks = 0;
				tile.error = FluxError.NONE;
			}
		}
	}

	/// STATE
	public void switchState(GuiState state) {
	    PacketHelper.sendPacketToServer(PacketType.GUI_STATE_CHANGE, tile, PacketHelper.createStateChangePacket(state.type));
        GuiFluxBase.state = state;
        doReset();//force quick reset
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
		    PacketHelper.sendPacketToServer(PacketType.SET_NETWORK, tile, PacketHelper.createNetworkSetPacket(network.getNetworkID()));
		}
	}

	public ResourceLocation getBackground() {
        return state.getBackground();
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

    public SelectButtons selectButton(int id, int x, int y, int texX, String name) {
        return new SelectButtons(id, x, y, texX, name);
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

        public NavigationButtons(GuiState state, int id, int x, int y) {
            super(id, x, y, navigation, state.texX / 2, 0, 16, 16);
			this.id = id;
			this.buttonState = state;
		}

		public void drawButtonForegroundLayer(int x, int y) {
			drawCreativeTabHoveringText(FontHelper.translate(buttonState.getClientName()), x, y);
		}

        public void drawButton(Minecraft mc, int x, int y) {
			if (visible) {
				color(1.0F, 1.0F, 1.0F, 1.0F);
                hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + width + 1 && y < this.yPosition + height + 1;
                /*short short1 = 219;
				int k = 0;

				if (!enabled) {
					k += width * 2;
				} else if (bool) {
                    k += width;
				} else if (hovered) {
					k += width * 3;
                }*/

				mc.getTextureManager().bindTexture(texture);
				scale(0.5, 0.5, 0.5);
                drawTexturedModalRect((float) (this.xPosition / 0.5), (float) (this.yPosition / 0.5), textureX, state == buttonState ? textureY : textureY + 32, sizeX * 2, sizeY * 2);
				scale(1.0 / 0.5, 1.0 / 0.5, 1.0 / 0.5);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
    public static class NetworkButton extends ImageButton {

		public NetworkButton(int id, int x, int y) {
			super(id, x, y, bground, 0, 190, 154, 11);
		}
	}
	
    public FontRenderer getFontRenderer() {
        return this.fontRendererObj;
    }

    public List<GuiButton> getButtonList() {
        return buttonList;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }
}
