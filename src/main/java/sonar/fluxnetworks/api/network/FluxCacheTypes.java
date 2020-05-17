package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import sonar.fluxnetworks.api.tiles.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluxCacheTypes<T extends IFluxConnector> {

    public static final FluxCacheTypes<IFluxConnector> FLUX = new FluxCacheTypes<>(IFluxConnector.class);
    public static final FluxCacheTypes<IFluxPlug> PLUG = new FluxCacheTypes<>(IFluxPlug.class);
    public static final FluxCacheTypes<IFluxPoint> POINT = new FluxCacheTypes<>(IFluxPoint.class);
    public static final FluxCacheTypes<IFluxStorage> STORAGE = new FluxCacheTypes<>(IFluxStorage.class);
    public static final FluxCacheTypes<IFluxController> CONTROLLER = new FluxCacheTypes<>(IFluxController.class);

    private final Class<T> clazz;

    public static final List<FluxCacheTypes<?>> TYPES = Lists.newArrayList(FLUX, PLUG, POINT, STORAGE, CONTROLLER);

    public FluxCacheTypes(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T extends IFluxConnector> List<FluxCacheTypes<T>> getValidTypes(T tile) {
        List<FluxCacheTypes<T>> valid = new ArrayList<>();
        for (FluxCacheTypes<?> type : FluxCacheTypes.TYPES) {
            if (type.clazz.isInstance(tile)) {
                valid.add((FluxCacheTypes<T>) type);
            }
        }
        return valid;
    }
}
