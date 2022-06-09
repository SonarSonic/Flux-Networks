package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

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

    // default check state skips the animation
    public SwitchButton(Minecraft mc, int x, int y, boolean checked) {
        super(mc, x, y, WIDTH, HEIGHT);
        if (checked) {
            mChecked = true;
            mOffset = 1;
        }
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        // Calculate animation
        // 1000ms=20ticks, so animation duration is (1000/20*4)=200ms
        float delta = deltaTicks / 4f;
        mOffset = Mth.clamp(mChecked ? mOffset + delta : mOffset - delta, 0, 1);

        // Whether to use selected or unselected texture
        final int state = mChecked || isMouseHovered(mouseX, mouseY) ? 0 : 1;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BUTTONS);

        final float thumbOffset = mOffset * HEIGHT;
        // Background
        blitF(poseStack, x, y, thumbOffset * 2, 8, 64, 64, thumbOffset * 4, 16);

        // Thumb
        blitF(poseStack, x + thumbOffset, y, 8, 8, 32 * state, 80, 16, 16);

        // Track
        blitF(poseStack, x, y, width, height, 32 * state, 64, 32, 16);
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
}
