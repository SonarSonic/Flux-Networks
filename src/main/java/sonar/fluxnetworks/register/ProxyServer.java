package sonar.fluxnetworks.register;

import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

import java.util.Map;

public class ProxyServer implements IProxy {

    public boolean baublesLoaded;
    public boolean ocLoaded;

    public int admin_viewing_network_id = -1;
    public IFluxNetwork admin_viewing_network = FluxNetworkInvalid.INSTANCE;

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run this on the client");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run this on the client");
    }


    public EnumFeedbackInfo getFeedback(boolean operation) {
        return null;
    }

    public void setFeedback(EnumFeedbackInfo info, boolean operation) {}

    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {}

    @Override
    public int getAdminViewingNetworkID(){
        return admin_viewing_network_id;
    }

    @Override
    public IFluxNetwork getAdminViewingNetwork(){
        return admin_viewing_network;
    }

    @Override
    public IFluxNetwork getNetwork(int networkID) {
        return FluxNetworkCache.INSTANCE.getNetwork(networkID);
    }
}
