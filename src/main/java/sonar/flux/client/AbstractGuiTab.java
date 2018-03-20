package sonar.flux.client;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.scale;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.client.gui.GuiSonar;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.common.containers.ContainerFlux;
import sonar.flux.common.tileentity.TileFlux;

public abstract class AbstractGuiTab<T extends TileFlux> extends GuiSonar {

	public List<GuiTab> tabs;

	public static final ResourceLocation buttons = new ResourceLocation("fluxnetworks:textures/gui/buttons/buttons.png");
	public static final ResourceLocation scroller_flux_gui = new ResourceLocation("fluxnetworks:textures/gui/scroller_flux_gui.png");
	public static final ResourceLocation blank_flux_gui = new ResourceLocation("fluxnetworks:textures/gui/blank_flux_gui.png");

	public static final CustomColour[] colours = new CustomColour[] { new CustomColour(41, 94, 138), new CustomColour(52, 52, 119), new CustomColour(88, 42, 114), new CustomColour(136, 45, 96), new CustomColour(170, 57, 57), new CustomColour(170, 111, 57), new CustomColour(198, 185, 0), new CustomColour(96, 151, 50) };

	public static final int midBlue = FontHelper.getIntFromColor(41, 94, 138);
	public static final int lightBlue = FontHelper.getIntFromColor(90, 180, 255);
	public static final int darkBlue = FontHelper.getIntFromColor(37, 61, 81);
	public static final int grey = FontHelper.getIntFromColor(85, 85, 85);
	public static final int black = FontHelper.getIntFromColor(0, 0, 0);

	public int errorDisplayTicks;
	public int errorDisplayTime = 300;
	public boolean disabled = false;

	public final T flux;
	public IFluxCommon common;

	public AbstractGuiTab(T tile, List<GuiTab> tabs) {
		super(new ContainerFlux(Minecraft.getMinecraft().player, tile));
		this.flux = tile;
		this.tabs = tabs;
		this.common = FluxNetworks.getClientCache().getNetwork(getNetworkID());
	}

	public abstract GuiTab getCurrentTab();

	@Override
	public void initGui() {
		super.initGui();
		int i = 0;
		for (GuiTab state : tabs) {
			buttonList.add(new NavigationButtons(this, state, -i, guiLeft + 2 + 18 * i, guiTop - 15));
			i++;
		}
	}

	public int getNetworkID() {
		return flux.getNetworkID();
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof NavigationButtons) {
			switchTab(((NavigationButtons) button).tab);
		}
	}

	public void switchTab(GuiTab tab) {
		if (tab != getCurrentTab()) {
			Object screen = tab.getGuiScreen(flux, tabs);
			FMLCommonHandler.instance().showGuiScreen(screen);
		}
	}

	public Object origin;

	public void setOrigin(Object origin) {
		this.origin = origin;
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		if (isCloseKey(i)) {
			if (origin != null) {
				FMLCommonHandler.instance().showGuiScreen(origin);
				return;
			}
			if (getCurrentTab() != GuiTab.INDEX) {
				FMLCommonHandler.instance().showGuiScreen(GuiTab.INDEX.getGuiScreen(flux, tabs));
				return;
			}
		}
		super.keyTyped(c, i);
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	//// RENDER METHODs \\\\

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
		Minecraft.getMinecraft().getTextureManager().bindTexture(getBackground());
		drawTexturedModalRect(x, y, 0, /* isSelected ? 178 : 166 */166, 154, 12);
		FontHelper.text(network.getCustomName(), x + 3, y + 2, isSelected ? Color.WHITE.getRGB() : Color.DARK_GRAY.getRGB());
	}

	public void renderNetwork(String networkName, AccessType access, int rgb, boolean isSelected, int x, int y) {
		color(1.0F, 1.0F, 1.0F, 1.0F);
		drawRect(x, y, x + 154, y + 12, rgb);
		Minecraft.getMinecraft().getTextureManager().bindTexture(scroller_flux_gui);
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

}
