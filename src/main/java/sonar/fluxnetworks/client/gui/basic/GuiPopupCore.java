package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiPopupCore<T extends GuiFluxCore> extends GuiFocusable {

    protected final List<GuiButtonCore> mButtons = new ArrayList<>();

    public final T mHost;

    protected float mAlpha = 0;

    public GuiPopupCore(@Nonnull T host) {
        super(host.getMenu(), host.mPlayer);
        mHost = host;
    }

    public void init() {
        super.init();
        mButtons.clear();
    }

    @Override
    public void onClose() {
        mButtons.clear();
    }

    @Override
    public final void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void renderBg(@Nonnull PoseStack poseStack, float deltaTicks, int mouseX, int mouseY) {
        throw new UnsupportedOperationException();
    }

    public void drawForegroundLayer(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        for (GuiButtonCore button : mButtons) {
            button.drawButton(poseStack, mouseX, mouseY, deltaTicks);
        }
    }

    public void drawBackgroundLayer(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        mAlpha = Math.min(1.0f, mAlpha + deltaTicks / 6); // animation duration is (1000/20*6)=300ms

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, mAlpha);
        RenderSystem.setShaderTexture(0, GuiFluxCore.BACKGROUND);
        blit(poseStack, (width - 256) / 2, (height - 256) / 2, 0, 0, 256, 256);

        int color = mHost.mNetwork.getNetworkColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, GuiFluxCore.FRAME);
        blit(poseStack, (width - 256) / 2, (height - 256) / 2, 0, 0, 256, 256);

        // dimmer
        int bgColor = (int) (mAlpha * 64) << 24;
        fill(poseStack, 0, 0, width, height, bgColor);

        for (Widget widget : renderables) {
            widget.render(poseStack, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (GuiButtonCore button : mButtons) {
            if (button.mClickable && button.isMouseHovered(mouseX, mouseY)) {
                onButtonClicked(button, (int) mouseX, (int) mouseY, mouseButton);
                return true;
            }
        }
        for (GuiEventListener child : this.children()) {
            if (child.mouseClicked(mouseX, mouseY, mouseButton)) {
                setFocused(child);
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    setDragging(true);
                }
                return true;
            }
        }
        boolean focused = false;
        for (GuiEventListener child : this.children()) {
            if (child instanceof EditBox editBox && editBox.isFocused()) {
                focused = true;
                break;
            }
        }
        if (!focused) {
            setFocused(null);
            return true;
        }
        return false;
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
    }
}
