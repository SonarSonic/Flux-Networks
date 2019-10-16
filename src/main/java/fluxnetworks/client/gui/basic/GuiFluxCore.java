package fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.*;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.item.ItemAdminConfigurator;
import fluxnetworks.common.item.ItemConfigurator;
import fluxnetworks.common.network.PacketSetConfiguratorNetwork;
import fluxnetworks.common.network.PacketTile;
import fluxnetworks.common.network.PacketTileHandler;
import fluxnetworks.common.network.PacketTileType;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.scale;

public abstract class GuiFluxCore extends GuiPopUpHost {

    public List<List<? extends GuiButtonCore>> buttonLists = Lists.newArrayList();
    protected List<NormalButton> buttons = Lists.newArrayList();
    protected List<TextboxButton> textBoxes = Lists.newArrayList();
    protected List<SlidedSwitchButton> switches = Lists.newArrayList();

    public IFluxNetwork network;
    public AccessPermission accessPermission = AccessPermission.NONE;
    protected boolean networkValid;
    private int timer1;

    public GuiFluxCore(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
        this.network = FluxNetworkCache.instance.getClientNetwork(connector.getNetworkID());
        this.networkValid = !network.isInvalid();
    }

    @Override
    public void initGui(){
        super.initGui();
        buttonLists.clear();
        buttons.clear();
        switches.clear();
        textBoxes.clear();

        buttonLists.add(buttons);
        buttonLists.add(switches);
    }

    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        textBoxes.forEach(TextboxButton::drawTextBox);
        buttonLists.forEach(list -> list.forEach(b -> b.drawButton(mc, mouseX, mouseY, guiLeft, guiTop)));
    }

    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
        buttonLists.forEach(list -> list.forEach(b -> b.updateButton(partialTicks, mouseX, mouseY)));
    }

    @Override
    public void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        CYCLE: for(List<? extends GuiButtonCore> list : buttonLists){
           for(GuiButtonCore button : list){
                if(button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    onButtonClicked(button, mouseX - guiLeft, mouseY - guiTop, mouseButton);
                    break CYCLE;
                }
            }
        }
        textBoxes.forEach(b -> b.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton));
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){}


    protected void keyTypedMain(char c, int k) throws IOException {

        for(TextboxButton text : textBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer1 == 0) {
            this.network = FluxNetworkCache.instance.getClientNetwork(connector.getNetworkID());
            this.networkValid = !network.isInvalid();
        }
        timer1++;
        timer1 %= 20;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, false);
        FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, true);
    }

    @Override
    protected void drawFluxDefaultBackground() {
        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        float f = (float)(network.getSetting(NetworkSettings.NETWORK_COLOR) >> 16 & 255) / 255.0F;
        float f1 = (float)(network.getSetting(NetworkSettings.NETWORK_COLOR) >> 8 & 255) / 255.0F;
        float f2 = (float)(network.getSetting(NetworkSettings.NETWORK_COLOR) & 255) / 255.0F;
        GlStateManager.color(f, f1, f2);
        mc.getTextureManager().bindTexture(FRAME);
        this.drawTexturedModalRect(width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        GlStateManager.popMatrix();
    }

    protected void renderTransfer(IFluxConnector fluxConnector, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);

        fontRenderer.drawString(FluxUtils.getTransferInfo(fluxConnector.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), fluxConnector.getTransferHandler().getChange()), x, y, color);
        fontRenderer.drawString((fluxConnector.getConnectionType().isStorage() ? FluxTranslate.ENERGY.t() : FluxTranslate.BUFFER.t()) +
                ": " + TextFormatting.BLUE + FluxUtils.format(fluxConnector.getTransferHandler().getEnergyStored(), FluxUtils.TypeNumberFormat.COMMAS,
                network.getSetting(NetworkSettings.NETWORK_ENERGY), false), x, y + 10, 0xffffff);

        renderItemStack(fluxConnector.getDisplayStack(), x - 20, y + 1);

        GlStateManager.popMatrix();
    }


    protected List<String> getFluxInfo(IFluxConnector flux) {
        List<String> list = Lists.newArrayList();
        list.add(TextFormatting.BOLD + flux.getCustomName());
        NBTTagCompound tag = flux.getDisplayStack().getSubCompound(FluxUtils.FLUX_DATA);
        if(flux.isChunkLoaded()) {
            if(flux.isForcedLoading()) {
                list.add(TextFormatting.AQUA + FluxTranslate.FORCED_LOADING.t());
            }
            list.add(FluxUtils.getTransferInfo(flux.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), flux.getChange()));
            if(flux.getConnectionType() == ConnectionType.STORAGE) {
                list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            } else {
                list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            }
        } else {
            list.add(TextFormatting.RED + FluxTranslate.CHUNK_UNLOADED.t());
            if(tag != null) {
                if (tag.hasKey("energy")) {
                    list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getInteger("energy")) + "RF");
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
            PacketHandler.network.sendToServer(new PacketTile.TileMessage(PacketTileType.SET_NETWORK, PacketTileHandler.getSetNetworkPacket(networkID, password), ((IFluxConnector)connector).getCoords().getPos(), ((IFluxConnector)connector).getCoords().getDimension()));
        }
        if(connector instanceof ItemAdminConfigurator.AdminConnector){
            FluxNetworks.proxy.admin_viewing_network_id = networkID;
            FluxNetworks.proxy.admin_viewing_network = FluxNetworkCache.instance.getClientNetwork(networkID);
        }
        if(connector instanceof ItemConfigurator.NetworkConnector){
            PacketHandler.network.sendToServer(new PacketSetConfiguratorNetwork.SetConfiguratorNetworkMessage(networkID, password));
        }
    }
}
