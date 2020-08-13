package sonar.fluxnetworks.api.translate;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class StyleUtils {

    public static ITextComponent getErrorStyle(FluxTranslate key) {
        return key.getTextComponent().mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED);
    }

}
