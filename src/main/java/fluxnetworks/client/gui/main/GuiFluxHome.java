package fluxnetworks.client.gui.main;

import fluxnetworks.client.gui.GuiFluxCore;
import fluxnetworks.client.gui.button.GuiTextField;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.GuiTextBox;
import fluxnetworks.common.connection.ConnectionTransferHandler;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketByteBuf;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

/**
 * The home page.
 */
public class GuiFluxHome extends GuiFluxCore {

    public GuiTextBox fluxName, priority, limit;

    public GuiFluxHome(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        if(tileEntity.getTransferHandler() != null)
            renderTransfer((ConnectionTransferHandler) tileEntity.getTransferHandler(), 0xffffff, 30, 90);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();
        fluxName = GuiTextBox.create("Name: ", 0, fontRenderer, 16, 28, 144, 12);
        fluxName.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());

        priority = GuiTextBox.create("Priority: ", 1, fontRenderer, 16, 45, 144, 12).setDigitalOnly();
        priority.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        priority.setMaxStringLength(8);
        priority.setText(String.valueOf(tileEntity.getPriority()));

        limit = GuiTextBox.create("Transfer Limit: ", 2, fontRenderer, 16, 62, 144, 12).setDigitalOnly();
        limit.setOutlineColor(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(tileEntity.getCurrentLimit()));

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(0).setMain();

        textBoxes.add(fluxName);
        textBoxes.add(priority);
        textBoxes.add(limit);
    }

    @Override
    public void onTextBoxChanged(GuiTextBox text) {
        if(text == fluxName) {
            tileEntity.customName = fluxName.getText();
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 1));
        } else if(text == priority) {
            tileEntity.priority = priority.getIntegerFromText();
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 2));
        } else if(text == limit) {
            tileEntity.limit = limit.getIntegerFromText();
            PacketHandler.network.sendToServer(new PacketByteBuf.ByteBufMessage(tileEntity, tileEntity.getPos(), 3));
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
