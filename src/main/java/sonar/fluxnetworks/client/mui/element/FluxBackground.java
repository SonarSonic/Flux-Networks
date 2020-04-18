package sonar.fluxnetworks.client.mui.element;

import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.master.IDrawable;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.mui.module.NavigationHome;

import javax.annotation.Nonnull;

/**
 * Draw flux default background
 * @author BloCamLimb
 */
public class FluxBackground implements IDrawable {

    private final NavigationHome module;

    private float x1, x2, y1, y2;

    private float r, g, b;

    public FluxBackground(NavigationHome module) {
        this.module = module;
        updateFrameColor();
    }

    @Override
    public void draw(@Nonnull Canvas canvas, float v) {
        canvas.setRGBA(0, 0, 0, 0.5f);
        canvas.drawRoundedRect(x1, y1, x2, y2, 5);
        canvas.setRGBA(r, g, b, 1.0f);
        canvas.drawRoundedRectFrame(x1, y1, x2, y2, 5);
    }

    @Override
    public void resize(int width, int height) {
        this.x1 = width / 2f - 85;
        this.x2 = width / 2f + 85;
        this.y1 = height / 2f - 77;
        this.y2 = height / 2f + 93;
    }

    @Override
    public void tick(int ticks) {
        if ((ticks & 15) == 0) {
            updateFrameColor();
        }
    }

    private void updateFrameColor() {
        int color = module.getNetwork().getSetting(NetworkSettings.NETWORK_COLOR);
        r = ScreenUtils.getRed(color);
        g = ScreenUtils.getGreen(color);
        b = ScreenUtils.getBlue(color);
    }
}
