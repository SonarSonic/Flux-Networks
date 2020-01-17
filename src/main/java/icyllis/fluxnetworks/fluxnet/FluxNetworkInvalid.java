package icyllis.fluxnetworks.fluxnet;

import icyllis.fluxnetworks.api.network.*;
import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.util.NBTType;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public class FluxNetworkInvalid implements IFluxNetwork {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    @Override
    public INetworkSetting getNetworkSetting() {
        return null;
    }

    @Override
    public INetworkTransfer getNetworkTransfer() {
        return null;
    }

    @Override
    public IRequestHandler getRequestHandler() {
        return null;
    }

    @Override
    public <T extends IFluxTile> List<T> getConnections(FluxCacheTypes<T> type) {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void onRemoved() {

    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {

    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        return nbt;
    }
}
