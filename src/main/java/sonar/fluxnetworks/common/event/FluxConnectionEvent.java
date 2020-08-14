package sonar.fluxnetworks.common.event;

import net.minecraftforge.eventbus.api.Event;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxDevice;

public class FluxConnectionEvent extends Event {

    public final IFluxDevice flux;

    public FluxConnectionEvent(IFluxDevice flux) {
        this.flux = flux;
    }

    public static class Connected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Connected(IFluxDevice flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    public static class Disconnected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Disconnected(IFluxDevice flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }
}
