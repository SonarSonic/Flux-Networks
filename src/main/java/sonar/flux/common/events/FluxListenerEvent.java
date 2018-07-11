package sonar.flux.common.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFluxListenable;

public class FluxListenerEvent extends Event {

    public final IFluxListenable flux;

    public FluxListenerEvent(IFluxListenable flux) {
        super();
        this.flux = flux;
    }

    public static class AddConnectionListener extends FluxConnectionEvent{

        public final IFluxNetwork network;

        public AddConnectionListener(IFluxListenable flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

    public static class RemoveConnectionListener extends FluxConnectionEvent{

        public final IFluxNetwork network;

        public RemoveConnectionListener(IFluxListenable flux, IFluxNetwork network) {
            super(flux);
            this.network = network;
        }
    }

}