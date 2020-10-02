package sonar.fluxnetworks.register;

import net.minecraft.util.Tuple;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;

import java.util.Map;

public interface IProxy {

    EnumFeedbackInfo getFeedback(boolean operation);

    void setFeedback(EnumFeedbackInfo info, boolean operation);

    void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache);

    IFluxNetwork getNetwork(int networkID);

    default void setDetailedNetworkView(boolean set) {
    }

    default boolean getDetailedNetworkView() {
        return false;
    }

    default void setAdminViewingNetwork(IFluxNetwork set) {
    }

    default IFluxNetwork getAdminViewingNetwork() {
        return FluxNetworkInvalid.INSTANCE;
    }
}
