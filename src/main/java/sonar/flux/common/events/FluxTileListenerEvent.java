package sonar.flux.common.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFluxListenable;

public class FluxTileListenerEvent extends Event {

    public final IFluxListenable flux;
    public final IFluxNetwork network;

    public FluxTileListenerEvent(IFluxListenable flux, IFluxNetwork network) {
        super();
        this.flux = flux;
        this.network = network;
    }

    public static class AddConnectionListener extends FluxTileListenerEvent{

        public AddConnectionListener(IFluxListenable flux, IFluxNetwork network) {
            super(flux, network);
        }
    }

    public static class RemoveConnectionListener extends FluxTileListenerEvent{

        public RemoveConnectionListener(IFluxListenable flux, IFluxNetwork network) {
            super(flux, network);
        }
    }

}