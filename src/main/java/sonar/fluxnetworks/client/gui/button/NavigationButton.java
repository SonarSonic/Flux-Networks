package sonar.fluxnetworks.client.gui.button;

/*import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

public class NavigationButton extends GuiButtonCore {

    public EnumNavigationTab tab;
    public boolean isCurrentTab = false;

    public NavigationButton(int x, int y, EnumNavigationTab tab) {
        super(x, y, 16, 16, 0);
        this.tab = tab;
    }

    @Override
    public void drawButton(Minecraft mc, MatrixStack matrixStack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0f);
        mc.getTextureManager().bindTexture(ScreenUtils.BUTTONS);
        blit(matrixStack, x, y, 16 * tab.ordinal(), 16 * getHoverState(isCurrentTab || isMouseHovered(mc, mouseX, mouseY)), 16, 16);

        if (isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
            FontRenderer fontRenderer = mc.fontRenderer;
            String text = tab.getTranslatedName();
            fontRenderer.drawString(matrixStack, text, x - fontRenderer.getStringWidth(text) / 2f + 8, y - 10, 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

    public void setMain() {
        isCurrentTab = true;
    }
}*/
