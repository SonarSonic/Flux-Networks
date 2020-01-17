package icyllis.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import icyllis.fluxnetworks.api.tile.*;

import java.util.ArrayList;
import java.util.List;

public class FluxCacheTypes<T extends IFluxTile> {

    public static final FluxCacheTypes flux = new FluxCacheTypes<>(IFluxTile.class);
    public static final FluxCacheTypes plug = new FluxCacheTypes<>(IFluxPlug.class);
    public static final FluxCacheTypes point = new FluxCacheTypes<>(IFluxPoint.class);
    public static final FluxCacheTypes storage = new FluxCacheTypes<>(IFluxStorage.class);
    public static final FluxCacheTypes controller = new FluxCacheTypes<>(IFluxController.class);

    public Class<T> clazz;

    public static final List<FluxCacheTypes> types = Lists.newArrayList(flux, plug, point, storage, controller);

    public FluxCacheTypes(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static List<FluxCacheTypes> getValidTypes(IFluxTile tile) {
        List<FluxCacheTypes> valid = new ArrayList<>();
        for (FluxCacheTypes handler : FluxCacheTypes.types) {
            if (handler.clazz.isInstance(tile)) {
                valid.add(handler);
            }
        }
        return valid;
    }
}
