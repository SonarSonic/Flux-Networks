package sonar.fluxnetworks.api.gui;

import sonar.fluxnetworks.api.translate.FluxTranslate;

public enum EnumChargingTypes {

    ENABLE_WIRELESS(FluxTranslate.ENABLE_WIRELESS),
    RIGHT_HAND(FluxTranslate.RIGHT_HAND),
    LEFT_HAND(FluxTranslate.LEFT_HAND),
    HOT_BAR(FluxTranslate.HOT_BAR),
    ARMOR_SLOT(FluxTranslate.ARMOR),
    BAUBLES(FluxTranslate.BAUBLES),
    INVENTORY(FluxTranslate.INVENTORY);

    FluxTranslate typeName;

    EnumChargingTypes(FluxTranslate typeName){
        this.typeName = typeName;
    }

    public boolean isActivated(int setting){
        if(this == INVENTORY){
            return false;
        }
        if(this == ENABLE_WIRELESS){
             return (setting & 1) == 1;
        }

        return (setting >> ordinal() & 1) == 1;
    }

    public String getTranslatedName(){
        return typeName.t();
    }
}
