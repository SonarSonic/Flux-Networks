package sonar.flux.common.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import sonar.flux.api.ConnectionSettings;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFlux;

public class FluxConnectionEvent extends Event {

    public final IFlux flux;

    public FluxConnectionEvent(IFlux flux) {
        super();
        this.flux = flux;
    }

    public static class Connected extends FluxConnectionEvent{

        public final IFluxNetwork network;

        public Connected(IFlux flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    public static class Disconnected extends FluxConnectionEvent{

        public final IFluxNetwork network;

        public Disconnected(IFlux flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    public static class SettingChanged extends FluxConnectionEvent{

        public final ConnectionSettings setting;

        public SettingChanged(IFlux flux, ConnectionSettings setting) {
            super(flux);
            this.setting = setting;
        }

    }
}
