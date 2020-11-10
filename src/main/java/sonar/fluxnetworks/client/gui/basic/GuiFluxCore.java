package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.CConfiguratorConnectMessage;
import sonar.fluxnetworks.common.network.CSelectNetworkMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class GuiFluxCore extends GuiPopupHost {

    public List<List<? extends GuiButtonCore>> buttonLists = Lists.newArrayList();
    protected List<NormalButton> buttons = Lists.newArrayList();
    protected List<SlidedSwitchButton> switches = Lists.newArrayList();

    public final PlayerEntity player; // client player
    public IFluxNetwork network;
    public AccessLevel accessLevel = AccessLevel.BLOCKED;
    protected boolean networkValid;

    public GuiFluxCore(@Nonnull FluxMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        this.player = player;
        this.network = FluxClientCache.getNetwork(container.bridge.getNetworkID());
        this.networkValid = network.isValid();
        if (FluxClientCache.superAdmin) {
            accessLevel = AccessLevel.SUPER_ADMIN;
        } else {
            network.getMemberByUUID(PlayerEntity.getUUID(player.getGameProfile())).ifPresent(m -> accessLevel = m.getAccessLevel());
        }
    }

    @Override
    public void init() {
        super.init();
        buttonLists.clear();
        buttons.clear();
        switches.clear();

        buttonLists.add(buttons);
        buttonLists.add(switches);
    }

    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        buttonLists.forEach(list -> list.forEach(b -> b.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop)));
    }

    protected void drawBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(ScreenUtils.BACKGROUND);
        blit(matrixStack, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        screenUtils.setGuiColoring(network.getNetworkColor());
        minecraft.getTextureManager().bindTexture(ScreenUtils.FRAME);
        blit(matrixStack, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        buttonLists.forEach(list -> list.forEach(b -> b.updateButton(partialTicks, mouseX, mouseY)));
    }

    @Override
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        for (List<? extends GuiButtonCore> list : buttonLists) {
            for (GuiButtonCore button : list) {
                if (button.clickable && button.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                    onButtonClicked(button, (int) mouseX - guiLeft, (int) mouseY - guiTop, mouseButton);
                    return true;
                }
            }
        }
        return super.mouseClickedMain(mouseX, mouseY, mouseButton);
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void tick() {
        super.tick();
        network = FluxClientCache.getNetwork(container.bridge.getNetworkID());
        networkValid = network.isValid();
    }

    @Override
    public void onClose() {
        super.onClose();
        FluxClientCache.setFeedback(FeedbackInfo.NONE);
    }

    protected void renderNavigationPrompt(MatrixStack matrixStack, String error, String prompt) {
        drawCenterText(matrixStack, error, xSize / 2f, 16, 0xff808080);
        matrixStack.push();
        matrixStack.scale(0.625f, 0.625f, 1);
        drawCenterText(matrixStack, FluxTranslate.CLICK_ABOVE.format(TextFormatting.AQUA + prompt + TextFormatting.RESET),
                xSize / 2f * 1.6f, 26 * 1.6f, 0x808080);
        matrixStack.pop();
    }

    protected void renderTransfer(MatrixStack matrixStack, IFluxDevice flux) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        screenUtils.resetGuiColouring();
        font.drawString(matrixStack, FluxUtils.getTransferInfo(flux, EnergyType.FE), 30, 90, 0xffffff);

        font.drawString(matrixStack, (flux.getDeviceType().isStorage() ? FluxTranslate.ENERGY.t() : FluxTranslate.BUFFER.t()) +
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

        list.add(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ? FluxTranslate.UNLIMITED.t() :
                EnergyType.storage(flux.getRawLimit())));
        list.add(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ? FluxTranslate.SURGE.t() : flux.getRawPriority()));
        list.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + FluxUtils.getDisplayPos(flux.getGlobalPos()));
        list.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + FluxUtils.getDisplayDim(flux.getGlobalPos()));
        return list;
    }

    public void setConnectedNetwork(int networkID, String password) {
        if (container.bridge instanceof TileFluxDevice) {
            NetworkHandler.INSTANCE.sendToServer(new CSelectNetworkMessage(((TileFluxDevice) container.bridge).getPos(), networkID, password));
        } else if (container.bridge instanceof ItemFluxConfigurator.MenuBridge) {
            NetworkHandler.INSTANCE.sendToServer(new CConfiguratorConnectMessage(networkID, password));
        } else if (container.bridge instanceof ItemAdminConfigurator.MenuBridge) {
            FluxClientCache.adminViewingNetwork = networkID;
        }
    }

    public void onOperationalFeedback(@Nonnull FeedbackInfo info) {

    }
}
