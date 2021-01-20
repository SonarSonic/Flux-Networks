package sonar.fluxnetworks.api.network;

import com.google.common.collect.Lists;
import sonar.fluxnetworks.api.tiles.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Logic types in flux networks
 */
public enum FluxLogicType {
    ANY(IFluxConnector.class),
    PLUG(IFluxPlug.class), // plug and storage
    POINT(IFluxPoint.class), // point, storage and controller
    STORAGE(IFluxStorage.class),
    CONTROLLER(IFluxController.class);

    private final Class<? extends IFluxConnector> clazz;

    FluxLogicType(Class<? extends IFluxConnector> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get valid logic types for a flux device
     *
     * @param tile the tile entity that extends IFluxDevice
     * @return a set of logic types that the given device can work as
     */
    @Nonnull
    public static Set<FluxLogicType> getValidTypes(IFluxConnector tile) {
        Set<FluxLogicType> valid = EnumSet.noneOf(FluxLogicType.class);
        for (FluxLogicType type : FluxLogicType.values()) {
            if (type.clazz.isInstance(tile)) {
                valid.add(type);
            }
        }
        return valid;
    }
}
