package sonar.flux.common.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.NetworkSettings;

public class FluxNetworkEvent extends Event {

    public final IFluxNetwork network;

    public FluxNetworkEvent(IFluxNetwork network){
        this.network = network;
    }

    public static class SettingsChanged extends FluxNetworkEvent{

        public SettingsChanged(IFluxNetwork network){
            super(network);
        }

        public boolean hasSettingChanged(NetworkSettings setting){
            return network.getSyncSetting(setting).isDirty();
        }

    }
}
