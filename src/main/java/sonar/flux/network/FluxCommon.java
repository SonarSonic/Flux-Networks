package sonar.flux.network;

import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import sonar.flux.FluxNetworks;

import java.util.Map;

public class FluxCommon {

	public void registerRenderThings() {}

	public static void registerPackets() {
		FluxNetworks.network.registerMessage(PacketFluxButton.Handler.class, PacketFluxButton.class, 0, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketFluxNetworkList.Handler.class, PacketFluxNetworkList.class, 1, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketFluxConnectionsList.Handler.class, PacketFluxConnectionsList.class, 2, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketFluxError.Handler.class, PacketFluxError.class, 3, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketConfiguratorSettings.Handler.class, PacketConfiguratorSettings.class, 4, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketNetworkStatistics.Handler.class, PacketNetworkStatistics.class, 5, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketColourRequest.Handler.class, PacketColourRequest.class, 6, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketColourCache.Handler.class, PacketColourCache.class, 7, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketClearNetwork.Handler.class, PacketClearNetwork.class, 8, Side.CLIENT);
	}

	public void preInit(FMLPreInitializationEvent event) {}

	public void init(FMLInitializationEvent event) {}

	public void postInit(FMLPostInitializationEvent evt) {}

	public void shutdown(FMLServerStoppedEvent event) {}

	public void receiveColourCache(Map<Integer, Tuple<Integer, String>> cache){}

	public void clearNetwork(int networkID){}
}
