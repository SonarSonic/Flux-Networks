package sonar.fluxnetworks.client.design;

import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.view.View;

import javax.annotation.Nonnull;

public class RoundRectDrawable extends Drawable {

    private final float mRadius;
    private int mColor;

    public RoundRectDrawable(View v) {
        this(v, FluxDesign.LIGHT_GRAY);
    }

    public RoundRectDrawable(View v, int color) {
        mRadius = v.dp(3);
        setColor(color);
    }

    public void setColor(int color) {
        mColor = 0xFF000000 | color;
    }

    @Override
    public void draw(@Nonnull Canvas canvas) {
        Rect b = getBounds();
        float inner = mRadius * 0.5f;

        Paint paint = Paint.obtain();
        paint.setStyle(Paint.STROKE);
        paint.setStrokeWidth(mRadius);
        paint.setColor(mColor);
        canvas.drawRoundRect(b.left + inner, b.top + inner, b.right - inner, b.bottom - inner, mRadius, paint);
        paint.recycle();
    }

    @Override
    public boolean getPadding(@Nonnull Rect padding) {
        int r = (int) Math.ceil(mRadius / 2f);
        padding.set(r, r, r, r);
        return true;
    }
}
