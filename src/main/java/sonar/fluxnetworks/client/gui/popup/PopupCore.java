package sonar.fluxnetworks.client.gui.popup;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.misc.FluxMenu;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class PopupCore<T extends GuiFluxCore> extends GuiFocusable<FluxMenu> {

    protected List<NormalButton> popButtons = Lists.newArrayList();
    protected List<SlidedSwitchButton> popSwitches = new ArrayList<>();

    public final T host;
    public final PlayerEntity player;

    private float alpha = 0;

    public PopupCore(@Nonnull T host, PlayerEntity player) {
        super(host.getContainer(), player);
        this.host = host;
        this.player = player;
    }

    public void init() {
        super.init();
        popButtons.clear();
        popSwitches.clear();
    }

    public void openPopUp() {
        super.init(Minecraft.getInstance(), host.width, host.height);
    }

    public void closePopUp() {
        popButtons.clear();
        popSwitches.clear();
    }

    public void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        popButtons.forEach(b -> b.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop));
    }

    @Override
    public void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        alpha = Math.min(1.0f, alpha + partialTicks * 0.25f);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
        minecraft.getTextureManager().bindTexture(ScreenUtils.BACKGROUND);
        blit(matrixStack, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        screenUtils.setGuiColoring(host.network.getNetworkColor(), alpha);
        minecraft.getTextureManager().bindTexture(ScreenUtils.FRAME);
        blit(matrixStack, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        int bgColor = (int) (alpha * 64) << 24;
        fill(matrixStack, -guiLeft, -guiTop, this.width, this.height, bgColor);

        //screenUtils.drawRectWithBackground(guiLeft + 8, guiTop + 13, 150, 160, 0xccffffff, 0xb0000000);
        for (SlidedSwitchButton button : popSwitches) {
            button.updateButton(partialTicks, mouseX, mouseY);
        }
        for (Widget widget : buttons) {
            widget.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }
}
