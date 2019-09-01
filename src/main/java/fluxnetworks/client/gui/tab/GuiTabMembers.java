package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GuiTabMembers extends GuiTabPages<NetworkMember> {

    public TextboxButton player;

    private int timer;

    public GuiTabMembers(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 18;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 10;
        elementWidth = 143;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().info, 89, 162, 0xffffff);
    }

    @Override
    public void initGui() {

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(5).setMain();

        buttons.add(new NormalButton("+", 152, 150, 12, 12, 1));

        player = TextboxButton.create("", 1, fontRenderer, 14, 150, 130, 12);
        player.setMaxStringLength(32);

        textBoxes.add(player);

        super.initGui();
    }

    @Override
    protected void onElementClicked(NetworkMember element, int mouseButton) {
        if(mouseButton == 0) {
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_PERMISSION, PacketGeneralHandler.getChangePermissionPacket(network.getNetworkID(), element.getPlayerUUID())));
        } else if(mouseButton == 1) {
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.REMOVE_MEMBER, PacketGeneralHandler.getRemoveMemberPacket(network.getNetworkID(), element.getPlayerUUID())));
        }
    }

    @Override
    public void renderElement(NetworkMember element, int x, int y) {
        drawColorRect(x, y, elementHeight, elementWidth, element.getPermission().color | 0xcc000000);
        fontRenderer.drawString(element.getCachedName(), x + 3, y, 0xffffff);
        String p = element.getPermission().name;
        fontRenderer.drawString(p, x + 140 - fontRenderer.getStringWidth(p), y, element.getPermission().color);
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        for(NormalButton button : buttons) {
            if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                if(button.id == 1) {
                    PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.ADD_MEMBER, PacketGeneralHandler.getAddMemberPacket(network.getNetworkID(), player.getText())));
                    player.setText("");
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer == 0) {
            List<NetworkMember> a = network.getSetting(NetworkSettings.NETWORK_PLAYERS);
            a.sort(Comparator.comparing(NetworkMember::getPermission));
            refreshPages(a);
        }
        timer++;
        timer %= 10;
    }

}
