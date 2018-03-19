package sonar.flux.api.network;

import java.util.List;

import com.google.common.collect.Lists;

import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.api.tiles.IFluxStorage;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.FluxHelper;

public class FluxCache<T extends IFluxListenable> {

    public static final FluxCache flux = new FluxCache(IFluxListenable.class) {

        public void connect(BasicFluxNetwork network, IFluxListenable flux) {
            flux.connect(network);
        }

        public void disconnect(BasicFluxNetwork network, IFluxListenable flux) {
            flux.disconnect(network);
        }

        public void update(BasicFluxNetwork network) {
            network.setHasConnections(!network.getConnections(this).isEmpty());
        }

    };
    public static final FluxCache plug = new FluxCache(IFluxPlug.class);
    public static final FluxCache point = new FluxCache(IFluxPoint.class);
    public static final FluxCache storage = new FluxCache(IFluxStorage.class) {

        public void update(BasicFluxNetwork network) {
            network.resetStorageValues();
            controller.update(network); //update priorities
            network.updateFluxTallies();
        }

    };
    public static final FluxCache controller = new FluxCache(IFluxController.class) {

        public void update(BasicFluxNetwork network) {
            List<IFluxController> controllers = network.getConnections(FluxCache.controller);
            if (controllers.size() > 1) {
                boolean set = false;
                for (IFluxController controller : controllers) {
                    if (set && controller.canTransfer()) {
                        set = true;
                    } else {
                        controller.disconnect(network);
                    }
                }
            }
            if (controllers.size() > 0) {
                IFluxController controller = controllers.get(0);
                FluxHelper.sortConnections(network.getConnections(FluxCache.plug), controller.getSendMode());
                FluxHelper.sortConnections(network.getConnections(FluxCache.point), controller.getReceiveMode());
            }
        }
    };

    public Class<T> clazz;

    public static final List<FluxCache> types = Lists.newArrayList(flux, plug, point, storage, controller);

    public FluxCache(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static List<FluxCache> getValidTypes(IFluxListenable tile) {
        List<FluxCache> valid = Lists.newArrayList();
        for (FluxCache handler : FluxCache.types) {
            if (handler.clazz.isInstance(tile)) {
                valid.add(handler);
            }
        }
        return valid;
    }

    public void update(BasicFluxNetwork network) {
    }

    public void connect(BasicFluxNetwork network, IFluxListenable flux) {
    }

    public void disconnect(BasicFluxNetwork network, IFluxListenable flux) {
    }
}