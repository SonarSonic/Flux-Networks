package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.text.FluxTranslate;

import javax.annotation.Nonnull;

public enum WirelessType {
    ENABLE_WIRELESS(FluxTranslate.ENABLE_WIRELESS),
    MAIN_HAND(FluxTranslate.MAIN_HAND),
    OFF_HAND(FluxTranslate.OFF_HAND),
    HOT_BAR(FluxTranslate.HOT_BAR),
    ARMOR(FluxTranslate.ARMOR),
    CURIOS(FluxTranslate.CURIOS),
    INVENTORY(FluxTranslate.INVENTORY);

    private final FluxTranslate typeName;

    WirelessType(FluxTranslate typeName) {
        this.typeName = typeName;
    }

    public boolean isActivated(int setting) {
        if (this == INVENTORY) {
            return false;
        }
        return (setting >> ordinal() & 1) == 1;
    }

    public boolean isActivated(@Nonnull IFluxNetwork network) {
        return isActivated(network.getWirelessMode());
    }

    @Nonnull
    public String getTranslatedName() {
        return typeName.t();
    }
}
