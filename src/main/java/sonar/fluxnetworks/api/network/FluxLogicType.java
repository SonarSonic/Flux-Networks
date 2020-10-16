package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.device.*;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

/**
 * Logic types in flux networks
 */
public enum FluxLogicType {
    ANY(IFluxDevice.class),
    PLUG(IFluxPlug.class), // plug and storage
    POINT(IFluxPoint.class), // point, storage and controller
    STORAGE(IFluxStorage.class),
    CONTROLLER(IFluxController.class);

    private final Class<? extends IFluxDevice> clazz;

    FluxLogicType(Class<? extends IFluxDevice> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get valid logic types for a flux device
     *
     * @param tile the tile entity that extends IFluxDevice
     * @return a set of logic types that the given device can work as
     */
    @Nonnull
    public static Set<FluxLogicType> getValidTypes(IFluxDevice tile) {
        Set<FluxLogicType> valid = EnumSet.noneOf(FluxLogicType.class);
        for (FluxLogicType type : FluxLogicType.values()) {
            if (type.clazz.isInstance(tile)) {
                valid.add(type);
            }
        }
        return valid;
    }
}
