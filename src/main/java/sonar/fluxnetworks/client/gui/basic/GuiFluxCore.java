package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.client.ClientRepository;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiFluxCore extends GuiPopupHost {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_bar.png");

    protected final List<GuiButtonCore> mButtons = new ArrayList<>();

    public final Player mPlayer; // client player

    public FluxNetwork mNetwork;
    public AccessLevel mAccessLevel = AccessLevel.BLOCKED;

    public GuiFluxCore(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
        mPlayer = player;
        mNetwork = ClientRepository.getNetwork(menu.mProvider.getNetworkID());
        if (ClientRepository.superAdmin) {
            mAccessLevel = AccessLevel.SUPER_ADMIN;
        } else {
            NetworkMember member = mNetwork.getMemberByUUID(player.getUUID());
            if (member != null) {
                mAccessLevel = member.getAccessLevel();
            }
        }
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

        int color = mNetwork.getColor();
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
        mNetwork = ClientRepository.getNetwork(menu.mProvider.getNetworkID());
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientRepository.setFeedbackText(FeedbackInfo.NONE);
    }

    /*protected void renderNavigationPrompt(PoseStack matrixStack, String error, String prompt) {
        drawCenterText(matrixStack, error, xSize / 2f, 16, 0xff808080);
        matrixStack.push();
        matrixStack.scale(0.625f, 0.625f, 1);
        drawCenterText(matrixStack,
                FluxTranslate.CLICK_ABOVE.format(TextFormatting.AQUA + prompt + TextFormatting.RESET),
                xSize / 2f * 1.6f, 26 * 1.6f, 0x808080);
        matrixStack.pop();
    }

    protected void renderTransfer(MatrixStack matrixStack, IFluxDevice flux) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        screenUtils.resetGuiColouring();
        font.drawString(matrixStack, FluxUtils.getTransferInfo(flux, EnergyType.FE), 30, 90, 0xffffff);

        font.drawString(matrixStack, (flux.getDeviceType().isStorage() ? FluxTranslate.ENERGY.t() :
                FluxTranslate.BUFFER.t()) +
                ": " + TextFormatting.BLUE + EnergyType.storage(flux.getTransferBuffer()), 30, 90 + 10, 0xffffff);

        screenUtils.renderItemStack(flux.getDisplayStack(), 30 - 20, 90 + 1);
    }

    protected List<String> getFluxInfo(IFluxDevice flux) {
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
    }

    public void setConnectedNetwork(int networkID, String password) {
        if (container.bridge instanceof FluxDeviceEntity) {
            C2SNetMsg.setNetwork(((FluxDeviceEntity) container.bridge).getPos(), networkID, password);
        } else if (container.bridge instanceof ItemFluxConfigurator.MenuBridge) {
            C2SNetMsg.configuratorNet(networkID, password);
        } else if (container.bridge instanceof ItemAdminConfigurator.MenuBridge) {
            FluxClientCache.adminViewingNetwork = networkID;
        }
    }*/

    protected void renderNetwork(PoseStack poseStack, int x, int y) {
        RenderSystem.enableBlend();
        int color = mNetwork.getColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, GUI_BAR);
        blit(poseStack, x, y, 0, 0, 135, 12, 256, 256);
        font.draw(poseStack, mNetwork.getName(), x + 4, y + 2, 0xffffff);
    }

    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
    }
}
