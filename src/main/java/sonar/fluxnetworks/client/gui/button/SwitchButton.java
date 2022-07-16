package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;
import sonar.fluxnetworks.common.util.FluxUtils;

/**
 * A simple switch button with sliding thumb and track.
 */
public class SwitchButton extends GuiButtonCore {

    private static final int WIDTH = 16;
    private static final int HEIGHT = 8;

    // switch on/off
    private boolean mChecked = false;

    // thumb offset, fraction (0..1)
    private float mOffset;

    private int mColor;

    // default check state skips the animation

    public SwitchButton(GuiFocusable screen, int x, int y, boolean checked) {
        this(screen, x, y, checked, EnumNetworkColor.BLUE.getRGB());
    }

    public SwitchButton(GuiFocusable screen, int x, int y, boolean checked, int color) {
        super(screen, x, y, WIDTH, HEIGHT);
        if (checked) {
            mChecked = true;
            mOffset = 1;
        }
        mColor = color;
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        // Calculate animation
        // 1000ms=20ticks, so animation duration is (1000/20*4)=200ms
        float delta = deltaTicks / 4f;
        mOffset = Mth.clamp(mChecked ? mOffset + delta : mOffset - delta, 0, 1);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, GuiFocusable.ICON);

        float r = FluxUtils.getRed(mColor);
        float g = FluxUtils.getGreen(mColor);
        float b = FluxUtils.getBlue(mColor);

        final float thumbOffset = mOffset * height;

        if (mClickable) {
            RenderSystem.setShaderColor(r, g, b, 0.9F);
        } else {
            RenderSystem.setShaderColor(r * 0.5F, g * 0.5F, b * 0.5F, 0.9F);
        }
        // Background
        screen.blitF(poseStack, x, y, thumbOffset * 2, height, 320, 256, thumbOffset * 8, 32);

        if (mClickable) {
            if (isMouseHovered(mouseX, mouseY)) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else if (mChecked) {
                RenderSystem.setShaderColor(0.85F, 0.85F, 0.85F, 1.0F);
            } else {
                RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, 1.0F);
            }
        } else {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
        }
        // Track
        screen.blitF(poseStack, x, y, width, height, 256, 256, 64, 32);

        if (mClickable) {
            if (mChecked || isMouseHovered(mouseX, mouseY)) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                RenderSystem.setShaderColor(0.7F, 0.7F, 0.7F, 1.0F);
            }
        } else {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
        }
        // Thumb
        screen.blitF(poseStack, x + thumbOffset, y, width / 2f, height, 256, 288, 32, 32);
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
