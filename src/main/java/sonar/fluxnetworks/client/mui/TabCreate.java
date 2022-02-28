package sonar.fluxnetworks.client.mui;

import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.math.Rect;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.method.PasswordTransformationMethod;
import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import icyllis.modernui.widget.AdapterView.OnItemSelectedListener;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.mui.FluxDeviceUI.TabBackground;
import sonar.fluxnetworks.client.util.PasswordInputFilter;
import sonar.fluxnetworks.client.util.SecurityLevelAdapter;
import sonar.fluxnetworks.client.widget.ColorButton;
import sonar.fluxnetworks.client.widget.RoundTextBackground;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static icyllis.modernui.view.View.dp;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TabCreate extends Fragment {

    private SecurityLevel mSecurityLevel = SecurityLevel.PRIVATE;

    private EditText mName;
    private EditText mPassword;
    private Button mCreateButton;
    private RoundTextBackground mNameBg;
    private RoundTextBackground mPasswordBg;
    private RoundTextBackground mCreateButtonBg;

    private RelativeRadioGroup mColorGroup;
    private int mSelectedColor = EnumNetworkColor.BLUE.getRGB();

    public TabCreate() {
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        final Language lang = Language.getInstance();
        final int dp2 = dp(2);

        var content = new LinearLayout();
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutTransition(new LayoutTransition());

        {
            var v = new TextView();
            v.setText(lang.getOrDefault(FluxTranslate.TAB_CREATE));
            v.setTextSize(16);
            v.setTextColor(0xFFB4B4B4);
            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(0, dp(20), 0, dp(20));
            content.addView(v, params);
        }

        {
            var v = new EditText();
            String text = "";
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                text = player.getGameProfile().getName() + "'s Network";
            }
            v.setFilters(new InputFilter.LengthFilter(FluxNetwork.MAX_NETWORK_NAME_LENGTH));
            v.setText(text);
            v.setHint("Network Name");
            v.setHintTextColor(0xFF808080);
            v.setSingleLine();
            v.setTextSize(16);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(mNameBg = new RoundTextBackground());
            v.setPadding(dp(8), dp(4), dp(8), dp(4));
            v.setOnFocusChangeListener((__, focused) -> {
                if (!focused) {
                    updateViewStates();
                }
            });
            var params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.setMargins(dp(20), dp2, dp(20), dp(2));
            content.addView(v, params);
            mName = v;
        }

        {
            var group = new LinearLayout();
            group.setOrientation(LinearLayout.HORIZONTAL);

            {
                var title = new TextView();
                title.setText(FluxTranslate.translate(FluxTranslate.NETWORK_SECURITY));
                title.setTextSize(16);
                title.setTextColor(0xFF808080);
                var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
                params.setMargins(dp(20), dp2, dp(20), dp2);
                group.addView(title, params);
            }

            {
                var spinner = new Spinner();
                spinner.setPopupBackgroundDrawable(new Drawable() {
                    private final int mRadius = dp(8);

                    @Override
                    public void draw(@Nonnull Canvas canvas) {
                        Rect b = getBounds();
                        float inner = mRadius * 0.5f;
                        Paint paint = Paint.take();
                        paint.setColor(0xCF202020);
                        canvas.drawRoundRect(b.left + inner, b.top + inner, b.right - inner, b.bottom - inner,
                                mRadius, paint);
                    }

                    @Override
                    public boolean getPadding(@Nonnull Rect padding) {
                        int r = (int) Math.ceil(mRadius);
                        padding.set(r, r, r, r);
                        return true;
                    }
                });
                spinner.setGravity(Gravity.END);
                spinner.setPadding(dp(8), dp2, dp(8), dp2);
                spinner.setAdapter(new SecurityLevelAdapter());
                spinner.setSelection(mSecurityLevel.getId());
                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSecurityLevel = (SecurityLevel) parent.getSelectedItem();
                        updateViewStates();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                params.setMargins(dp(20), dp2, dp(20), dp2);
                group.addView(spinner, params);
            }

            group.setBaselineAligned(true);

            var params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            content.addView(group, params);
        }

        {
            var v = new EditText();
            v.setHint("Password");
            v.setHintTextColor(0xFF808080);
            v.setFilters(PasswordInputFilter.getInstance());
            v.setSingleLine();
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setTextSize(16);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(mPasswordBg = new RoundTextBackground());
            v.setPadding(dp(8), dp(4), dp(8), dp(4));
            v.setVisibility(mSecurityLevel == SecurityLevel.ENCRYPTED ? View.VISIBLE : View.GONE);
            v.setOnFocusChangeListener((__, focused) -> {
                if (!focused) {
                    updateViewStates();
                }
            });
            var params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.setMargins(dp(20), dp2, dp(20), dp2);
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

            group.setLayoutTransition(new LayoutTransition());

            group.setOnCheckedChangeListener((__, id) -> {
                mSelectedColor = EnumNetworkColor.VALUES[id - 3].getRGB();
                updateViewStates();
            });

            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
            params.gravity = Gravity.CENTER;
            params.setMargins(dp(20), dp(12), dp(20), dp(20));

            content.addView(group, params);
            mColorGroup = group;
        }

        {
            var v = new Button();
            v.setText("Create");
            v.setSingleLine();
            v.setTextSize(16);
            v.setTextColor(new ColorStateList(new int[][]{
                    StateSet.get(StateSet.VIEW_STATE_ENABLED),
                    StateSet.WILD_CARD
            }, new int[]{
                    0xFFFFFFFF,
                    0xFF808080
            }));
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            v.setBackground(mCreateButtonBg = new RoundTextBackground());
            v.setPadding(dp(8), dp(4), dp(8), dp(4));
            v.setOnClickListener(__ -> {
                ClientMessages.sendCreateNetwork(mName.getText().toString(),
                        mSelectedColor,
                        mSecurityLevel,
                        mPassword.getText().toString(), code -> {
                            if (code == FluxConstants.RES_REJECT) {
                                requireView().post(() -> {
                                    Toast.makeText("Your request was rejected by the server", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
            });
            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(dp(20), dp(4), dp(20), dp(20));
            content.addView(v, params);
            mCreateButton = v;
        }

        content.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER));
        return content;
    }

    private void updateViewStates() {
        boolean needPassword = mSecurityLevel == SecurityLevel.ENCRYPTED;
        mPassword.setVisibility(needPassword ? View.VISIBLE : View.GONE);
        if (needPassword && mPassword.getText().isEmpty()) {
            mCreateButton.setEnabled(false);
        } else {
            mCreateButton.setEnabled(!mName.getText().isEmpty());
        }
        mNameBg.setColor(mSelectedColor);
        mPasswordBg.setColor(mSelectedColor);
        mCreateButtonBg.setColor(mSelectedColor);
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            View view = fragment.requireView().findViewById(FluxDeviceUI.id_tab_container);
            if (view.getBackground() instanceof TabBackground bg) {
                bg.setColor(mSelectedColor);
            }
        }
    }
}
