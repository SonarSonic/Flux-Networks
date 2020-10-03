package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.network.AccessType;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.ConfiguratorNetworkConnectPacket;
import sonar.fluxnetworks.common.network.TilePacket;
import sonar.fluxnetworks.common.network.TilePacketEnum;
import sonar.fluxnetworks.common.network.TilePacketHandler;

import java.text.NumberFormat;
import java.util.List;

public abstract class GuiFluxCore extends GuiPopUpHost {

    public List<List<? extends GuiButtonCore>> buttonLists = Lists.newArrayList();
    protected List<NormalButton> buttons = Lists.newArrayList();
    protected List<SlidedSwitchButton> switches = Lists.newArrayList();

    public IFluxNetwork network;
    public AccessType accessPermission = AccessType.BLOCKED;
    protected boolean networkValid;
    private int timer1;

    public GuiFluxCore(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        this.network = FluxClientCache.INSTANCE.getNetwork(connector.getNetworkID());
        this.networkValid = network.isValid();
    }

    @Override
    public int getGuiColouring() {
        return network.getNetworkColor();
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
        if (timer1 == 0) {
            this.network = FluxClientCache.INSTANCE.getNetwork(connector.getNetworkID());
            this.networkValid = network.isValid();
        }
        timer1++;
        timer1 %= 20;
    }

    @Override
    public void onClose() {
        super.onClose();
        FluxNetworks.PROXY.setFeedback(EnumFeedbackInfo.NONE, false);
        FluxNetworks.PROXY.setFeedback(EnumFeedbackInfo.NONE, true);
    }


    protected void renderNavigationPrompt(MatrixStack matrixStack, String error, String prompt) {
        RenderSystem.pushMatrix();
        drawCenteredString(matrixStack, font, error, xSize / 2, 16, 0x808080);
        RenderSystem.scaled(0.625, 0.625, 0.625);
        drawCenteredString(matrixStack, font, FluxTranslate.CLICK.t() + TextFormatting.AQUA + ' ' + prompt + ' ' + TextFormatting.RESET + FluxTranslate.ABOVE.t(), (int) (xSize / 2 * 1.6), (int) (26 * 1.6), 0x808080);
        RenderSystem.scaled(1.6, 1.6, 1.6);
        RenderSystem.popMatrix();
    }

    protected void renderTransfer(MatrixStack matrixStack, IFluxDevice fluxConnector, int color, int x, int y) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        screenUtils.resetGuiColouring();
        font.drawString(matrixStack, FluxUtils.getTransferInfo(fluxConnector.getDeviceType(), EnergyType.FE, fluxConnector.getTransferHandler().getChange()), x, y, color);

        font.drawString(matrixStack, (fluxConnector.getDeviceType().isStorage() ? FluxTranslate.ENERGY.t() : FluxTranslate.BUFFER.t()) +
                ": " + TextFormatting.BLUE + FluxUtils.format(fluxConnector.getTransferHandler().getBuffer(), FluxUtils.TypeNumberFormat.COMMAS,
                EnergyType.FE, false), x, y + 10, 0xffffff);

        screenUtils.renderItemStack(fluxConnector.getDisplayStack(), x - 20, y + 1);

        RenderSystem.popMatrix();
    }


    protected List<String> getFluxInfo(IFluxDevice flux) {
        List<String> list = Lists.newArrayList();
        list.add(TextFormatting.BOLD + flux.getCustomName());
        CompoundNBT tag = flux.getDisplayStack().getChildTag(FluxUtils.FLUX_DATA);

        if (flux.isChunkLoaded()) {
            if (flux.isForcedLoading()) {
                list.add(TextFormatting.AQUA + FluxTranslate.FORCED_LOADING.t());
            }
            list.add(FluxUtils.getTransferInfo(flux.getDeviceType(), EnergyType.FE, flux.getChange()));
            if (flux.getDeviceType() == FluxDeviceType.STORAGE) {
                list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            } else {
                list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            }
        } else {
            list.add(TextFormatting.RED + FluxTranslate.CHUNK_UNLOADED.t());
            if (tag != null) {
                if (tag.contains("energy")) {
                    list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getInt("energy")) + "RF");
                } else {
                    list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getLong("buffer")) + "RF");
                }
            }
        }

        list.add(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ? FluxTranslate.UNLIMITED.t() : flux.getLogicLimit()));
        list.add(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ? FluxTranslate.SURGE.t() : flux.getLogicPriority()));
        list.add(TextFormatting.ITALIC + FluxUtils.getDisplayString(flux.getGlobalPos()));
        return list;
    }

    public void onSuperAdminChanged() {
    }

    public void setConnectedNetwork(int networkID, String password) {
        if (connector instanceof IFluxDevice) {
            //TODO
            //PacketHandler.CHANNEL.sendToServer(new TilePacket(TilePacketEnum.SET_NETWORK, TilePacketHandler.getSetNetworkPacket(networkID, password), ((IFluxDevice) connector).getCoords()));
        }
        if (connector instanceof ItemAdminConfigurator.ContainerProvider) {
            FluxNetworks.PROXY.setAdminViewingNetwork(FluxClientCache.INSTANCE.getNetwork(networkID));
        }
        if (connector instanceof ItemFluxConfigurator.ContainerProvider) {
            PacketHandler.CHANNEL.sendToServer(new ConfiguratorNetworkConnectPacket(networkID, password));
        }
    }
}
