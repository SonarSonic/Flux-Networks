package sonar.fluxnetworks.common.test;

import net.minecraftforge.eventbus.api.Event;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.IFluxNetwork;

@Deprecated
public class FluxConnectionEvent extends Event {

    public final IFluxDevice flux;

    public FluxConnectionEvent(IFluxDevice flux) {
        this.flux = flux;
    }

    @Deprecated
    public static class Connected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Connected(IFluxDevice flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    @Deprecated
    public static class Disconnected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Disconnected(IFluxDevice flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }
}
