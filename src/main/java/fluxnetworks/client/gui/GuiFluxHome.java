package fluxnetworks.client.gui;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.client.gui.basic.GuiFluxCore;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
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

    public SlidedSwitchButton surge, disableLimit, chunkLoad;

    private int timer;

    public GuiFluxHome(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        renderTransfer(tileEntity.getTransferHandler(), 0xffffff, 30, 90);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        fontRenderer.drawString(FluxTranslate.SURGE_MODE.t(), 20, 120, network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(FluxTranslate.DISABLE_LIMIT.t(), 20, 132, network.getSetting(NetworkSettings.NETWORK_COLOR));
        if(!tileEntity.getConnectionType().isStorage()) {
            fontRenderer.drawString(FluxTranslate.CHUNK_LOADING.t(), 20, 144, network.getSetting(NetworkSettings.NETWORK_COLOR));
        }
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();
        int color = network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
        fluxName = TextboxButton.create(this, FluxTranslate.NAME.t() + ": ", 0, fontRenderer, 16, 28, 144, 12).setOutlineColor(color);
        //fluxName.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());

        priority = TextboxButton.create(this, FluxTranslate.PRIORITY.t() + ": ", 1, fontRenderer, 16, 45, 144, 12).setOutlineColor(color).setDigitsOnly();
        //priority.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(tileEntity.priority));

        limit = TextboxButton.create(this, FluxTranslate.TRANSFER_LIMIT.t() + ": ", 2, fontRenderer, 16, 62, 144, 12).setOutlineColor(color).setDigitsOnly();
        //limit.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(tileEntity.limit));

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(0).setMain();

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
                            tileEntity.disableLimit = s.slideControl;
                            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 5));
                            break;
                        case 3:
                            PacketHandler.network.sendToServer(new PacketTile.TileMessage(PacketTileType.CHUNK_LOADING, PacketTileHandler.getChunkLoadPacket(s.slideControl), tileEntity.getPos(), tileEntity.getWorld().provider.getDimension()));
                            break;
                    }
                }
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

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        super.keyTypedMain(c, k);
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(textBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                mc.player.closeScreen();
            }
        }
    }

}
