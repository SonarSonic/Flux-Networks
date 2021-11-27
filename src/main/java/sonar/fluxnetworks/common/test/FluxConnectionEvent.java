package sonar.fluxnetworks.common.test;

import net.minecraftforge.eventbus.api.Event;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.common.connection.FluxNetwork;

@Deprecated
public class FluxConnectionEvent extends Event {

    public final IFluxDevice flux;

    public FluxConnectionEvent(IFluxDevice flux) {
        this.flux = flux;
    }

    @Deprecated
    public static class Connected extends FluxConnectionEvent {

        public final FluxNetwork network;

        public Connected(IFluxDevice flux, FluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    @Deprecated
    public static class Disconnected extends FluxConnectionEvent {

        public final FluxNetwork network;

        public Disconnected(IFluxDevice flux, FluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }
}
