package sonar.fluxnetworks.client.mui;

import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.method.ArrowKeyMovementMethod;
import icyllis.modernui.text.method.PasswordTransformationMethod;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewConfiguration;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TabCreate {

    public View mContent;

    private final CharSequence[] mSecurityLevelTexts = Arrays.stream(SecurityLevel.VALUES)
            .map(securityLevel -> {
                String prefix = FluxTranslate.translate(FluxTranslate.NETWORK_SECURITY) + ": ";
                String name = securityLevel.getName();
                SpannableString spannableString = new SpannableString(prefix + name);
                spannableString.setSpan(new ForegroundColorSpan(0xFF0FB7EC), prefix.length(),
                        spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            }).toArray(CharSequence[]::new);

    private SecurityLevel mSecurityLevel = SecurityLevel.PUBLIC;

    private TextView mName;
    private TextView mSecurity;
    private TextView mPassword;

    public TabCreate() {
    }

    public View inflate() {
        final ViewConfiguration c = ViewConfiguration.get();
        final Language lang = Language.getInstance();

        var content = new LinearLayout();
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutTransition(new LayoutTransition());

        {
            var v = new TextView();
            v.setText(lang.getOrDefault(FluxTranslate.TAB_CREATE));
            v.setTextSize(16);
            v.setTextColor(0xFFB4B4B4);
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(0, c.view(20), 0, c.view(20));
            content.addView(v, params);
        }

        {
            var v = new TextView();
            String text = "";
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                text = player.getGameProfile().getName() + "'s Network";
            }
            v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(FluxNetwork.MAX_NETWORK_NAME_LENGTH)});
            v.setText(text, TextView.BufferType.EDITABLE);
            v.setHint("Network Name");
            v.setHintTextColor(0xFF808080);
            v.setFocusableInTouchMode(true);
            v.setSingleLine();
            v.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setTextSize(16);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(new FluxDeviceUI.TextFieldBackground());
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(c.view(20), c.view(4), c.view(20), c.view(4));
            content.addView(v, params);
            mName = v;
        }

        {
            var v = new TextView();
            v.setText(mSecurityLevelTexts[mSecurityLevel.getId()]);
            v.setTextSize(16);
            v.setTextColor(0xFF808080);
            v.setOnClickListener(__ -> {
                mSecurityLevel = FluxUtils.incrementEnum(mSecurityLevel, SecurityLevel.VALUES);
                mSecurity.setText(mSecurityLevelTexts[mSecurityLevel.getId()]);
                mPassword.setVisibility(mSecurityLevel == SecurityLevel.ENCRYPTED ? View.VISIBLE : View.INVISIBLE);
            });
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(c.view(20), c.view(4), c.view(20), c.view(4));
            content.addView(v, params);
            mSecurity = v;
        }

        {
            var v = new TextView();
            v.setText("", TextView.BufferType.EDITABLE);
            v.setHint("Password");
            v.setHintTextColor(0xFF808080);
            v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(FluxNetwork.MAX_PASSWORD_LENGTH),
                    PasswordFilter.INSTANCE});
            v.setFocusableInTouchMode(true);
            v.setSingleLine();
            v.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setTextSize(16);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(new FluxDeviceUI.TextFieldBackground());
            v.setVisibility(View.INVISIBLE);
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(c.view(20), c.view(4), c.view(20), c.view(4));
            content.addView(v, params);
            mPassword = v;
        }

        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        return mContent = content;
    }

    public static class PasswordFilter implements InputFilter {

        private static final PasswordFilter INSTANCE = new PasswordFilter();

        @Nullable
        @Override
        public CharSequence filter(@Nonnull CharSequence source, int start, int end,
                                   @Nonnull Spanned dest, int dstart, int dend) {
            int i;
            for (i = start; i < end; i++) {
                if (FluxUtils.notPasswordChar(source.charAt(i))) {
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
                if (FluxUtils.notPasswordChar(source.charAt(j))) {
                    filtered.delete(j, j + 1);
                }
            }

            return filtered;
        }
    }
}
