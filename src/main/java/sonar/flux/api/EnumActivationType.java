package sonar.flux.api;

import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;

public enum EnumActivationType {

    ACTIVATED(FluxTranslate.ACTIVATION_ALWAYS),
    DISACTIVATED(FluxTranslate.ACTIVATION_NEVER),
    POSITIVE_SIGNAL(FluxTranslate.ACTIVATION_POSITIVE_SIGNAL),
    NEGATIVE_SIGNAL(FluxTranslate.ACTIVATION_NEGATIVE_SIGNAL);

    public Localisation comment;

    EnumActivationType(Localisation comment){
        this.comment = comment;
    }

    public int getTextureY(){
        switch(this){
            case ACTIVATED:
                return 24/2;
            case DISACTIVATED:
                return 48/2;
            case POSITIVE_SIGNAL:
                return 72/2;
            case NEGATIVE_SIGNAL:
                return 96/2;
        }
        return 0;
    }

}
