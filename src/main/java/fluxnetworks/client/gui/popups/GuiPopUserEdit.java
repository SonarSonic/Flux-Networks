package fluxnetworks.client.gui.popups;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.tab.GuiTabMembers;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class GuiPopUserEdit extends GuiPopCore<GuiTabMembers> {

    public NormalButton transferOwnership;
    public int transferOwnershipCount;

    public GuiPopUserEdit(GuiTabMembers host, EntityPlayer player, INetworkConnector connector) {
        super(host, player, connector);
    }

    @Override
    public void initGui() {
        super.initGui();
        popButtons.clear();
        boolean editPermission = host.accessPermission.canEdit();
        boolean ownerPermission = host.accessPermission.canDelete();
        if(host.selectedPlayer.getAccessPermission() != AccessPermission.OWNER && editPermission) {
            String text;
            int length;
            int i = 0;
            if (host.selectedPlayer.getAccessPermission() == AccessPermission.NONE || host.selectedPlayer.getAccessPermission() == AccessPermission.SUPER_ADMIN) {
                text = FluxTranslate.SET_USER.t();
                length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 0));
                if(host.selectedPlayer.getAccessPermission() == AccessPermission.SUPER_ADMIN && ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            } else {
                if(ownerPermission) {
                    if (host.selectedPlayer.getAccessPermission() == AccessPermission.USER) {
                        text = FluxTranslate.SET_ADMIN.t();
                        length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 1));
                    } else if(host.selectedPlayer.getAccessPermission() == AccessPermission.ADMIN) {
                        text = FluxTranslate.SET_USER.t();
                        length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 2));
                    }
                }
                if(!host.selectedPlayer.getAccessPermission().canEdit() || ownerPermission) {
                    text = FluxTranslate.CANCEL_MEMBERSHIP.t();
                    length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                    popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 3).setTextColor(0xffff5555));
                }
                if(ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            }
        }
    }
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(20, 34, 100, 138, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 162, 0xffffff);
        drawCenteredString(fontRenderer, TextFormatting.AQUA + host.selectedPlayer.getCachedName(), 88, 38, 0xffffff);
        drawCenteredString(fontRenderer, host.selectedPlayer.getAccessPermission().getName(), 88, 48, 0xffffff);
        String text = host.selectedPlayer.getPlayerUUID().toString();
        GlStateManager.scale(0.625, 0.625, 0.625);
        drawCenteredString(fontRenderer, "UUID: " + text.substring(0, 16), (int) (88 * 1.6), (int) (60 * 1.6), 0xffffff);
        drawCenteredString(fontRenderer, text.substring(16), (int) (88 * 1.6), (int) (66 * 1.6), 0xffffff);
        GlStateManager.scale(1.6, 1.6, 1.6);
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(NormalButton button : popButtons) {
            if(button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_PERMISSION, PacketGeneralHandler.getChangePermissionPacket(host.network.getNetworkID(), host.selectedPlayer.getPlayerUUID(), button.id)));
            }
        }
    }



    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(transferOwnership != null) {
            if (keyCode == 42) {
                transferOwnershipCount++;
                if (transferOwnershipCount > 1) {
                    transferOwnership.clickable = true;
                }
            } else {
                transferOwnershipCount = 0;
                transferOwnership.clickable = false;
            }
        }
    }
}
