package icyllis.fluxnetworks.api.network;

import icyllis.fluxnetworks.api.tile.IFluxTile;
import net.minecraftforge.eventbus.api.Event;

public class FluxConnectionEvent extends Event {

    public final IFluxTile flux;

    public FluxConnectionEvent(IFluxTile flux) {
        super();
        this.flux = flux;
    }

    public static class Connected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Connected(IFluxTile flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    public static class Disconnected extends FluxConnectionEvent {

        public final IFluxNetwork network;

        public Disconnected(IFluxTile flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }
}
