package sonar.fluxnetworks.client.gui.popups;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.api.network.AccessType;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabMembers;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.GeneralPacket;
import sonar.fluxnetworks.common.network.GeneralPacketHandler;
import sonar.fluxnetworks.common.network.GeneralPacketEnum;
import net.minecraft.util.text.TextFormatting;

public class PopUpUserEdit extends PopUpCore<GuiTabMembers> {

    public NormalButton transferOwnership;
    public int transferOwnershipCount;

    public PopUpUserEdit(GuiTabMembers host, PlayerEntity player, INetworkConnector connector) {
        super(host, player, connector);
    }

    @Override
    public void init() {
        super.init();
        popButtons.clear();
        boolean editPermission = host.accessPermission.canEdit();
        boolean ownerPermission = host.accessPermission.canDelete();
        if(host.selectedPlayer.getPlayerAccess() != AccessType.OWNER && editPermission) {
            String text;
            int length;
            int i = 0;
            if (host.selectedPlayer.getPlayerAccess() == AccessType.BLOCKED || host.selectedPlayer.getPlayerAccess() == AccessType.SUPER_ADMIN) {
                text = FluxTranslate.SET_USER.t();
                length = Math.max(64, font.getStringWidth(text) + 4);
                popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 0));
                if(host.selectedPlayer.getPlayerAccess() == AccessType.SUPER_ADMIN && ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, font.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            } else {
                if(ownerPermission) {
                    if (host.selectedPlayer.getPlayerAccess() == AccessType.USER) {
                        text = FluxTranslate.SET_ADMIN.t();
                        length = Math.max(64, font.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 1));
                    } else if(host.selectedPlayer.getPlayerAccess() == AccessType.ADMIN) {
                        text = FluxTranslate.SET_USER.t();
                        length = Math.max(64, font.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 2));
                    }
                }
                if(!host.selectedPlayer.getPlayerAccess().canEdit() || ownerPermission) {
                    text = FluxTranslate.CANCEL_MEMBERSHIP.t();
                    length = Math.max(64, font.getStringWidth(text) + 4);
                    popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 3).setTextColor(0xffff5555));
                }
                if(ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, font.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            }
        }
    }
    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        //screenUtils.drawRectWithBackground(20, 34, 100, 138, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxNetworks.PROXY.getFeedback(false).getInfo(), 88, 162, 0xffffff);
        drawCenteredString(matrixStack, font, TextFormatting.AQUA + host.selectedPlayer.getCachedName(), 88, 38, 0xffffff);
        drawCenteredString(matrixStack, font, host.selectedPlayer.getPlayerAccess().getName(), 88, 48, 0xffffff);
        String text = host.selectedPlayer.getPlayerUUID().toString();
        GlStateManager.scaled(0.625, 0.625, 0.625);
        drawCenteredString(matrixStack, font, "UUID: " + text.substring(0, 16), (int) (88 * 1.6), (int) (60 * 1.6), 0xffffff);
        drawCenteredString(matrixStack, font, text.substring(16), (int) (88 * 1.6), (int) (66 * 1.6), 0xffffff);
        GlStateManager.scaled(1.6, 1.6, 1.6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(NormalButton button : popButtons) {
            if(button.clickable && button.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
                PacketHandler.CHANNEL.sendToServer(new GeneralPacket(GeneralPacketEnum.CHANGE_PERMISSION, GeneralPacketHandler.getChangePermissionPacket(host.network.getNetworkID(), host.selectedPlayer.getPlayerUUID(), button.id)));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(transferOwnership != null) {
            if (scanCode == 42) {
                transferOwnershipCount++;
                if (transferOwnershipCount > 1) {
                    transferOwnership.clickable = true;
                }
            } else {
                transferOwnershipCount = 0;
                transferOwnership.clickable = false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
