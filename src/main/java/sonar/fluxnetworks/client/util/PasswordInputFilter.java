package sonar.fluxnetworks.client.util;

import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.Spanned;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An input filter for network password.
 *
 * @see icyllis.modernui.widget.TextView#setFilters(InputFilter[])
 */
@OnlyIn(Dist.CLIENT)
public class PasswordInputFilter implements InputFilter {

    public static final PasswordInputFilter sInstance = new PasswordInputFilter();

    private PasswordInputFilter() {
    }

    @Nullable
    @Override
    public CharSequence filter(@Nonnull CharSequence source, int start, int end,
                               @Nonnull Spanned dest, int dstart, int dend) {
        int i;
        for (i = start; i < end; i++) {
            // All chars are BMP.
            if (FluxUtils.isBadPasswordChar(source.charAt(i))) {
                break;
            }
        }

        if (i == end) {
            // It was all OK.
            return null;
        }

        if (end - start == 1) {
            // It was not OK, and there is only one char, so nothing remains.
            return "";
        }

        StringBuilder filtered = new StringBuilder();
        filtered.append(source, start, end);
        i -= start;
        end -= start;

        // Only count down to i because the chars before that were all OK.
        for (int j = end - 1; j >= i; j--) {
            if (FluxUtils.isBadPasswordChar(source.charAt(j))) {
                filtered.delete(j, j + 1);
            }
        }

        return filtered;
    }
}
