package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.device.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
     * @return a list of logic types that the given device can work as
     */
    @Nonnull
    public static List<FluxLogicType> getValidTypes(IFluxDevice tile) {
        List<FluxLogicType> valid = new ArrayList<>();
        for (FluxLogicType type : FluxLogicType.values()) {
            if (type.clazz.isInstance(tile)) {
                valid.add(type);
            }
        }
        return valid;
    }
}
