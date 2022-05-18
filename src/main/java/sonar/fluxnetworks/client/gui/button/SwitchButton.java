package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.client.gui.basic.GuiButton;

/**
 * A simple switch button with sliding thumb and track.
 */
public class SwitchButton extends GuiButton {

    private static final int WIDTH = 16;
    private static final int HEIGHT = 8;

    // switch on/off
    private boolean mChecked = false;

    // thumb offset, max value is (width/2)=8
    private float mOffset;

    // default check state skips the animation
    public SwitchButton(Minecraft mc, int x, int y, boolean checked) {
        super(mc, x, y, WIDTH, HEIGHT);
        if (checked) {
            mChecked = true;
            mOffset = 8;
        }
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        // Calculate animation
        // Offset range is 0..8, 1000ms=20ticks, we multiply 2 so animation duration is (1000*8/20/2)=200ms
        float delta = deltaTicks * 2;
        if (mChecked) {
            if (mOffset <= 8 - delta) {
                mOffset += delta;
            } else {
                mOffset = 8;
            }
        } else {
            if (mOffset >= delta) {
                mOffset -= delta;
            } else {
                mOffset = 0;
            }
        }

        // Whether to use selected or unselected texture
        final int state = mChecked || isMouseHovered(mouseX, mouseY) ? 0 : 1;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.setShaderTexture(0, BUTTONS);

        // Background
        blit(poseStack, x, y, mOffset * 2, 8, 64, 64, mOffset * 4, 16);

        // Thumb
        blit(poseStack, x + mOffset, y, 8, 8, 32 * state, 80, 16, 16);

        // Track
        blit(poseStack, x, y, width, height, 32 * state, 64, 32, 16);
    }

    public void toggle() {
        mChecked = !mChecked;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
