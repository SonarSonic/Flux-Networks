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

    public static final List<FluxCacheType<? extends IFluxConnector>> TYPES = Lists.newArrayList(FLUX, PLUG, POINT, STORAGE, CONTROLLER);

    public FluxCacheType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static List<FluxCacheType<IFluxConnector>> getValidTypes(IFluxConnector tile) {
        List<FluxCacheType<IFluxConnector>> valid = new ArrayList<>();
        for (FluxCacheType<? extends IFluxConnector> type : FluxCacheType.TYPES) {
            if (type.clazz.isInstance(tile)) {
                valid.add((FluxCacheType<IFluxConnector>) type);
            }
        }
        return valid;
    }
}
