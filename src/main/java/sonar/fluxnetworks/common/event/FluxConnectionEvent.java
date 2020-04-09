package sonar.fluxnetworks.common.event;

import net.minecraftforge.eventbus.api.Event;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxConnector;

public class FluxConnectionEvent extends Event {

    public final IFluxConnector flux;

    public FluxConnectionEvent(IFluxConnector flux) {
        super();
        this.flux = flux;
    }

    public static class Connected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Connected(IFluxConnector flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    public static class Disconnected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Disconnected(IFluxConnector flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }
}
