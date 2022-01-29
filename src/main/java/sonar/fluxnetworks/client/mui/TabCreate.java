package sonar.fluxnetworks.client.mui;

import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.method.ArrowKeyMovementMethod;
import icyllis.modernui.text.method.PasswordTransformationMethod;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.util.PasswordInputFilter;
import sonar.fluxnetworks.client.widget.ColorButton;
import sonar.fluxnetworks.client.widget.RelativeRadioGroup;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nullable;
import java.util.Arrays;

import static icyllis.modernui.view.ViewConfiguration.dp;

public class TabCreate extends Fragment {

    private CharSequence[] mSecurityLevelTexts;

    private SecurityLevel mSecurityLevel = SecurityLevel.PUBLIC;

    private TextView mName;
    private TextView mSecurity;
    private TextView mPassword;

    private RelativeRadioGroup mColorGroup;

    private TextView mCreate;

    public TabCreate() {
    }

    @Override
    public void onCreate(@Nullable DataSet savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSecurityLevelTexts = Arrays.stream(SecurityLevel.VALUES)
                .map(level -> {
                    String prefix = FluxTranslate.translate(FluxTranslate.NETWORK_SECURITY) + ": ";
                    SpannableString string = new SpannableString(prefix + level.getName());
                    string.setSpan(new ForegroundColorSpan(0xFF0FB7EC), prefix.length(),
                            string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return string;
                }).toArray(CharSequence[]::new);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
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
            params.setMargins(0, dp(20), 0, dp(20));
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
            v.setOnFocusChangeListener((__, focused) -> {
                if (!focused) {
                    checkCreateState();
                }
            });
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(dp(20), dp(4), dp(20), dp(4));
            content.addView(v, params);
            mName = v;
        }

        {
            var v = new TextView();
            v.setText(mSecurityLevelTexts[mSecurityLevel.ordinal()]);
            v.setTextSize(16);
            v.setTextColor(0xFF808080);
            v.setOnClickListener(__ -> {
                mSecurityLevel = FluxUtils.cycle(mSecurityLevel, SecurityLevel.VALUES);
                mSecurity.setText(mSecurityLevelTexts[mSecurityLevel.ordinal()]);
                mPassword.setVisibility(mSecurityLevel == SecurityLevel.ENCRYPTED ? View.VISIBLE : View.GONE);
                checkCreateState();
            });
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(dp(20), dp(4), dp(20), dp(4));
            content.addView(v, params);
            mSecurity = v;
        }

        {
            var v = new TextView();
            v.setText("", TextView.BufferType.EDITABLE);
            v.setHint("Password");
            v.setHintTextColor(0xFF808080);
            v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(FluxNetwork.MAX_PASSWORD_LENGTH),
                    PasswordInputFilter.sInstance});
            v.setFocusableInTouchMode(true);
            v.setSingleLine();
            v.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setTextSize(16);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(new FluxDeviceUI.TextFieldBackground());
            v.setVisibility(mSecurityLevel == SecurityLevel.ENCRYPTED ? View.VISIBLE : View.GONE);
            v.setOnFocusChangeListener((__, focused) -> {
                if (!focused) {
                    checkCreateState();
                }
            });
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(dp(20), dp(4), dp(20), dp(4));
            content.addView(v, params);
            mPassword = v;
        }

        {
            var group = new RelativeRadioGroup();

            int buttonSize = dp(32);
            int margin = dp(2);
            for (int i = 0; i < EnumNetworkColor.VALUES.length; i++) {
                EnumNetworkColor color = EnumNetworkColor.VALUES[i];
                var v = new ColorButton();
                v.setColor(color.getRGB() | 0xC0000000);
                v.setId(i + 3);
                v.setClickable(true);
                var params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
                params.setMargins(margin, margin, margin, margin);

                if (i == 0) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                } else if (i < 7) {
                    params.addRule(RelativeLayout.END_OF, i + 2);
                } else if (i == 7) {
                    params.addRule(RelativeLayout.BELOW, 3);
                } else {
                    params.addRule(RelativeLayout.END_OF, i + 2);
                    params.addRule(RelativeLayout.BELOW, 3);
                }

                v.setLayoutParams(params);

                if (i == 0 || i == 7) {
                    group.addView(v);
                } else {
                    content.postDelayed(() -> group.addView(v), i * 50L);
                }
            }

            group.check(3);

            //group.setVerticalGravity(Gravity.CENTER);
            group.setLayoutTransition(new LayoutTransition());

            group.setOnCheckedChangeListener((__, id) -> {
                //FluxNetworks.LOGGER.info("Checked {}", EnumNetworkColor.VALUES[id - 3]);
            });

            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            params.gravity = Gravity.CENTER;

            content.addView(group, params);
            mColorGroup = group;
        }

        {
            var v = new TextView();
            v.setText("Create");
            v.setSingleLine();
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setTextSize(16);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(new FluxDeviceUI.TextFieldBackground());
            v.setOnClickListener(__ -> {
                ClientMessages.sendCreateNetwork(mName.getText().toString(),
                        EnumNetworkColor.VALUES[mColorGroup.getCheckedId() - 3].getRGB(),
                        mSecurityLevel, mPassword.getText().toString(), code -> {
                            if (code == FluxConstants.RES_REJECT) {
                                requireView().post(() -> {
                                    Toast.makeText("Your request was rejected by the server", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
            });
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(dp(20), dp(4), dp(20), dp(20));
            content.addView(v, params);
            mCreate = v;
        }

        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        return content;
    }

    private void checkCreateState() {
        if (mSecurityLevel == SecurityLevel.ENCRYPTED && mPassword.getText().isEmpty()) {
            mCreate.setEnabled(false);
            return;
        }
        mCreate.setEnabled(mName.getText().length() > 0);
    }
}
