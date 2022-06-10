package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

public class PageLabelButton extends GuiButtonCore {

    public int mPage, mPages, mColor;
    public int mHoveredPage = -1;

    private int mShowTick;

    public PageLabelButton(Minecraft mc, int x, int y, int width, int height, int page, int pages, int color) {
        super(mc, x, y, width, height);
        mColor = color;
        refreshPages(page, pages);
    }

    @Override
    public void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        int pages = mPages;
        if (pages <= 0) {
            return;
        }

        boolean hovered = isMouseHovered(mouseX, mouseY);

        int dotsWidth = (pages * height) + (pages - 1); // spacing
        int startX = x + (width - dotsWidth) / 2;

        if (hovered) {
            mHoveredPage = Mth.clamp((mouseX - startX) / height, 0, pages - 1);
        } else {
            mHoveredPage = -1;
        }

        for (int i = 0; i < pages; i++) {
            if (i == mPage) {
                fill(poseStack, startX, y, startX + height, y + height, mColor | 0xf0000000);
            } else if (i == mHoveredPage) {
                fill(poseStack, startX, y, startX + height, y + height, 0xf0808080);
            } else {
                int inset = height / 4;
                fill(poseStack, startX + inset, y + inset, startX + height - inset, y + height - inset, 0xf0808080);
            }
        }

        if (hovered) {
            drawCenteredString(poseStack, mc.font, (mHoveredPage + 1) + " / " + pages, x + width / 2, y + 6, mColor);
        } else if (mShowTick > 0) {
            int alpha = Math.min(255, mShowTick * 32);
            drawCenteredString(poseStack, mc.font, (mPage + 1) + " / " + pages, 88, y + 6, mColor | alpha << 24);
            mShowTick--;
        }
    }

    /**
     * @param page  0-based indexing
     * @param pages max number of pages
     */
    public void refreshPages(int page, int pages) {
        mPage = page;
        mPages = pages;
        mHoveredPage = -1;
        mShowTick = 40;
    }
}
