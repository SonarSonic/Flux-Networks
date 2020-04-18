package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import sonar.fluxnetworks.api.tiles.*;

import java.util.ArrayList;
import java.util.List;

public class FluxCacheTypes<T extends IFluxConnector> {

    public static final FluxCacheTypes flux = new FluxCacheTypes<>(IFluxConnector.class);
    public static final FluxCacheTypes<IFluxPlug> plug = new FluxCacheTypes<>(IFluxPlug.class);
    public static final FluxCacheTypes<IFluxPoint> point = new FluxCacheTypes<>(IFluxPoint.class);
    public static final FluxCacheTypes<IFluxStorage> storage = new FluxCacheTypes<>(IFluxStorage.class);
    public static final FluxCacheTypes<IFluxController> controller = new FluxCacheTypes<>(IFluxController.class);

    public Class<T> clazz;

    public static final List<FluxCacheTypes<?>> types = Lists.newArrayList(flux, plug, point, storage, controller);

    public FluxCacheTypes(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static List<FluxCacheTypes> getValidTypes(IFluxConnector tile) {
        List<FluxCacheTypes> valid = new ArrayList<>();
        for (FluxCacheTypes<?> handler : FluxCacheTypes.types) {
            if (handler.clazz.isInstance(tile)) {
                valid.add(handler);
            }
        }
        return valid;
    }
}
