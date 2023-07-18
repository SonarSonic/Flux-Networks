package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
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
    public final void render(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics gr, int mouseX, int mouseY) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void renderBg(@Nonnull GuiGraphics gr, float deltaTicks, int mouseX, int mouseY) {
        throw new UnsupportedOperationException();
    }

    public void drawForegroundLayer(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        for (GuiButtonCore button : mButtons) {
            button.drawButton(gr, mouseX, mouseY, deltaTicks);
        }
    }

    public void drawBackgroundLayer(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        mAlpha = Math.min(1.0f, mAlpha + deltaTicks / 6); // animation duration is (1000/20*6)=300ms

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, mAlpha);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blitBackgroundOrFrame(gr);

        int color = mHost.getNetwork().getNetworkColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), mAlpha);
        RenderSystem.setShaderTexture(0, FRAME);
        blitBackgroundOrFrame(gr);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // dimmer
        int bgColor = (int) (mAlpha * 128) << 24;
        gr.fill(0, 0, width, height, bgColor);

        for (Renderable widget : renderables) {
            widget.render(gr, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean result = false;
        for (GuiButtonCore button : mButtons) {
            if (button.mClickable && button.isMouseHovered(mouseX, mouseY)) {
                onButtonClicked(button, (int) mouseX, (int) mouseY, mouseButton);
                result = true;
                break;
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
        return result;
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
    }
}
