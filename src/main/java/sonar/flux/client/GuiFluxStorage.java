package sonar.flux.client;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxCommon;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityStorage;

public class GuiFluxStorage extends GuiFlux {
	//public static final ResourceLocation bground = new ResourceLocation("FluxNetworks:textures/gui/fluxPlug.png");

	public TileEntityStorage entity;

	public GuiFluxStorage(EntityPlayer player, TileEntityStorage entity) {
		super(new ContainerFlux(player, entity, false), entity, player);
		this.entity = entity;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		/*
		 * if ((state==GuiState.INDEX)) { FontHelper.text(TextFormatting.UNDERLINE + FontHelper.translate("plug.sending"), 10, 27, midBlue); FontHelper.text(TextFormatting.UNDERLINE + FontHelper.translate("plug.receiving"), xSize / 2 + 8, 27, midBlue);
		 * 
		 * IFluxCommon common = CommonNetworkCache.getNetwork(entity.networkOwner.getObject(), entity.playerName.getObject(), false, entity.networkID.getObject()); INetworkStatistics networkStats = common.getStatistics(); EnergyStats stats = networkStats.getLatestStats();
		 * 
		 * FontHelper.text("Max: " + FontHelper.formatOutput(stats.maxReceived), 10, 40, lightBlue); FontHelper.text("Max: " + FontHelper.formatOutput(stats.maxSent), xSize / 2 + 8, 40, lightBlue);
		 * 
		 * FontHelper.text("Last: " + TextFormatting.RESET + FontHelper.formatOutput(stats.received), 10, 50, lightBlue); FontHelper.text("Last: " + TextFormatting.RESET + FontHelper.formatOutput(stats.sent), xSize / 2 + 8, 50, lightBlue);
		 * 
		 * FontHelper.text("Plugs: " + TextFormatting.RESET + networkStats.getPlugCount(), 10, 60, lightBlue); FontHelper.text("Points: " + TextFormatting.RESET + networkStats.getPointCount(), xSize / 2 + 8, 60, lightBlue);
		 * 
		 * if (entity.networkName.getObject().equals("NETWORK") || entity.networkName.getObject().isEmpty()) { FontHelper.text(TextFormatting.UNDERLINE + FontHelper.translate("network.notConnected"), 26, 9, midBlue); } else { FontHelper.text(TextFormatting.UNDERLINE + entity.networkName.getObject() + ": " + getNetworkType(AccessType.PRIVATE), 26, 9, midBlue); } }
		 */
		if (state == GuiState.INDEX) {
			renderEnergyBar(14, 90, entity.storage.getEnergyStored(), entity.storage.getMaxEnergyStored(), midBlue, FontHelper.getIntFromColor(41, 94, 220));
			IFluxCommon common = FluxNetworks.getClientCache().getNetwork(tile.networkID.getObject());
			int colour = common.getNetworkColour().getRGB();
			renderEnergyBar(14, 130, common.getEnergyAvailable(), common.getMaxEnergyStored(), colour, colour);
			FontHelper.text("Local Buffer: ", 14, 80, Color.DARK_GRAY.getRGB());
			FontHelper.text("Network Buffer: " + (common.getEnergyAvailable() != 0 ? +(entity.storage.getEnergyStored() * 100 / common.getEnergyAvailable()) + " %" : ""), 14, 120, colour);
			//FontHelper.text(, 14, 110, Color.DARK_GRAY.getRGB());
		}

		super.drawGuiContainerForegroundLayer(x, y);
	}

}
