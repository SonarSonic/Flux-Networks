package sonar.fluxnetworks.api.translate;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class StyleUtils {

    public static ITextComponent getErrorStyle(FluxTranslate key){
        return key.getTextComponent().setStyle(new Style().setBold(true).setColor(TextFormatting.DARK_RED));
    }

}
