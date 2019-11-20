package fluxnetworks.connection;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.connection.manager.NetworkSettingManager;
import fluxnetworks.system.FluxNetworks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;

public class FluxDataHandler {

    public static final FluxDataHandler INSTANCE = new FluxDataHandler();

    private FluxNetworkData data;

    public void loadData(MinecraftServer server) {
        ServerWorld world = server.getWorld(DimensionType.OVERWORLD);
        data = world.getSavedData().getOrCreate(FluxNetworkData::new, FluxNetworkData.NETWORK_DATA);
        FluxNetworks.logger.info("FluxNetworkData has been successfully loaded");
    }

    public void releaseData() {
        if(data != null) {
            data = null;
            FluxNetworks.logger.info("FluxNetworkData has been unloaded");
        }
    }

    public IFluxNetwork createdNetwork() {
        IFluxNetwork network = new FluxNetworkServer();
        return network;
    }

    public Collection<IFluxNetwork> getAllNetworks() {
        return data.networks.values();
    }
}
