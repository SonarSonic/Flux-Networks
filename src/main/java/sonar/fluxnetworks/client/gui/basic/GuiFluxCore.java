package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
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

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_bar.png");

    protected final List<GuiButtonCore> mButtons = new ArrayList<>();

    public final Player mPlayer; // client player

    private FluxNetwork mNetwork;

    public GuiFluxCore(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mPlayer = player;
        mNetwork = ClientCache.getNetwork(menu.mProvider.getNetworkID());
        // this called from main thread
        menu.mOnResultListener = (__, key, code) -> {
            final FluxTranslate t = switch (code) {
                case FluxConstants.RESPONSE_REJECT -> FluxTranslate.REJECT;
                case FluxConstants.RESPONSE_NO_OWNER -> FluxTranslate.NO_OWNER;
                case FluxConstants.RESPONSE_NO_ADMIN -> FluxTranslate.NO_ADMIN;
                case FluxConstants.RESPONSE_NO_SPACE -> FluxTranslate.NO_SPACE;
                case FluxConstants.RESPONSE_HAS_CONTROLLER -> FluxTranslate.HAS_CONTROLLER;
                case FluxConstants.RESPONSE_INVALID_USER -> FluxTranslate.INVALID_USER;
                case FluxConstants.RESPONSE_INVALID_PASSWORD -> FluxTranslate.INVALID_PASSWORD;
                case FluxConstants.RESPONSE_BANNED_LOADING -> FluxTranslate.BANNED_LOADING;
                default -> null;
            };
            if (t != null) {
                if (FluxNetworks.isModernUILoaded()) {
                    MUIIntegration.showToastError(t);
                } else {
                    getMinecraft().getToasts().addToast(SystemToast.multiline(getMinecraft(),
                            SystemToast.SystemToastIds.TUTORIAL_HINT,
                            Component.nullToEmpty(FluxNetworks.NAME),
                            t.getComponent()));
                }
            }
            onResponseAction(key, code);
        };
    }

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
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(poseStack, (width - 256) / 2, (height - 256) / 2, 0, 0, 256, 256);

        int color = mNetwork.getNetworkColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, FRAME);
        blit(poseStack, (width - 256) / 2, (height - 256) / 2, 0, 0, 256, 256);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (GuiButtonCore button : mButtons) {
            if (button.mClickable && button.isMouseHovered(mouseX, mouseY)) {
                onButtonClicked(button, (int) mouseX, (int) mouseY, mouseButton);
                return true;
            }
        }
        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
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

    protected void renderNavigationPrompt(PoseStack poseStack, String error, String prompt) {
        drawCenteredString(poseStack, font, error, width / 2, topPos + 16, 0xff808080);
        poseStack.pushPose();
        poseStack.scale(0.75f, 0.75f, 1);
        drawCenteredString(poseStack, font,
                FluxTranslate.CLICK_ABOVE.format(ChatFormatting.AQUA + prompt + ChatFormatting.RESET),
                (int) (width / 2f / 0.75f), (int) ((topPos + 28) / 0.75f), 0x808080);
        poseStack.popPose();
    }

    protected void renderTransfer(PoseStack poseStack, IFluxDevice device, int x, int y) {
        RenderSystem.enableBlend();
        font.draw(poseStack, FluxUtils.getTransferInfo(device, EnergyType.FE), x, y, 0xffffff);

        String text = device.getDeviceType().isStorage() ? FluxTranslate.ENERGY.get() : FluxTranslate.BUFFER.get();
        text += ": " + ChatFormatting.BLUE + EnergyType.FE.getStorage(device.getTransferBuffer());
        font.draw(poseStack, text, x, y + 10, 0xffffff);

        renderItemStack(device.getDisplayStack(), x - 20, y + 1);
    }

    protected void renderItemStack(ItemStack stack, int x, int y) {
        setBlitOffset(200);
        itemRenderer.blitOffset = 200.0F;
        itemRenderer.renderAndDecorateItem(stack, x, y);
        setBlitOffset(0);
        itemRenderer.blitOffset = 0.0F;
    }

     /*protected List<String> getFluxInfo(IFluxDevice flux) {
        List<String> list = Lists.newArrayList();
        list.add(TextFormatting.BOLD + flux.getCustomName());

        if (flux.isChunkLoaded()) {
            if (flux.isForcedLoading()) {
                list.add(TextFormatting.AQUA + FluxTranslate.FORCED_LOADING.t());
            }
            list.add(FluxUtils.getTransferInfo(flux, EnergyType.FE));
        } else {
            list.add(TextFormatting.RED + FluxTranslate.CHUNK_UNLOADED.t());
        }
        if (flux.getDeviceType().isStorage()) {
            list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE +
                    EnergyType.storage(flux.getTransferBuffer()));
        } else {
            list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE +
                    EnergyType.storage(flux.getTransferBuffer()));
        }

        list.add(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ?
                FluxTranslate.UNLIMITED.t() :
                EnergyType.storage(flux.getRawLimit())));
        list.add(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ?
                FluxTranslate.SURGE.t() : flux.getRawPriority()));
        list.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + FluxUtils.getDisplayPos(flux.getGlobalPos()));
        list.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + FluxUtils.getDisplayDim(flux.getGlobalPos()));
        return list;
    }*/

    public void setConnectedNetwork(int networkID, String password) {
        if (menu.mProvider instanceof TileFluxDevice) {
            ClientMessages.setTileNetwork(getToken(), (TileFluxDevice) menu.mProvider, networkID, password);
        } /*else if (menu.mProvider instanceof ItemFluxConfigurator.Provider) {
            C2SNetMsg.configuratorNet(networkID, password);
        }*/ else if (menu.mProvider instanceof ItemAdminConfigurator.Provider) {
            ClientCache.sAdminViewingNetwork = networkID;
        }
    }

    protected void renderNetwork(PoseStack poseStack, String name, int color, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, GUI_BAR);
        blit(poseStack, x, y, 0, 0, 135, 12, 256, 256);
        font.draw(poseStack, name, x + 4, y + 2, 0xffffff);
    }

    /**
     * Called when a non-text response is received.
     *
     * @param key  a request key
     * @param code a response code
     */
    protected void onResponseAction(int key, int code) {
    }
}
