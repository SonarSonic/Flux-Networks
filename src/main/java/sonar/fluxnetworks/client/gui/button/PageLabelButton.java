package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

public class PageLabelButton extends GuiButtonCore {

    public int mPage, mPages, mColor;
    public int mHoveredPage = -1;

    private float mShowTick;

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

        int dotSize = height;
        int dotsWidth = (pages * dotSize) + (pages - 1); // with padding
        int startX = x + (width - dotsWidth) / 2;

        boolean hovered = isMouseHovered(mouseX, mouseY);

        if (hovered) {
            int pos = (int) Math.floor((mouseX - startX) / (dotSize + 1F));
            if (pos < 0 || pos >= pages) {
                mHoveredPage = -1;
            } else {
                mHoveredPage = pos;
            }
        } else {
            mHoveredPage = -1;
        }

        for (int i = 0; i < pages; i++) {
            if (i == mPage) {
                fill(poseStack, startX, y, startX + dotSize, y + dotSize, mColor | 0xF0000000);
            } else if (i == mHoveredPage) {
                fill(poseStack, startX, y, startX + dotSize, y + dotSize, 0xC0808080);
            } else {
                int inset = dotSize / 4;
                fill(poseStack, startX + inset, y + inset, startX + dotSize - inset, y + dotSize - inset, 0xC0808080);
            }
            startX += dotSize + 1;
        }

        if (mHoveredPage != -1) {
            drawCenteredString(poseStack, mc.font, (mHoveredPage + 1) + " / " + pages,
                    x + width / 2, y + 6, mColor);
        } else if (mShowTick > 0) {
            int alpha = (int) Math.min(255, mShowTick * 24);
            if (alpha > 3) {
                drawCenteredString(poseStack, mc.font, (mPage + 1) + " / " + pages,
                        x + width / 2, y + 6, mColor | alpha << 24);
            }
            mShowTick -= deltaTicks;
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
        mShowTick = 20;
    }
}
