package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import sonar.fluxnetworks.api.tiles.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluxCacheType<T extends IFluxConnector> {

    public static final FluxCacheType<IFluxConnector> FLUX = new FluxCacheType<>(IFluxConnector.class);
    public static final FluxCacheType<IFluxPlug> PLUG = new FluxCacheType<>(IFluxPlug.class);
    public static final FluxCacheType<IFluxPoint> POINT = new FluxCacheType<>(IFluxPoint.class);
    public static final FluxCacheType<IFluxStorage> STORAGE = new FluxCacheType<>(IFluxStorage.class);
    public static final FluxCacheType<IFluxController> CONTROLLER = new FluxCacheType<>(IFluxController.class);

    private final Class<T> clazz;

    public static final List<FluxCacheType<?>> TYPES = Lists.newArrayList(FLUX, PLUG, POINT, STORAGE, CONTROLLER);

    public FluxCacheType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T extends IFluxConnector> List<FluxCacheType<T>> getValidTypes(T tile) {
        List<FluxCacheType<T>> valid = new ArrayList<>();
        for (FluxCacheType<?> type : FluxCacheType.TYPES) {
            if (type.clazz.isInstance(tile)) {
                valid.add((FluxCacheType<T>) type);
            }
        }
        return valid;
    }
}
