package sonar.fluxnetworks.client.widget;

import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.widget.RadioButton;

import javax.annotation.Nonnull;

// may be replaced by drawables in future
public class ColorButton extends RadioButton {

    private final int mRadius;
    private int mColor = ~0;

    private final Animator mMagAnim;
    private final Animator mMinAnim;

    private float mScale = 0.75f;

    private static final FloatProperty<ColorButton> sScaleProp = new FloatProperty<>() {
        @Override
        public void setValue(@Nonnull ColorButton object, float value) {
            object.mScale = value;
            object.invalidate();
        }

        @Override
        public Float get(@Nonnull ColorButton object) {
            return object.mScale;
        }
    };

    private static final TimeInterpolator sMagInterpolator = TimeInterpolator.anticipateOvershoot(4);

    public ColorButton() {
        mRadius = dp(4);
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
        canvas.scale(mScale, mScale, getWidth() / 2f, getHeight() / 2f);

        Paint paint = Paint.take();
        paint.setColor(mColor);
        float inner = mRadius * 0.5f;
        canvas.drawRect(inner, inner, getWidth() - inner, getHeight() - inner, paint);

        if (isChecked()) {
            paint.setStyle(Paint.STROKE);
            paint.setStrokeWidth(mRadius);
            paint.setColor(0xFFFFFFFF);
            canvas.drawRoundRect(inner, inner, getWidth() - inner, getHeight() - inner, inner, paint);
        }

        canvas.restore();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        // RadioButton has checked whether it's changed
        invalidate();
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
