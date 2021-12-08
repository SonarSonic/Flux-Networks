package sonar.fluxnetworks.client.widget;

import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.ViewConfiguration;
import icyllis.modernui.widget.CompoundButton;

import javax.annotation.Nonnull;

// may be replaced by drawables in future
public class ColorButton extends CompoundButton {

    private final int mRadius;
    private int mColor = ~0;

    private float mScale = 0.75f;

    private static final FloatProperty<ColorButton> sScaleProp = new FloatProperty<>() {
        @Override
        public void setValue(@Nonnull ColorButton object, float value) {
            if (object.mScale != value) {
                object.mScale = value;
                object.invalidate();
            }
        }

        @Override
        public Float get(@Nonnull ColorButton object) {
            return object.mScale;
        }
    };

    private final Animator mMagAnim;
    private final Animator mMinAnim;

    private static final TimeInterpolator sMagInterpolator = TimeInterpolator.anticipateOvershoot(4);

    public ColorButton() {
        mRadius = ViewConfiguration.dp(4);
        mMagAnim = ObjectAnimator.ofFloat(this, sScaleProp, mScale, 0.93f);
        mMagAnim.setInterpolator(sMagInterpolator);

        mMinAnim = ObjectAnimator.ofFloat(this, sScaleProp, 0.93f, mScale);
        mMinAnim.setInterpolator(TimeInterpolator.DECELERATE);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    protected void onDraw(@Nonnull Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScale, mScale, getWidth() * 0.5f, getHeight() * 0.5f);

        float offset = mRadius * 0.5f;
        Paint paint = Paint.take();
        paint.setColor(mColor);
        canvas.drawRect(offset, offset, getWidth() - offset, getHeight() - offset, paint);

        if (isChecked()) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mRadius);
            paint.setColor(0xFFFFFFFF);
            canvas.drawRoundRect(offset, offset, getWidth() - offset, getHeight() - offset, offset, paint);
        }

        canvas.restore();
    }

    @Override
    public void setChecked(boolean checked) {
        boolean oldChecked = isChecked();
        super.setChecked(checked);
        if (oldChecked != checked) {
            invalidate();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    @Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        }
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        super.onHoverChanged(hovered);
        if (hovered) {
            mMinAnim.cancel();
            mMagAnim.setupStartValues();
            mMagAnim.start();
        } else {
            mMagAnim.cancel();
            mMinAnim.setupStartValues();
            mMinAnim.start();
        }
    }
}
