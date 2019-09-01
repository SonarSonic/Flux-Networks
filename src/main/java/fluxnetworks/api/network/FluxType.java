package fluxnetworks.api.network;

import com.google.common.collect.Lists;
import fluxnetworks.api.tileentity.*;

import java.util.ArrayList;
import java.util.List;

public class FluxType<T extends IFluxConnector> {

    public static final FluxType flux = new FluxType<>(IFluxConnector.class);
    public static final FluxType plug = new FluxType<>(IFluxPlug.class);
    public static final FluxType point = new FluxType<>(IFluxPoint.class);
    public static final FluxType storage = new FluxType<>(IFluxStorage.class);
    public static final FluxType controller = new FluxType<>(IFluxController.class);

    public Class<T> clazz;

    public static final List<FluxType> types = Lists.newArrayList(flux, plug, point, storage, controller);

    public FluxType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static List<FluxType> getValidTypes(IFluxConnector tile) {
        List<FluxType> valid = new ArrayList<>();
        for (FluxType handler : FluxType.types) {
            if (handler.clazz.isInstance(tile)) {
                valid.add(handler);
            }
        }
        return valid;
    }
}
