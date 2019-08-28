package fluxnetworks.client.gui.tab;

import fluxnetworks.client.gui.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTabMembers extends GuiTabPages<NetworkMember> {

    private int timer;

    public GuiTabMembers(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 22;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 12;
        elementWidth = 143;
    }

    @Override
    public void initGui() {

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(5).setMain();

        super.initGui();
    }

    @Override
    protected void onElementClicked(NetworkMember element, int mouseButton) {

    }

    @Override
    public void renderElement(NetworkMember element, int x, int y) {
        drawRect(x - 1, y - 1, x + elementWidth + 1, y, element.getPermission().getColor() | 0xff000000);
        drawRect(x - 1, y + elementHeight, x + elementWidth + 1, y + elementHeight + 1, element.getPermission().getColor() | 0xff000000);
        drawRect(x - 1, y, x, y + elementHeight, element.getPermission().getColor() | 0xff000000);
        drawRect(x + elementWidth, y, x + elementWidth + 1, y + elementHeight, element.getPermission().getColor() | 0xff000000);

        fontRenderer.drawString(element.getCachedName(), x + 3, y + 1, 0xffffff);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer == 0) {
            refreshPages(network.getSetting(NetworkSettings.NETWORK_PLAYERS));
        }
        timer++;
        timer %= 10;
    }

}
