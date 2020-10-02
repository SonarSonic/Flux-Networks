package sonar.fluxnetworks.api.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class StyleUtils {

    @Nonnull
    public static ITextComponent getErrorStyle(@Nonnull FluxTranslate key) {
        return key.getTextComponent().mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED);
    }
}
