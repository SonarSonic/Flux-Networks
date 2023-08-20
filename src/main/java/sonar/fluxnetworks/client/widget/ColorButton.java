package sonar.fluxnetworks.client.widget;

import icyllis.modernui.animation.*;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.widget.RadioButton;

import javax.annotation.Nonnull;

// may be replaced by drawables in future
public class ColorButton extends RadioButton {

    private static final float START_SCALE = 0.8f;
    private static final TimeInterpolator sMagInterpolator = new AnticipateOvershootInterpolator(4);

    private final int mRadius;
    private int mColor = ~0;

    private final Animator mMagAnim;
    private final Animator mMinAnim;

    public ColorButton(Context context) {
        super(context);
        mRadius = dp(4);
        mMagAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat(SCALE_X, START_SCALE, 1.0f),
                PropertyValuesHolder.ofFloat(SCALE_Y, START_SCALE, 1.0f));
        mMagAnim.setInterpolator(sMagInterpolator);

        mMinAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat(SCALE_X, 1.0f, START_SCALE),
                PropertyValuesHolder.ofFloat(SCALE_Y, 1.0f, START_SCALE));
        mMinAnim.setInterpolator(TimeInterpolator.DECELERATE);

        setScaleX(START_SCALE);
        setScaleY(START_SCALE);
    }

    // ARGB
    public int getColor() {
        return mColor;
    }

    // ARGB
    public void setColor(int color) {
        mColor = color;
    }

    @Override
    protected void onDraw(@Nonnull Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = Paint.obtain();
        paint.setColor(mColor);
        float inner = mRadius * 0.5f;
        canvas.drawRect(inner, inner, getWidth() - inner, getHeight() - inner, paint);

        if (isChecked()) {
            paint.setStyle(Paint.STROKE);
            paint.setStrokeWidth(mRadius);
            paint.setColor(0xFFFFFFFF);
            canvas.drawRoundRect(inner, inner, getWidth() - inner, getHeight() - inner, inner, paint);
        }
        paint.recycle();
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
