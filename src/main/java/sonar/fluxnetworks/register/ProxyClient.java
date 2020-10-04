package sonar.fluxnetworks.register;

import net.minecraft.util.Tuple;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;

import java.util.Map;

public class ProxyClient implements IProxy {

    private EnumFeedbackInfo feedbackInfo        = EnumFeedbackInfo.NONE; // Text message.
    private EnumFeedbackInfo feedbackInfoSuccess = EnumFeedbackInfo.NONE; // Special operation.

    private int feedbackTimer = 0;

    public IFluxNetwork admin_viewing_network = FluxNetworkInvalid.INSTANCE;

    public boolean detailed_network_view;

    //TODO move to client cache manager or UI data manager
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (feedbackInfo.hasFeedback()) {
                feedbackTimer++;
                if (feedbackTimer >= 60) {
                    feedbackTimer = 0;
                    setFeedback(EnumFeedbackInfo.NONE, false);
                }
            }
        }
    }

    @Override
    public EnumFeedbackInfo getFeedback(boolean operation) {
        return operation ? feedbackInfoSuccess : feedbackInfo;
    }

    @Override
    public void setFeedback(EnumFeedbackInfo info, boolean operation) {
        if (operation) {
            this.feedbackInfoSuccess = info;
        } else {
            this.feedbackInfo = info;
        }
        feedbackTimer = 0;
    }

    @Override
    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {
        //FluxColorHandler.INSTANCE.receiveCache(cache);
    }

    @Override
    public void setDetailedNetworkView(boolean set) {
        detailed_network_view = set;
    }

    @Override
    public boolean getDetailedNetworkView() {
        return detailed_network_view;
    }

    @Override
    public IFluxNetwork getNetwork(int networkID) {
        return FluxClientCache.getNetwork(networkID);
    }

    @Override
    public IFluxNetwork getAdminViewingNetwork() {
        return admin_viewing_network;
    }

    @Override
    public void setAdminViewingNetwork(IFluxNetwork set) {
        this.admin_viewing_network = set;
    }
}