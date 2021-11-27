package sonar.fluxnetworks.common.test;

import sonar.fluxnetworks.api.device.*;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

/**
 * Logical types in flux networks.
 */
@Deprecated
public enum FluxLogicalType {
    ANY(IFluxDevice.class),
    PLUG(IFluxPlug.class), // plug and storage
    POINT(IFluxPoint.class), // point, storage and controller
    STORAGE(IFluxStorage.class),
    CONTROLLER(IFluxController.class);

    public final Class<? extends IFluxDevice> mType;

    FluxLogicalType(Class<? extends IFluxDevice> type) {
        mType = type;
    }

    /**
     * Get valid logic types for a flux device
     *
     * @param tile the tile entity that extends IFluxDevice
     * @return a set of logic types that the given device can work as
     */
    @Nonnull
    public static Set<FluxLogicalType> getValidTypes(IFluxDevice tile) {
        Set<FluxLogicalType> valid = EnumSet.noneOf(FluxLogicalType.class);
        for (FluxLogicalType type : FluxLogicalType.values()) {
            if (type.mType.isInstance(tile)) {
                valid.add(type);
            }
        }
        return valid;
    }
}
