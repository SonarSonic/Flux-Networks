package sonar.fluxnetworks.client.mui;

import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.text.*;
import icyllis.modernui.text.method.PasswordTransformationMethod;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;
import icyllis.modernui.widget.AdapterView.OnItemSelectedListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.design.*;
import sonar.fluxnetworks.client.widget.ColorButton;
import sonar.fluxnetworks.client.widget.ColorPicker;
import sonar.fluxnetworks.client.widget.ColorPicker.OnColorChangeListener;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.integration.MUIIntegration;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static icyllis.modernui.view.ViewGroup.LayoutParams.*;

public class CreateTab extends Fragment {

    private SecurityLevel mSecurityLevel = SecurityLevel.PRIVATE;

    private EditText mName;
    private EditText mPassword;
    private Button mCreateButton;
    private RoundRectDrawable mNameBg;
    private RoundRectDrawable mPasswordBg;
    private RoundRectDrawable mCreateButtonBg;

    private RelativeRadioGroup mColorGroup;
    private ColorPicker mColorPicker;
    private int mSelectedColor = EnumNetworkColor.BLUE.getRGB();

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateViewStates();
        }
    };

    public CreateTab() {
    }

    @Override
    public void onCreate(@Nullable DataSet savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("create_network", this, (requestKey, result) -> {
            int code = result.getInt("code");
            final FluxTranslate t = FluxTranslate.fromResponseCode(code);
            if (t != null) {
                MUIIntegration.showToastError(t);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {

        var content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutTransition(new LayoutTransition());
        final int dp2 = content.dp(2);

        {
            var v = new TextView(requireContext());
            v.setText(FluxTranslate.TAB_CREATE.get());
            v.setTextSize(16);
            v.setTextColor(FluxDesign.LIGHT_GRAY);
            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(0, content.dp(20), 0, content.dp(12));
            content.addView(v, params);
        }

        {
            mName = FluxDesign.createTextField(requireContext());
            mNameBg = (RoundRectDrawable) mName.getBackground();
            // make sure it will be filtered
            mName.setFilters(new InputFilter.LengthFilter(FluxNetwork.MAX_NETWORK_NAME_LENGTH));

            String text = "";
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                text = player.getGameProfile().getName() + "'s Network";
            }
            mName.setText(text);
            mName.setHint("Network Name");
            mName.setHintTextColor(0xFF808080);
            mName.addTextChangedListener(mTextWatcher);

            var params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.setMargins(content.dp(20), dp2, content.dp(20), content.dp(2));
            content.addView(mName, params);
        }

        {
            var group = new LinearLayout(requireContext());
            group.setOrientation(LinearLayout.HORIZONTAL);

            {
                var title = new TextView(requireContext());
                title.setText(FluxTranslate.NETWORK_SECURITY.get());
                title.setTextSize(16);
                title.setTextColor(0xFF808080);
                var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
                params.setMargins(content.dp(26), 0, content.dp(20), 0);
                group.addView(title, params);
            }

            {
                var spinner = new Spinner(requireContext());
                spinner.setPopupBackgroundDrawable(new Drawable() {
                    private final int mRadius = content.dp(8);

                    @Override
                    public void draw(@Nonnull Canvas canvas) {
                        Rect b = getBounds();
                        float inner = mRadius * 0.5f;
                        Paint paint = Paint.obtain();
                        paint.setColor(0xCF202020);
                        canvas.drawRoundRect(b.left + inner, b.top + inner, b.right - inner, b.bottom - inner,
                                mRadius, paint);
                        paint.recycle();
                    }

                    @Override
                    public boolean getPadding(@Nonnull Rect padding) {
                        int r = (int) Math.ceil(mRadius);
                        padding.set(r, r, r, r);
                        return true;
                    }
                });
                spinner.setGravity(Gravity.END);
                spinner.setPadding(content.dp(8), dp2, content.dp(8), dp2);
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
                params.setMargins(content.dp(20), 0, content.dp(20), 0);
                group.addView(spinner, params);
            }

            group.setBaselineAligned(true);

            var params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            content.addView(group, params);
        }

        {
            mPassword = FluxDesign.createTextField(requireContext());
            mPasswordBg = (RoundRectDrawable) mPassword.getBackground();
            mPassword.setHint("Password");
            mPassword.setHintTextColor(FluxDesign.GRAY);
            mPassword.setFilters(PasswordFilter.getInstance());
            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPassword.setVisibility(mSecurityLevel == SecurityLevel.ENCRYPTED ? View.VISIBLE : View.GONE);
            mPassword.addTextChangedListener(mTextWatcher);

            var params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.setMargins(content.dp(20), dp2, content.dp(20), dp2);
            content.addView(mPassword, params);
        }

        {
            mColorGroup = inflateColorGroup();

            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(content.dp(20), content.dp(8), content.dp(20), content.dp(2));

            content.addView(mColorGroup, params);

            mColorPicker = new ColorPicker(requireContext());

            mColorPicker.setOnColorChangeListener(new OnColorChangeListener() {
                @Override
                public void onColorChanged(ColorPicker picker, int color) {
                    if (mSelectedColor != color) {
                        mColorGroup.clearCheck();
                        mSelectedColor = color;
                        updateViewStates();
                    }
                }

                @Override
                public void onFocusLost(ColorPicker picker, int color) {
                    if (mSelectedColor != color) {
                        mColorGroup.clearCheck();
                        mSelectedColor = color;
                        updateViewStates();
                    }
                }
            });

            params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
            params.gravity = Gravity.CENTER;
            params.setMargins(content.dp(20), content.dp(2), content.dp(20), content.dp(4));

            content.addView(mColorPicker, params);
        }

        {
            var v = new Button(requireContext());
            v.setText("Create");
            v.setSingleLine();
            v.setTextSize(16);
            v.setTextColor(FluxDesign.TEXT_COLOR);
            v.setBackground(mCreateButtonBg = new RoundRectDrawable(v));
            v.setPadding(content.dp(8), content.dp(4), content.dp(8), content.dp(4));
            v.setOnClickListener(__ -> ClientMessages.createNetwork(
                    requireArguments().getInt("token"),
                    mName.getText().toString(),
                    mSelectedColor,
                    mSecurityLevel,
                    mPassword.getText().toString()));
            var params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(content.dp(20), content.dp(4), content.dp(20), content.dp(20));
            content.addView(v, params);
            mCreateButton = v;
        }

        // set default
        mColorGroup.check(1);

        content.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER));
        return content;
    }

    private void updateViewStates() {
        boolean password = mSecurityLevel == SecurityLevel.ENCRYPTED;
        mPassword.setVisibility(password ? View.VISIBLE : View.GONE);
        if (password && mPassword.getText().isEmpty()) {
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

    @Nonnull
    private RelativeRadioGroup inflateColorGroup() {
        var group = new RelativeRadioGroup(requireContext());

        int buttonSize = group.dp(32);
        int margin = group.dp(1);
        for (int i = 0; i < EnumNetworkColor.VALUES.length; i++) {
            EnumNetworkColor color = EnumNetworkColor.VALUES[i];
            var v = new ColorButton(requireContext());
            v.setColor(color.getRGB() | 0xC0000000);
            v.setId(i + 1);
            var params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
            params.setMargins(margin, margin, margin, margin);

            if (i == 0) {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
            } else if (i < 7) {
                params.addRule(RelativeLayout.END_OF, i);
            } else if (i == 7) {
                params.addRule(RelativeLayout.BELOW, 1);
            } else {
                params.addRule(RelativeLayout.END_OF, i);
                params.addRule(RelativeLayout.BELOW, 1);
            }

            v.setLayoutParams(params);

            if (i == 0 || i == 7) {
                group.addView(v);
            } else {
                group.postDelayed(() -> group.addView(v), i * 50L);
            }
        }

        group.setLayoutTransition(new LayoutTransition());

        group.setOnCheckedChangeListener((__, id) -> {
            if (id > 0) {
                mSelectedColor = EnumNetworkColor.VALUES[id - 1].getRGB();
                mColorPicker.setColor(mSelectedColor);
                updateViewStates();
            }
        });

        return group;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentFragmentManager().clearFragmentResultListener("create_network");
    }
}
