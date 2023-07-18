package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;

public class NavigationButton extends GuiButtonCore {

    private final EnumNavigationTab mTab;
    private boolean mSelected = false;

    public NavigationButton(GuiFocusable screen, int x, int y, EnumNavigationTab tab) {
        super(screen, x, y, 16, 16);
        mTab = tab;
    }

    @Override
    protected void drawButton(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        boolean hovered = isMouseHovered(mouseX, mouseY);
        if (mClickable) {
            if (mSelected || hovered) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
            }
        } else {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
        }
        RenderSystem.setShaderTexture(0, GuiFocusable.ICON);

        GuiFocusable.blitF(gr, x, y, width, height, 64 * mTab.ordinal(), 192, 64, 64);

        if (hovered && mClickable) {
            gr.drawCenteredString(screen.getMinecraft().font, mTab.getTranslatedName(),
                    x + width / 2, y - 10, 0xFFFFFFFF);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
    }

    public EnumNavigationTab getTab() {
        return mTab;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}
