package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;

public abstract class GuiButtonCore extends GuiComponent {

    public static final ResourceLocation BUTTONS = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_button.png");

    public final GuiFocusable screen;

    public int x;
    public int y;
    public int width;
    public int height;

    protected boolean mClickable = true;

    protected GuiButtonCore(GuiFocusable screen, int x, int y, int width, int height) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected abstract void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks);

    public boolean isClickable() {
        return mClickable;
    }

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }

    public final boolean isMouseHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static void drawOuterFrame(PoseStack poseStack, int x, int y, int width, int height, int color) {
        fill(poseStack, x - 1, y - 1, x + width + 1, y, color);
        fill(poseStack, x - 1, y + height, x + width + 1, y + height + 1, color);
        fill(poseStack, x - 1, y, x, y + height, color);
        fill(poseStack, x + width, y, x + width + 1, y + height, color);
    }
}
