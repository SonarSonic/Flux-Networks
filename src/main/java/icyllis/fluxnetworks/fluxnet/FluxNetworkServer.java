package icyllis.fluxnetworks.fluxnet;

import icyllis.fluxnetworks.api.network.*;
import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.util.NBTType;
import icyllis.fluxnetworks.fluxnet.manager.NetworkSetting;
import icyllis.fluxnetworks.fluxnet.manager.NetworkTransfer;
import icyllis.fluxnetworks.fluxnet.manager.RequestHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FluxNetworkServer implements IFluxNetwork {

    private final INetworkSetting setting = new NetworkSetting();
    private final INetworkTransfer transfer = new NetworkTransfer(this);
    private final IRequestHandler requestHandler = new RequestHandler(this);

    public HashMap<FluxCacheTypes, List<IFluxTile>> connections = new HashMap<>();

    public FluxNetworkServer() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFluxTile> List<T> getConnections(FluxCacheTypes<T> type) {
        return (List<T>) connections.computeIfAbsent(type, m -> new ArrayList<>());
    }

    @Override
    public INetworkSetting getNetworkSetting() {
        return setting;
    }

    @Override
    public INetworkTransfer getNetworkTransfer() {
        return transfer;
    }

    @Override
    public IRequestHandler getRequestHandler() {
        return requestHandler;
    }

    @Override
    public void tick() {
        requestHandler.tick();
        transfer.tick();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {

    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        return nbt;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRemoved() {
        getConnections(FluxCacheTypes.flux).forEach(flux -> MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Disconnected((IFluxTile) flux, this)));
        connections.clear();
        requestHandler.onRemoved();
        transfer.onRemoved();
    }
}
