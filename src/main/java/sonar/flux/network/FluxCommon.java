package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.utils.IGuiItem;
import sonar.flux.FluxNetworks;

public class FluxCommon implements IGuiHandler {

	public void registerRenderThings() {}

	public static void registerPackets() {
		FluxNetworks.network.registerMessage(PacketFluxButton.Handler.class, PacketFluxButton.class, 0, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketFluxNetworkList.Handler.class, PacketFluxNetworkList.class, 1, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketFluxConnectionsList.Handler.class, PacketFluxConnectionsList.class, 2, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketFluxError.Handler.class, PacketFluxError.class, 3, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketConfiguratorSettings.Handler.class, PacketConfiguratorSettings.class, 4, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketNetworkStatistics.Handler.class, PacketNetworkStatistics.class, 5, Side.CLIENT);
	}

	public void preInit(FMLPreInitializationEvent event) {}

	public void init(FMLInitializationEvent event) {}

	public void postInit(FMLPostInitializationEvent evt) {}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ItemStack equipped = player.getHeldItemMainhand();
		if (!equipped.isEmpty()) {
			switch (ID) {
			case IGuiItem.ID:
				return ((IGuiItem) equipped.getItem()).getGuiContainer(player, equipped);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ItemStack equipped = player.getHeldItemMainhand();
		if (!equipped.isEmpty()) {
			switch (ID) {
			case IGuiItem.ID:
				return ((IGuiItem) equipped.getItem()).getGuiScreen(player, equipped);
			}
		}
		return null;
	}
}
