package sonar.fluxnetworks.client.design;

import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.widget.EditText;

import javax.annotation.Nonnull;

import static icyllis.modernui.view.View.dp;

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
    public static EditText createTextField() {
        var v = new EditText();
        v.setSingleLine();
        v.setTextSize(16);
        v.setTextColor(TEXT_COLOR);
        v.setBackground(new RoundRectDrawable());
        v.setPadding(dp(8), dp(4), dp(8), dp(4));
        return v;
    }
}
