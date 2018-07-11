package sonar.flux.api.network;

import com.google.common.collect.Lists;
import sonar.flux.api.tiles.*;

import java.util.ArrayList;
import java.util.List;

public class FluxCache<T extends IFluxListenable> {

    public static final FluxCache flux = new FluxCache<>(IFluxListenable.class);
    public static final FluxCache plug = new FluxCache<>(IFluxPlug.class);
    public static final FluxCache point = new FluxCache<>(IFluxPoint.class);
    public static final FluxCache storage = new FluxCache<>(IFluxStorage.class);
    public static final FluxCache controller = new FluxCache<>(IFluxController.class);

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
}