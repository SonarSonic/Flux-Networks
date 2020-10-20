package sonar.fluxnetworks.api.gui;

import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.text.FluxTranslate;

import javax.annotation.Nonnull;

public enum EnumChargingType {
    ENABLE_WIRELESS(FluxTranslate.ENABLE_WIRELESS),
    RIGHT_HAND(FluxTranslate.RIGHT_HAND),
    LEFT_HAND(FluxTranslate.LEFT_HAND),
    HOT_BAR(FluxTranslate.HOT_BAR),
    ARMOR_SLOT(FluxTranslate.ARMOR),
    BAUBLES(FluxTranslate.BAUBLES),
    INVENTORY(FluxTranslate.INVENTORY);

    private final FluxTranslate typeName;

    EnumChargingType(FluxTranslate typeName) {
        this.typeName = typeName;
    }

    public boolean isActivated(int setting) {
        if (this == INVENTORY) {
            return false;
        }
        return (setting >> ordinal() & 1) == 1;
    }

    public boolean isActivated(@Nonnull IFluxNetwork network) {
        return false/*isActivated(network.getSetting(NetworkSettings.NETWORK_WIRELESS))*/;
    }

    @Nonnull
    public String getTranslatedName() {
        return typeName.t();
    }
}
