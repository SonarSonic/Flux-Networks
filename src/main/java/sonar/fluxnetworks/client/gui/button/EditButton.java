package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;

/**
 * Button may have two states, and have different icons and texts.
 */
public class EditButton extends GuiButtonCore {

    private final int mCheckU0;
    private final int mUncheckU0;
    private final String mCheckText;
    private final String mUncheckText;

    private boolean mChecked = false;

    public EditButton(GuiFocusable screen, int x, int y,
                      int checkU0, int uncheckU0, String checkText, String uncheckText) {
        this(screen, x, y, 10, 10, checkU0, uncheckU0, checkText, uncheckText);
    }

    public EditButton(GuiFocusable screen, int x, int y, int width, int height,
                      int checkU0, int uncheckU0, String checkText, String uncheckText) {
        super(screen, x, y, width, height);
        mCheckU0 = checkU0;
        mUncheckU0 = uncheckU0;
        mCheckText = checkText;
        mUncheckText = uncheckText;
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        boolean hovered = isMouseHovered(mouseX, mouseY);
        if (mClickable) {
            if (hovered) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
            }
        } else {
            RenderSystem.setShaderColor(0.35f, 0.35f, 0.35f, 1.0f);
        }
        RenderSystem.setShaderTexture(0, GuiFocusable.ICON);

        screen.blitF(poseStack, x, y, width, height, mChecked ? mCheckU0 : mUncheckU0, 256, 64, 64);

        if (hovered && mClickable) {
            drawCenteredString(poseStack, screen.getMinecraft().font, mChecked ? mCheckText : mUncheckText,
                    x + width / 2, y - 9, 0xFFFFFFFF);
        }
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
