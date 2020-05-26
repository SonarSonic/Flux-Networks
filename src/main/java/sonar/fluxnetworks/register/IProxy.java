package sonar.fluxnetworks.register;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;

import java.util.Map;

public interface IProxy {

    World getClientWorld();

    PlayerEntity getClientPlayer();

    EnumFeedbackInfo getFeedback(boolean operation);

    void setFeedback(EnumFeedbackInfo info, boolean operation);

    void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache);

    IFluxNetwork getNetwork(int networkID);

    default void setAdminViewingNetworkID(int set){}

    default void setAdminViewingNetwork(IFluxNetwork set){}

    default void setDetailedNetworkView(boolean set){}

    default boolean getDetailedNetworkView(){
        return false;
    }

    default int getAdminViewingNetworkID(){
        return -1;
    }

    default IFluxNetwork getAdminViewingNetwork(){
        return FluxNetworkInvalid.INSTANCE;
    }
}
