package sonar.flux.client;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityFlux;

public class GuiFluxPlug extends GuiFlux {
	//public static final ResourceLocation bground = new ResourceLocation("FluxNetworks:textures/gui/fluxPlug.png");

	public TileEntityFlux entity;	

	public GuiFluxPlug(EntityPlayer player, TileEntityFlux entity) {
		super(new ContainerFlux(player, entity, false), entity, player);
		this.entity = entity;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		/*
		if ((state==GuiState.INDEX)) {
			FontHelper.text(TextFormatting.UNDERLINE + FontHelper.translate("plug.sending"), 10, 27, midBlue);
			FontHelper.text(TextFormatting.UNDERLINE + FontHelper.translate("plug.receiving"), xSize / 2 + 8, 27, midBlue);
						
			IFluxCommon common = CommonNetworkCache.getNetwork(entity.networkOwner.getObject(), entity.playerName.getObject(), false, entity.networkID.getObject());
			INetworkStatistics networkStats = common.getStatistics();
			EnergyStats stats = networkStats.getLatestStats();
			
			FontHelper.text("Max: " + FontHelper.formatOutput(stats.maxReceived), 10, 40, lightBlue);	
			FontHelper.text("Max: " + FontHelper.formatOutput(stats.maxSent), xSize / 2 + 8, 40, lightBlue);	
			
			FontHelper.text("Last: " + TextFormatting.RESET + FontHelper.formatOutput(stats.received), 10, 50, lightBlue);			
			FontHelper.text("Last: " + TextFormatting.RESET + FontHelper.formatOutput(stats.sent), xSize / 2 + 8, 50, lightBlue);
			
			FontHelper.text("Plugs: " + TextFormatting.RESET + networkStats.getPlugCount(), 10, 60, lightBlue);
			FontHelper.text("Points: " + TextFormatting.RESET + networkStats.getPointCount(), xSize / 2 + 8, 60, lightBlue);

			if (entity.networkName.getObject().equals("NETWORK") || entity.networkName.getObject().isEmpty()) {
				FontHelper.text(TextFormatting.UNDERLINE + FontHelper.translate("network.notConnected"), 26, 9, midBlue);
			} else {
				FontHelper.text(TextFormatting.UNDERLINE + entity.networkName.getObject() + ": " + getNetworkType(AccessType.PRIVATE), 26, 9, midBlue);
			}
		}
		*/
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

}
