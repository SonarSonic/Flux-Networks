package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.network.EnumAccessType;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.AdminConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.network.ConfiguratorNetworkConnectPacket;
import sonar.fluxnetworks.common.network.TilePacket;
import sonar.fluxnetworks.common.network.TilePacketHandler;
import sonar.fluxnetworks.common.network.TilePacketEnum;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.util.text.TextFormatting;

import java.text.NumberFormat;
import java.util.List;

public abstract class GuiFluxCore extends GuiPopUpHost {

    public List<List<? extends GuiButtonCore>> buttonLists = Lists.newArrayList();
    protected List<NormalButton> buttons = Lists.newArrayList();
    protected List<SlidedSwitchButton> switches = Lists.newArrayList();

    public IFluxNetwork network;
    public EnumAccessType accessPermission = EnumAccessType.NONE;
    protected boolean networkValid;
    private int timer1;

    public GuiFluxCore(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        this.network = FluxNetworkCache.INSTANCE.getClientNetwork(connector.getNetworkID());
        this.networkValid = !network.isInvalid();
    }

    @Override
    public int getGuiColouring(){
        return network.getSetting(NetworkSettings.NETWORK_COLOR);
    }

    @Override
    public void init(){
        super.init();
        buttonLists.clear();
        buttons.clear();
        switches.clear();

        buttonLists.add(buttons);
        buttonLists.add(switches);
    }

    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        buttonLists.forEach(list -> list.forEach(b -> b.drawButton(minecraft, mouseX, mouseY, guiLeft, guiTop)));
    }

    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
        buttonLists.forEach(list -> list.forEach(b -> b.updateButton(partialTicks, mouseX, mouseY)));
    }

    @Override
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        CYCLE: for(List<? extends GuiButtonCore> list : buttonLists){
           for(GuiButtonCore button : list){
                if(button.clickable && button.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
                    onButtonClicked(button, (int)mouseX - guiLeft, (int)mouseY - guiTop, mouseButton);
                    return true;
                }
            }
        }
        return super.mouseClickedMain(mouseX, mouseY, mouseButton);
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){}

    @Override
    public void tick() {
        super.tick();
        if(timer1 == 0) {
            this.network = FluxNetworkCache.INSTANCE.getClientNetwork(connector.getNetworkID());
            this.networkValid = !network.isInvalid();
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


    protected void renderNavigationPrompt(String error, String prompt) {
        RenderSystem.pushMatrix();
        drawCenteredString(font, error, xSize / 2, 16, 0x808080);
        RenderSystem.scaled(0.625, 0.625, 0.625);
        drawCenteredString(font, FluxTranslate.CLICK.t() + TextFormatting.AQUA + ' ' + prompt + ' ' + TextFormatting.RESET + FluxTranslate.ABOVE.t(), (int) (xSize / 2 * 1.6), (int) (26 * 1.6), 0x808080);
        RenderSystem.scaled(1.6, 1.6, 1.6);
        RenderSystem.popMatrix();
    }

    protected void renderTransfer(IFluxConnector fluxConnector, int color, int x, int y) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        screenUtils.resetGuiColouring();
        font.drawString(FluxUtils.getTransferInfo(fluxConnector.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), fluxConnector.getTransferHandler().getChange()), x, y, color);

        font.drawString((fluxConnector.getConnectionType().isStorage() ? FluxTranslate.ENERGY.t() : FluxTranslate.BUFFER.t()) +
                ": " + TextFormatting.BLUE + FluxUtils.format(fluxConnector.getTransferHandler().getBuffer(), FluxUtils.TypeNumberFormat.COMMAS,
                network.getSetting(NetworkSettings.NETWORK_ENERGY), false), x, y + 10, 0xffffff);

        screenUtils.renderItemStack(fluxConnector.getDisplayStack(), x - 20, y + 1);

        RenderSystem.popMatrix();
    }


    protected List<String> getFluxInfo(IFluxConnector flux) {
        List<String> list = Lists.newArrayList();
        list.add(TextFormatting.BOLD + flux.getCustomName());
        CompoundNBT tag = flux.getDisplayStack().getChildTag(FluxUtils.FLUX_DATA);

        if(flux.isChunkLoaded()) {
            if(flux.isForcedLoading()) {
                list.add(TextFormatting.AQUA + FluxTranslate.FORCED_LOADING.t());
            }
            list.add(FluxUtils.getTransferInfo(flux.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), flux.getChange()));
            if(flux.getConnectionType() == EnumConnectionType.STORAGE) {
                list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            } else {
                list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            }
        } else {
            list.add(TextFormatting.RED + FluxTranslate.CHUNK_UNLOADED.t());
            if(tag != null) {
                if (tag.contains("energy")) {
                    list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getInt("energy")) + "RF");
                } else {
                    list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getLong("buffer")) + "RF");
                }
            }
        }

        list.add(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ? FluxTranslate.UNLIMITED.t() : flux.getCurrentLimit()));
        list.add(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ? FluxTranslate.SURGE.t() : flux.getPriority()));
        list.add(TextFormatting.ITALIC + flux.getCoords().getStringInfo());
        return list;
    }

    public void onSuperAdminChanged(){}

    public void setConnectedNetwork(int networkID, String password){
        if(connector instanceof IFluxConnector){
            PacketHandler.CHANNEL.sendToServer(new TilePacket(TilePacketEnum.SET_NETWORK, TilePacketHandler.getSetNetworkPacket(networkID, password), ((IFluxConnector)connector).getCoords()));
        }
        if(connector instanceof AdminConfiguratorItem.ContainerProvider){
            FluxNetworks.PROXY.setAdminViewingNetworkID(networkID);
            FluxNetworks.PROXY.setAdminViewingNetwork(FluxNetworkCache.INSTANCE.getClientNetwork(networkID));
        }
        if(connector instanceof FluxConfiguratorItem.ContainerProvider){
            PacketHandler.CHANNEL.sendToServer(new ConfiguratorNetworkConnectPacket(networkID, password));
        }
    }
}
