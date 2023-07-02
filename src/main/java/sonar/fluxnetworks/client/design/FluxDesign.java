package sonar.fluxnetworks.client.design;

import icyllis.modernui.core.Context;
import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.widget.EditText;

import javax.annotation.Nonnull;

public final class FluxDesign {

    public static final int
            WHITE = 0xFFFFFFFF,
            LIGHT_GRAY = 0xFFB4B4B4,
            GRAY = 0xFF808080;

    public static final ColorStateList TEXT_COLOR = new ColorStateList(
            new int[][]{
                    StateSet.get(StateSet.VIEW_STATE_ENABLED),
                    StateSet.WILD_CARD
            }, new int[]{
            WHITE,
            GRAY
    });

    @Nonnull
    public static EditText createTextField(Context context) {
        var v = new EditText(context);
        v.setSingleLine();
        v.setTextSize(16);
        v.setTextColor(TEXT_COLOR);
        v.setBackground(new RoundRectDrawable(v));
        v.setPadding(v.dp(8), v.dp(4), v.dp(8), v.dp(4));
        return v;
    }
}
