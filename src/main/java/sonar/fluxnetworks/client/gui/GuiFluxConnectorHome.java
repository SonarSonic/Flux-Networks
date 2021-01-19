package sonar.fluxnetworks.client.gui;

import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.client.gui.button.TextboxButton;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.*;
import sonar.fluxnetworks.common.network.*;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

/**
 * The home page.
 */
public class GuiFluxConnectorHome extends GuiTabCore {

    public TextboxButton fluxName, priority, limit;

    public SlidedSwitchButton surge, disableLimit, chunkLoad;

    private TileFluxCore tileEntity;
    private int timer;

    public GuiFluxConnectorHome(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        this.tileEntity = tileEntity;
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        renderTransfer(tileEntity, 0xffffff, 30, 90);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        fontRenderer.drawString(FluxTranslate.SURGE_MODE.t(), 20, 120, network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(FluxTranslate.DISABLE_LIMIT.t(), 20, 132, network.getSetting(NetworkSettings.NETWORK_COLOR));
        if(!tileEntity.getConnectionType().isStorage()) {
            fontRenderer.drawString(FluxTranslate.CHUNK_LOADING.t(), 20, 144, network.getSetting(NetworkSettings.NETWORK_COLOR));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        int color = network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
        fluxName = TextboxButton.create(this, FluxTranslate.NAME.t() + ": ", 0, fontRenderer, 16, 28, 144, 12).setOutlineColor(color);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());

        priority = TextboxButton.create(this, FluxTranslate.PRIORITY.t() + ": ", 1, fontRenderer, 16, 45, 144, 12).setOutlineColor(color).setDigitsOnly();
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(tileEntity.priority));

        limit = TextboxButton.create(this, FluxTranslate.TRANSFER_LIMIT.t() + ": ", 2, fontRenderer, 16, 62, 144, 12).setOutlineColor(color).setDigitsOnly();
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(tileEntity.limit));

        surge = new SlidedSwitchButton(140, 120, 1, guiLeft, guiTop, tileEntity.surgeMode);
        disableLimit = new SlidedSwitchButton(140, 132, 2, guiLeft, guiTop, tileEntity.disableLimit);
        switches.add(surge);
        switches.add(disableLimit);

        if(!tileEntity.getConnectionType().isStorage()) {
            chunkLoad = new SlidedSwitchButton(140, 144, 3, guiLeft, guiTop, tileEntity.chunkLoading);
            switches.add(chunkLoad);
        }

        textBoxes.add(fluxName);
        textBoxes.add(priority);
        textBoxes.add(limit);
    }

    @Override
    public void onTextBoxChanged(TextboxButton text) {
        if(text == fluxName) {
            tileEntity.customName = fluxName.getText();
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 1));
        } else if(text == priority) {
            tileEntity.priority = priority.getIntegerFromText(false);
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 2));
        } else if(text == limit) {
            tileEntity.limit = Math.min(limit.getLongFromText(true), tileEntity.getMaxTransferLimit());
            limit.setText(String.valueOf(tileEntity.limit));
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 3));
        }
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(mouseButton == 0 && button instanceof SlidedSwitchButton){
            SlidedSwitchButton switchButton = (SlidedSwitchButton)button;
            switchButton.switchButton();
            switch (switchButton.id) {
                case 1:
                    tileEntity.surgeMode = switchButton.slideControl;
                    PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 4));
                    break;
                case 2:
                    tileEntity.disableLimit = switchButton.slideControl;
                    PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 5));
                    break;
                case 3:
                    PacketHandler.network.sendToServer(new PacketTile.TileMessage(PacketTileType.CHUNK_LOADING, PacketTileHandler.getChunkLoadPacket(switchButton.slideControl), tileEntity.getPos(), tileEntity.getWorld().provider.getDimension()));
                    break;
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer == 0) {
            PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_GENERAL));
        }
        if(timer % 4 == 0) {
            if (chunkLoad != null) {
                chunkLoad.slideControl = tileEntity.chunkLoading;
            }
        }
        timer++;
        timer %= 100;
    }

}
