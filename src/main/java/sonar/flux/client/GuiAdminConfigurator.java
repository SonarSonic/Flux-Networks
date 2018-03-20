package sonar.flux.client;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.GuiSelectionGrid;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.common.containers.ContainerAdminConfigurator;
import sonar.flux.network.ClientNetworkCache;

public class GuiAdminConfigurator extends GuiSelectionGrid<IFluxNetwork> {

	public static final ResourceLocation bground = new ResourceLocation("fluxnetworks:textures/gui/admin_configurator.png");
	public static final ResourceLocation bars = new ResourceLocation("fluxnetworks:textures/gui/admin_configurator_bars.png");

	public GuiAdminConfigurator(EntityPlayer player) {
		super(new ContainerAdminConfigurator(player));
		yPos = 24;
		xPos = 3;
		eWidth = 250;
		eHeight = 32;
		gWidth = 1;
		gHeight = 12;
	}

	@Override
	public void onGridClicked(IFluxNetwork element, int pos, int button, boolean empty) {

	}

	@Override
	public void renderGridElement(IFluxNetwork element, int x, int y, int slot) {
		this.bindTexture(bars);
		this.drawTexturedModalRect(xPos+ x, yPos+y*eHeight, 0, 0, 250, 32);
		FontHelper.text(element.getNetworkName(), xPos+ x + 4, yPos+y*eHeight +6, element.getNetworkColour().getRGB());
		FontHelper.text("Owner: " + element.getCachedPlayerName(), xPos+ x + 4, yPos+y*eHeight +18, 0);
		FontHelper.text("" + element.getAccessType(), xPos+ x + 4 + 200, yPos+y*eHeight +6, 0);
		FontHelper.text("UID: " + element.getNetworkID(), xPos+ x + 4 + 200, yPos+y*eHeight +18, 0);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre("Admin Configurator", xSize, 10, AbstractGuiTab.midBlue);
	}

	@Override
	public void renderElementToolTip(IFluxNetwork element, int x, int y) {
		
	}

	@Override
	public List<IFluxNetwork> getGridList() {
		return ClientNetworkCache.instance().getAllNetworks();
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

}
