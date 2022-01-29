package sonar.fluxnetworks.client.mui;

import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.forge.CanvasForge;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.math.Rect;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.method.ArrowKeyMovementMethod;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.util.IntProperty;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.LinearLayout;
import icyllis.modernui.widget.TextView;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.device.TransferHandler;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static icyllis.modernui.view.ViewConfiguration.dp;

/**
 * The home page for Flux Point and Flux Plug.
 */
public class FluxConnectorHome extends Fragment {

    private final TileFluxDevice mDevice;

    private TextView mCustomName;
    private TextView mPriority;
    private TextView mLimit;

    public FluxConnectorHome(@Nonnull TileFluxDevice device) {
        mDevice = device;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        final Language lang = Language.getInstance();

        var content = new LinearLayout();
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutTransition(new LayoutTransition());

        for (int i = 0; i < 3; i++) {
            var v = new TextView();
            switch (i) {
                case 0 -> {
                    v.setText(mDevice.getCustomName(), TextView.BufferType.EDITABLE);
                    v.setHint(lang.getOrDefault(mDevice.getBlockState().getBlock().getDescriptionId()));
                    v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(TileFluxDevice.MAX_CUSTOM_NAME_LENGTH)});
                    v.setOnFocusChangeListener((__, hasFocus) -> {
                        if (!hasFocus) {
                            // Do not check if it's changed on the client side,
                            // in case of a packet is being sent to the client
                            CompoundTag tag = new CompoundTag();
                            tag.putString(FluxConstants.CUSTOM_NAME, mCustomName.getText().toString());
                            ClientMessages.sendEditDevice(mDevice, tag);
                        }
                    });
                    mCustomName = v;
                }
                case 1 -> {
                    v.setText(Integer.toString(mDevice.getRawPriority()), TextView.BufferType.EDITABLE);
                    v.setHint("Priority");
                    v.setHintTextColor(0xFF808080);
                    v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5),
                            DigitsInputFilter.getInstance(null, true, false)});
                    v.setOnFocusChangeListener((__, hasFocus) -> {
                        if (!hasFocus) {
                            int priority = Mth.clamp(Integer.parseInt(mPriority.getText().toString()),
                                    TransferHandler.PRI_USER_MIN, TransferHandler.PRI_USER_MAX);
                            mPriority.setTextKeepState(Integer.toString(priority));

                            CompoundTag tag = new CompoundTag();
                            tag.putInt(FluxConstants.PRIORITY, priority);
                            ClientMessages.sendEditDevice(mDevice, tag);
                        }
                    });
                    mPriority = v;
                }
                default -> {
                    v.setText(Long.toString(mDevice.getRawLimit()), TextView.BufferType.EDITABLE);
                    v.setHint("Transfer Limit");
                    v.setHintTextColor(0xFF808080);
                    v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10),
                            DigitsInputFilter.getInstance(null, false, false)});
                    v.setOnFocusChangeListener((__, hasFocus) -> {
                        if (!hasFocus) {
                            long limit = Long.parseLong(mLimit.getText().toString());
                            mLimit.setTextKeepState(Long.toString(limit));

                            CompoundTag tag = new CompoundTag();
                            tag.putLong(FluxConstants.LIMIT, limit);
                            ClientMessages.sendEditDevice(mDevice, tag);
                        }
                    });
                    mLimit = v;
                }
            }
            v.setFocusableInTouchMode(true);
            v.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            v.setSingleLine();
            v.setBackground(new FluxDeviceUI.TextFieldBackground());
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setTextSize(16);
            v.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    new TextFieldStart(FluxDeviceUI.sButtonIcon, (((i + 1) % 3) + 1) * 64), null, null, null);
            v.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(dp(20), dp(i == 0 ? 50 : 2), dp(20), dp(2));

            content.postDelayed(() -> content.addView(v, params), (i + 1) * 100);
        }

        {
            var v = new ConnectorView(FluxDeviceUI.sButtonIcon);
            var params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(dp(8), dp(2), dp(8), dp(8));
            content.postDelayed(() -> content.addView(v, params), 400);
        }

        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return content;
    }

    static class ConnectorView extends View {

        private final Image mImage;
        private final int mSize;
        private float mRodLength;
        private final Paint mBoxPaint = new Paint();

        private final ObjectAnimator mRodAnimator;
        private final ObjectAnimator mBoxAnimator;

        private final ItemStack mItem = Items.DIAMOND_BLOCK.getDefaultInstance();

        public ConnectorView(Image image) {
            mImage = image;
            mSize = dp(32);
            mRodAnimator = ObjectAnimator.ofFloat(this, new FloatProperty<>() {
                @Override
                public void setValue(@Nonnull ConnectorView object, float value) {
                    object.mRodLength = value;
                    invalidate();
                }

                @Override
                public Float get(@Nonnull ConnectorView object) {
                    return object.mRodLength;
                }
            }, 0, mSize);
            mRodAnimator.setInterpolator(TimeInterpolator.DECELERATE);
            mRodAnimator.setDuration(400);
            mRodAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(@Nonnull Animator animation, boolean isReverse) {
                    mBoxAnimator.start();
                }
            });
            mBoxAnimator = ObjectAnimator.ofInt(mBoxPaint, new IntProperty<>() {
                @Override
                public void setValue(@Nonnull Paint object, int value) {
                    object.setAlpha(value);
                    invalidate();
                }

                @Override
                public Integer get(@Nonnull Paint object) {
                    return object.getColor() >>> 24;
                }
            }, 0, 128);
            mRodAnimator.setInterpolator(TimeInterpolator.LINEAR);
            mBoxAnimator.setDuration(400);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            mRodAnimator.start();
            mBoxPaint.setRGBA(64, 64, 64, 0);
        }

        @Override
        protected void onDraw(@Nonnull Canvas canvas) {
            Paint paint = Paint.take();
            paint.setColor(FluxDeviceUI.NETWORK_COLOR);
            paint.setAlpha(192);
            paint.setStrokeWidth(mSize / 8f);

            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;

            int boxAlpha = mBoxPaint.getColor() >>> 24;

            float px1l = centerX - (15 / 64f) * mSize;
            float py1 = centerY + (8 / 64f) * mSize;
            canvas.save();
            canvas.rotate(22.5f, px1l, py1);
            canvas.drawRoundLine(px1l, py1, px1l - mRodLength * 2, py1, paint);
            canvas.restore();

            if (boxAlpha > 0) {
                canvas.drawRect(px1l - mSize * 2.9f, py1 - mSize * 1.1f,
                        px1l - mSize * 1.9f, py1 - mSize * 0.1f, mBoxPaint);
            }

            float px1r = centerX + (15 / 64f) * mSize;
            canvas.save();
            canvas.rotate(-22.5f, px1r, py1);
            canvas.drawRoundLine(px1r, py1, px1r + mRodLength * 2, py1, paint);
            canvas.restore();

            if (boxAlpha > 0) {
                canvas.drawRect(px1r + mSize * 1.9f, py1 - mSize * 1.1f,
                        px1r + mSize * 2.9f, py1 - mSize * 0.1f, mBoxPaint);
            }

            float py2 = centerY + (19 / 64f) * mSize;
            canvas.drawRoundLine(centerX, py2, centerX, py2 + mRodLength, paint);

            if (boxAlpha > 0) {
                canvas.drawRect(centerX - mSize * .5f, py2 + mSize * 1.1f,
                        centerX + mSize * .5f, py2 + mSize * 2.1f, mBoxPaint);
            }

            float offset = mSize / 2f;
            canvas.drawImage(mImage, 0, 192, 64, 256,
                    centerX - offset, centerY - offset,
                    centerX + offset, centerY + offset, null);

            canvas.save();
            canvas.rotate(-22.5f, px1l, py1);
            canvas.drawRoundLine(px1l, py1, px1l - mRodLength * 2, py1, paint);
            canvas.restore();

            if (boxAlpha > 0) {
                canvas.drawRect(px1l - mSize * 2.9f, py1 + mSize * 0.1f,
                        px1l - mSize * 1.9f, py1 + mSize * 1.1f, mBoxPaint);
            }

            canvas.save();
            canvas.rotate(22.5f, px1r, py1);
            canvas.drawRoundLine(px1r, py1, px1r + mRodLength * 2, py1, paint);
            canvas.restore();

            if (boxAlpha > 0) {
                canvas.drawRect(px1r + mSize * 1.9f, py1 + mSize * 0.1f,
                        px1r + mSize * 2.9f, py1 + mSize * 1.1f, mBoxPaint);
            }

            py2 = centerY - (19 / 64f) * mSize;
            canvas.drawRoundLine(centerX, py2, centerX, py2 - mRodLength, paint);

            if (boxAlpha > 0) {
                canvas.drawRect(centerX - mSize * .5f, py2 - mSize * 2.1f,
                        centerX + mSize * .5f, py2 - mSize * 1.1f, mBoxPaint);
                paint.reset();
                paint.setAlpha(Math.min(255, boxAlpha << 1));
                CanvasForge.get(canvas).drawItemStack(mItem, centerX, py2 - mSize * 1.6f, mSize, paint);
            }
        }
    }

    static class TextFieldStart extends Drawable {

        private final Image mImage;
        private final int mSrcLeft;
        private final int mSize;

        public TextFieldStart(Image image, int srcLeft) {
            mImage = image;
            mSrcLeft = srcLeft;
            mSize = dp(24);
        }

        @Override
        public void draw(@Nonnull Canvas canvas) {
            Rect b = getBounds();
            canvas.drawImage(mImage, mSrcLeft, 192, mSrcLeft + 64, 256, b.left, b.top, b.right, b.bottom, null);
        }

        @Override
        public int getIntrinsicWidth() {
            return mSize;
        }

        @Override
        public int getIntrinsicHeight() {
            return mSize;
        }

        @Override
        public boolean getPadding(@Nonnull Rect padding) {
            int h = Math.round(mSize / 4f);
            int v = Math.round(mSize / 6f);
            padding.set(h, v, h, v);
            return true;
        }
    }
}
