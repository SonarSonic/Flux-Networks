package sonar.flux.api;

import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;

public enum EnumPriorityType {
    NORMAL(FluxTranslate.PRIORITY_NORMAL),
    SURGE(FluxTranslate.PRIORITY_SURGE);

    public Localisation comment;

    EnumPriorityType(Localisation comment){
        this.comment = comment;
    }

}
