package sonar.flux.api.network;

import com.google.common.collect.Lists;
import sonar.flux.api.tiles.*;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.FluxHelper;

import java.util.ArrayList;
import java.util.List;

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
            network.markConnectionsForSorting();
        }

    };
    public static final FluxCache plug = new FluxCache(IFluxPlug.class){

        public void update(BasicFluxNetwork network) {
            //network.markConnectionsForSorting();
        }
    };
    public static final FluxCache point = new FluxCache(IFluxPoint.class){

        public void update(BasicFluxNetwork network) {
            //network.markConnectionsForSorting();
        }
    };
    public static final FluxCache storage = new FluxCache(IFluxStorage.class);
    public static final FluxCache controller = new FluxCache(IFluxController.class) {

        public void update(BasicFluxNetwork network) {
            List<IFluxController> controllers = network.getConnections(FluxCache.controller);
            if (controllers.size() > 1) {
                boolean set = false;
                for (IFluxController controller : controllers) {
                    controller.disconnect(network);
                }
            }
           // network.markConnectionsForSorting();
        }
    };

    public Class<T> clazz;

    public static final List<FluxCache> types = Lists.newArrayList(flux, plug, point, storage, controller);

    public FluxCache(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static List<FluxCache> getValidTypes(IFluxListenable tile) {
        List<FluxCache> valid = new ArrayList<>();
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