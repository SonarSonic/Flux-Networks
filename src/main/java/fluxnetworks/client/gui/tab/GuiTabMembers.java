package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import fluxnetworks.common.network.PacketNetworkUpdateRequest;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiTabMembers extends GuiTabPages<NetworkMember> {

    public TextboxButton player;

    private int timer;

    public GuiTabMembers(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 18;
        gridHeight = 14;
        gridPerPage = 9;
        elementHeight = 11;
        elementWidth = 143;
        PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_PLAYERS));
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 89, 162, 0xffffff);
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void initGui() {

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(5).setMain();

        if(networkValid) {

            buttons.add(new NormalButton("+", 152, 150, 12, 12, 1));

            player = TextboxButton.create(this, "", 1, fontRenderer, 14, 150, 130, 12);
            player.setMaxStringLength(32);

            textBoxes.add(player);
        }

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
        fontRenderer.drawString(element.getCachedName(), x + 3, y + 1, 0xffffff);
        String p = element.getPermission().getName();
        fontRenderer.drawString(p, x + 140 - fontRenderer.getStringWidth(p), y + 1, 0xffffff);
    }

    @Override
    public void renderElementTooltip(NetworkMember element, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        List<String> strings = new ArrayList<>();
        strings.add(TextFormatting.AQUA + element.getCachedName());
        strings.add(TextFormatting.RESET + element.getPermission().getName());
        //strings.add(TextFormatting.GRAY + "UUID: " + TextFormatting.RESET + element.getPlayerUUID().toString());
        strings.add(TextFormatting.WHITE + "L/R Change/Remove");
        drawHoverTooltip(strings, mouseX + 4, mouseY - 8);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        for(NormalButton button : buttons) {
            if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                if(button.id == 1 && !player.getText().isEmpty()) {
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
            refreshPages(network.getSetting(NetworkSettings.NETWORK_PLAYERS));
        }
        timer++;
        timer %= 2;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(NetworkMember::getPermission).thenComparing(NetworkMember::getCachedName));
    }
}
