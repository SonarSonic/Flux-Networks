package sonar.fluxnetworks.client.widget;

import icyllis.modernui.text.Editable;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.TextWatcher;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.EditText;
import icyllis.modernui.widget.LinearLayout;
import sonar.fluxnetworks.client.design.FluxDesign;
import sonar.fluxnetworks.client.design.RoundRectDrawable;

import javax.annotation.Nullable;

// RGB color picker
public class ColorPicker extends LinearLayout {

    private static final InputFilter.LengthFilter sLengthFilter = new InputFilter.LengthFilter(3);

    private final EditText mRedField;
    private final EditText mGreenField;
    private final EditText mBlueField;

    private OnColorChangeListener mOnColorChangeListener;

    private boolean mPreventUpdateColor;

    // last valid color
    private int mColor;

    public ColorPicker() {
        mRedField = FluxDesign.createTextField();
        mRedField.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mRedField.setHint("R");
        mRedField.setHintTextColor(FluxDesign.GRAY);
        if (mRedField.getBackground() instanceof RoundRectDrawable bg) {
            bg.setColor(0xbf4040);
        }
        mRedField.setFilters(DigitsInputFilter.getInstance(mRedField.getTextLocale()), sLengthFilter);
        mRedField.setMinWidth(dp(72));
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateColor();
            }
        };
        mRedField.addTextChangedListener(textWatcher);
        final OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int r = mRedField.getText().isEmpty() ? 0 : Integer.parseInt(mRedField.getText().toString());
                    int g = mGreenField.getText().isEmpty() ? 0 : Integer.parseInt(mGreenField.getText().toString());
                    int b = mBlueField.getText().isEmpty() ? 0 : Integer.parseInt(mBlueField.getText().toString());
                    r = Math.min(r, 255);
                    g = Math.min(g, 255);
                    b = Math.min(b, 255);
                    int color = r << 16 | g << 8 | b;
                    if (color != mColor) {
                        mColor = color;
                    }
                    updateRgbText();
                    if (mOnColorChangeListener != null) {
                        mOnColorChangeListener.onFocusLost(ColorPicker.this, mColor);
                    }
                }
            }
        };
        mRedField.setOnFocusChangeListener(onFocusChangeListener);

        var params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));

        addView(mRedField, params);

        mGreenField = FluxDesign.createTextField();
        mGreenField.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mGreenField.setHint("G");
        mGreenField.setHintTextColor(FluxDesign.GRAY);
        if (mGreenField.getBackground() instanceof RoundRectDrawable bg) {
            bg.setColor(0x40bf40);
        }
        mGreenField.setFilters(DigitsInputFilter.getInstance(mGreenField.getTextLocale()), sLengthFilter);
        mGreenField.setMinWidth(dp(72));
        mGreenField.addTextChangedListener(textWatcher);
        mGreenField.setOnFocusChangeListener(onFocusChangeListener);

        addView(mGreenField, new LayoutParams(params));

        mBlueField = FluxDesign.createTextField();
        mBlueField.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mBlueField.setHint("B");
        mBlueField.setHintTextColor(FluxDesign.GRAY);
        if (mBlueField.getBackground() instanceof RoundRectDrawable bg) {
            bg.setColor(0x4040bf);
        }
        mBlueField.setFilters(DigitsInputFilter.getInstance(mBlueField.getTextLocale()), sLengthFilter);
        mBlueField.setMinWidth(dp(72));
        mBlueField.addTextChangedListener(textWatcher);
        mBlueField.setOnFocusChangeListener(onFocusChangeListener);

        addView(mBlueField, new LayoutParams(params));
    }

    public void setColor(int color) {
        if (color != mColor) {
            mColor = color;
            updateRgbText();
            invokeListener();
        }
    }

    public void setOnColorChangeListener(@Nullable OnColorChangeListener onColorChangeListener) {
        mOnColorChangeListener = onColorChangeListener;
    }

    private void updateColor() {
        if (mPreventUpdateColor) {
            return;
        }
        if (mRedField.getText().isEmpty() || mGreenField.getText().isEmpty() || mBlueField.getText().isEmpty()) {
            return;
        }
        int r = Integer.parseInt(mRedField.getText().toString());
        int g = Integer.parseInt(mGreenField.getText().toString());
        int b = Integer.parseInt(mBlueField.getText().toString());
        r = Math.min(r, 255);
        g = Math.min(g, 255);
        b = Math.min(b, 255);
        int color = r << 16 | g << 8 | b;
        if (color != mColor) {
            mColor = color;
            updateRgbText();
            invokeListener();
        }
    }

    private void updateRgbText() {
        int r = mColor >> 16 & 0xFF;
        int g = mColor >> 8 & 0xFF;
        int b = mColor & 0xFF;
        mPreventUpdateColor = true;
        mRedField.setTextKeepState(Integer.toString(r));
        mGreenField.setTextKeepState(Integer.toString(g));
        mBlueField.setTextKeepState(Integer.toString(b));
        mPreventUpdateColor = false;
    }

    private void invokeListener() {
        if (mOnColorChangeListener != null) {
            mOnColorChangeListener.onColorChanged(this, mColor);
        }
    }

    public interface OnColorChangeListener {

        void onColorChanged(ColorPicker picker, int color);

        void onFocusLost(ColorPicker picker, int color);
    }
}
