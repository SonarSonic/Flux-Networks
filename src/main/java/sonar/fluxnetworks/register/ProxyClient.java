package sonar.fluxnetworks.register;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Tuple;

import java.util.Map;

public class ProxyClient implements IProxy {

    private EnumFeedbackInfo feedbackInfo = EnumFeedbackInfo.NONE; // Text message.
    private EnumFeedbackInfo feedbackInfoSuccess = EnumFeedbackInfo.NONE; // Special operation.
    private int feedbackTimer = 0;

    public int admin_viewing_network_id = -1;
    public IFluxNetwork admin_viewing_network = FluxNetworkInvalid.instance;
    public boolean detailed_network_view;

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }


    public void onServerStopped() {
        FluxColorHandler.reset();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        FluxColorHandler.sendRequests();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            if(feedbackInfo.hasFeedback()) {
                feedbackTimer++;
                if(feedbackTimer >= 60) {
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
        if(operation) {
            this.feedbackInfoSuccess = info;
        } else {
            this.feedbackInfo = info;
        }
        feedbackTimer = 0;
    }

    @Override
    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {
        FluxColorHandler.receiveCache(cache);
    }

    @Override
    public void setDetailedNetworkView(boolean set){
        detailed_network_view = set;
    }

    @Override
    public boolean getDetailedNetworkView(){
        return detailed_network_view;
    }

    @Override
    public IFluxNetwork getNetwork(int networkID) {
        return FluxNetworkCache.instance.getClientNetwork(networkID);
    }

    @Override
    public int getAdminViewingNetworkID(){
        return admin_viewing_network_id;
    }

    @Override
    public IFluxNetwork getAdminViewingNetwork() {
        return admin_viewing_network;
    }

    @Override
    public void setAdminViewingNetworkID(int set){
        this.admin_viewing_network_id = set;
    }

    @Override
    public void setAdminViewingNetwork(IFluxNetwork set){
        this.admin_viewing_network = set;
    }

}