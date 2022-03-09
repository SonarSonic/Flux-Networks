package sonar.fluxnetworks.client.design;

import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.math.Rect;
import sonar.fluxnetworks.client.mui.FluxDeviceUI;

import javax.annotation.Nonnull;

import static icyllis.modernui.view.View.dp;

public class TabBackground extends Drawable {

    private final float mRadius;
    private int mColor;

    public TabBackground() {
        mRadius = dp(16);
        setColor(FluxDeviceUI.NETWORK_COLOR);
    }

    public void setColor(int color) {
        mColor = 0xFF000000 | color;
    }

    @Override
    public void draw(@Nonnull Canvas canvas) {
        Rect b = getBounds();
        float stroke = mRadius * 0.25f;
        float start = stroke * 0.5f;

        Paint paint = Paint.take();
        paint.setRGBA(0, 0, 0, 180);
        canvas.drawRoundRect(b.left + start, b.top + start, b.right - start, b.bottom - start, mRadius, paint);
        paint.setStyle(Paint.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(mColor);
        canvas.drawRoundRect(b.left + start, b.top + start, b.right - start, b.bottom - start, mRadius, paint);
    }
}
