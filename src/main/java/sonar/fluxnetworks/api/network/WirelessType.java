package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.translate.Translation;

import javax.annotation.Nonnull;

public enum WirelessType {
    ENABLE_WIRELESS(FluxTranslate.ENABLE_WIRELESS),
    RIGHT_HAND(FluxTranslate.RIGHT_HAND),
    LEFT_HAND(FluxTranslate.LEFT_HAND),
    HOT_BAR(FluxTranslate.HOT_BAR),
    ARMOR_SLOT(FluxTranslate.ARMOR),
    BAUBLES(FluxTranslate.BAUBLES),
    INVENTORY(FluxTranslate.INVENTORY);

    private final Translation typeName;

    WirelessType(Translation typeName){
        this.typeName = typeName;
    }

    public boolean isActivated(int setting){
        if (this == INVENTORY) {
            return false;
        }
        return (setting >> ordinal() & 1) == 1;
    }

    public boolean isActivated(@Nonnull IFluxNetwork network) {
        return isActivated(network.getSetting(NetworkSettings.NETWORK_WIRELESS));
    }

    public String getTranslatedName(){
        return typeName.t();
    }
}
