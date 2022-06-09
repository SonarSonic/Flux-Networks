package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiPopupCore extends GuiFocusable {

    protected final List<GuiButtonCore> mButtons = new ArrayList<>();

    public final GuiFluxCore mHost;
    public final Player mPlayer;

    protected float mAlpha = 0;

    public GuiPopupCore(@Nonnull GuiFluxCore host, Player player) {
        super(host.getMenu(), player);
        mHost = host;
        mPlayer = player;
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

        int color = mHost.mNetwork.getColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, GuiFluxCore.FRAME);
        blit(poseStack, (width - 256) / 2, (height - 256) / 2, 0, 0, 256, 256);

        // dimmer
        int bgColor = (int) (mAlpha * 64) << 24;
        fill(poseStack, 0, 0, this.width, this.height, bgColor);

        for (Widget widget : renderables) {
            widget.render(poseStack, mouseX, mouseY, deltaTicks);
        }
    }
}
