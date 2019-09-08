package fluxnetworks.client.gui;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.gui.basic.GuiFluxCore;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.connection.ConnectionTransferHandler;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.*;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

/**
 * The home page.
 */
public class GuiFluxHome extends GuiFluxCore {

    public TextboxButton fluxName, priority, limit;

    public SlidedSwitchButton chunkLoad;

    public GuiFluxHome(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        renderTransfer(tileEntity.getTransferHandler(), 0xffffff, 30, 90);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().info, 89, 150, 0xffffff);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();
        fluxName = TextboxButton.create("Name: ", 0, fontRenderer, 16, 28, 144, 12);
        fluxName.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());

        priority = TextboxButton.create("Priority: ", 1, fontRenderer, 16, 45, 144, 12).setDigitalOnly();
        priority.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        priority.setMaxStringLength(6);
        priority.setText(String.valueOf(tileEntity.priority));

        limit = TextboxButton.create("Transfer Limit: ", 2, fontRenderer, 16, 62, 144, 12).setDigitalOnly();
        limit.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(tileEntity.limit));

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(0).setMain();

        if(tileEntity.getConnectionType() != IFluxConnector.ConnectionType.STORAGE) {
            switches.add(new SlidedSwitchButton(80, 120, 1, guiLeft, guiTop, "Surge Mode: ", tileEntity.surgeMode));
            switches.add(new SlidedSwitchButton(100, 132, 2, guiLeft, guiTop, "Enable Limit: ", !tileEntity.disableLimit));
            chunkLoad = new SlidedSwitchButton(120, 144, 3, guiLeft, guiTop, "Chunk Loading: ", tileEntity.chunkLoading);
            switches.add(chunkLoad);
        }
        if(tileEntity.getConnectionType().isController() && !network.isInvalid()) {
            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, "Wireless Charging: ", network.getSetting(NetworkSettings.NETWORK_WIRELESS)));
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
            tileEntity.priority = priority.getIntegerFromText();
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 2));
        } else if(text == limit) {
            if(!(tileEntity.getConnectionType() == IFluxConnector.ConnectionType.STORAGE)) {
                tileEntity.limit = limit.getIntegerFromText();
            } else {
                // Flux Storage can't change limit
                limit.setText(String.valueOf(tileEntity.getCurrentLimit()));
                tileEntity.limit = tileEntity.getCurrentLimit();
            }
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 3));
        }
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(SlidedSwitchButton s : switches) {
                if(s.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    s.switchButton();
                    switch (s.id) {
                        case 1:
                            tileEntity.surgeMode = s.slideControl;
                            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 4));
                            break;
                        case 2:
                            tileEntity.disableLimit = !s.slideControl;
                            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 5));
                            break;
                        case 3:
                            PacketHandler.network.sendToServer(new PacketTile.TileMessage(PacketTileType.CHUNK_LOADING, PacketTileHandler.getChunkLoadPacket(s.slideControl), tileEntity.getPos(), tileEntity.getWorld().provider.getDimension()));
                            break;
                        case 4:
                            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_WIRELESS, PacketGeneralHandler.getChangeWirelessPacket(network.getNetworkID(), s.slideControl)));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(chunkLoad != null) {
            chunkLoad.slideControl = tileEntity.chunkLoading;
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        super.keyTypedMain(c, k);
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(!textBoxes.stream().anyMatch(GuiTextField::isFocused)) {
                mc.player.closeScreen();
            }
        }
    }
}
