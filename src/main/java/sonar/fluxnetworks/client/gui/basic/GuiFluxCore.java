package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.integration.MUIIntegration;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Gui that interacts flux networks.
 */
public abstract class GuiFluxCore extends GuiPopupHost {

    protected final List<GuiButtonCore> mButtons = new ArrayList<>();

    public final Player mPlayer; // client player

    private FluxNetwork mNetwork;

    public GuiFluxCore(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mPlayer = player;
        mNetwork = ClientCache.getNetwork(menu.mProvider.getNetworkID());
        menu.mOnResultListener = this::onResponse;
    }

    // this called from main thread
    private void onResponse(FluxMenu menu, int key, int code) {
        final FluxTranslate t = FluxTranslate.fromResponseCode(code);
        if (t != null) {
            if (FluxNetworks.isModernUILoaded()) {
                MUIIntegration.showToastError(t);
            } else {
                getMinecraft().getToasts().addToast(SystemToast.multiline(getMinecraft(),
                        SystemToast.SystemToastIds.TUTORIAL_HINT,
                        Component.literal(FluxNetworks.NAME),
                        t.getComponent()));
            }
        }
        onResponseAction(key, code);
    }

    /**
     * @return the menu token
     */
    public int getToken() {
        return menu.containerId;
    }

    /**
     * @return current network
     */
    @Nonnull
    public FluxNetwork getNetwork() {
        return mNetwork;
    }

    /**
     * @return current access
     */
    @Nonnull
    public AccessLevel getAccessLevel() {
        return mNetwork.getPlayerAccess(mPlayer);
    }

    @Override
    public void init() {
        super.init();
        mButtons.clear();
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        for (GuiButtonCore button : mButtons) {
            button.drawButton(poseStack, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    protected void drawBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blitBackgroundOrFrame(poseStack);

        int color = mNetwork.getNetworkColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, FRAME);
        blitBackgroundOrFrame(poseStack);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (GuiButtonCore button : mButtons) {
            if (button.mClickable && button.isMouseHovered(mouseX, mouseY)) {
                onButtonClicked(button, (float) mouseX, (float) mouseY, mouseButton);
                return true;
            }
        }
        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        mNetwork = ClientCache.getNetwork(menu.mProvider.getNetworkID());
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    /**
     * Render the network bar on the top.
     */
    protected void renderNetwork(PoseStack poseStack, String name, int color, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, ICON);
        int x = leftPos + 20;
        blitF(poseStack, x, y, 135, 12, 0, 320, 270, 24);
        font.draw(poseStack, name, x + 4, y + 2, 0xffffff);
    }

    /**
     * Render the energy change.
     */
    protected void renderTransfer(PoseStack poseStack, IFluxDevice device, int x, int y) {
        RenderSystem.enableBlend();
        font.draw(poseStack, FluxUtils.getTransferInfo(device, EnergyType.FE), x, y, 0xffffff);

        String text = device.getDeviceType().isStorage() ? FluxTranslate.ENERGY.get() : FluxTranslate.BUFFER.get();
        text += ": " + ChatFormatting.BLUE + EnergyType.FE.getStorage(device.getTransferBuffer());
        font.draw(poseStack, text, x, y + 10, 0xffffff);

        renderItemStack(device.getDisplayStack(), x - 20, y + 1);
    }

    protected void renderItemStack(ItemStack stack, int x, int y) {
        setBlitOffset(50);
        itemRenderer.blitOffset = 50.0F;
        itemRenderer.renderAndDecorateItem(stack, x, y);
        setBlitOffset(0);
        itemRenderer.blitOffset = 0.0F;
    }

    public void setConnectedNetwork(FluxNetwork network, String password) {
        if (menu.mProvider instanceof TileFluxDevice) {
            ClientMessages.tileNetwork(getToken(), (TileFluxDevice) menu.mProvider, network, password);
        } /*else if (menu.mProvider instanceof ItemFluxConfigurator.Provider) {
            C2SNetMsg.configuratorNet(networkID, password);
        }*/ else if (menu.mProvider instanceof ItemAdminConfigurator.Provider) {
            ClientCache.sAdminViewingNetwork = network.getNetworkID();
        }
    }

    /**
     * Called when a server response is received.
     *
     * @param key  the request key
     * @param code the response code
     */
    protected void onResponseAction(int key, int code) {
    }
}
