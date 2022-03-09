package sonar.fluxnetworks.client.design;

import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.math.Rect;

import javax.annotation.Nonnull;

import static icyllis.modernui.view.View.dp;

public class RoundRectDrawable extends Drawable {

    private final float mRadius;
    private int mColor;

    public RoundRectDrawable() {
        this(FluxDesign.LIGHT_GRAY);
    }

    public RoundRectDrawable(int color) {
        mRadius = dp(3);
        setColor(color);
    }

    public void setColor(int color) {
        mColor = 0xFF000000 | color;
    }

    @Override
    public void draw(@Nonnull Canvas canvas) {
        Rect b = getBounds();
        float inner = mRadius * 0.5f;

        Paint paint = Paint.take();
        paint.setStyle(Paint.STROKE);
        paint.setStrokeWidth(mRadius);
        paint.setColor(mColor);
        canvas.drawRoundRect(b.left + inner, b.top + inner, b.right - inner, b.bottom - inner, mRadius, paint);
    }

    @Override
    public boolean getPadding(@Nonnull Rect padding) {
        int r = (int) Math.ceil(mRadius / 2f);
        padding.set(r, r, r, r);
        return true;
    }
}
