package sonar.fluxnetworks.register;

import net.minecraft.util.Tuple;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;

import java.util.Map;

public class ProxyServer implements IProxy {

    public EnumFeedbackInfo getFeedback(boolean operation) {
        return null;
    }

    public void setFeedback(EnumFeedbackInfo info, boolean operation) {
    }

    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {
    }

    @Override
    public IFluxNetwork getNetwork(int networkID) {
        return FluxNetworkCache.INSTANCE.getNetwork(networkID);
    }
}
