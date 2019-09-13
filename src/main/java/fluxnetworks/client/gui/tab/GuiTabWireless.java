package fluxnetworks.client.gui.tab;

import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTabWireless extends GuiTabCore {

    public int enableWireless, rightHand, leftHand, hotBar, armorSlot, baublesSlot;

    public GuiTabWireless(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        int a = network.getSetting(NetworkSettings.NETWORK_WIRELESS);
        enableWireless = a & 1;
        rightHand = a >> 1 & 1;
        leftHand = a >> 2 & 1;
        hotBar = a >> 3 & 1;
        armorSlot = a >> 4 & 1;
        baublesSlot = a >> 5 & 1;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(2).setMain();

    }

}
