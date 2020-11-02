package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabMembers;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.network.CEditMemberMessage;

import javax.annotation.Nonnull;

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
        boolean editPermission = host.accessLevel.canEdit();
        boolean ownerPermission = host.accessLevel.canDelete();
        if (host.selectedPlayer.getAccessLevel() != AccessLevel.OWNER && editPermission) {
            String text;
            int length;
            int i = 0;
            if (host.selectedPlayer.getAccessLevel() == AccessLevel.BLOCKED || host.selectedPlayer.getAccessLevel() == AccessLevel.SUPER_ADMIN) {
                text = FluxTranslate.SET_USER.t();
                length = Math.max(64, font.getStringWidth(text) + 4);
                popButtons.add(new NormalButton(text, 88 - length / 2, 76, length, 12, FluxConstants.TYPE_NEW_MEMBER));
                ++i;
                if (host.selectedPlayer.getAccessLevel() == AccessLevel.SUPER_ADMIN && ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, font.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            } else {
                if (ownerPermission) {
                    if (host.selectedPlayer.getAccessLevel() == AccessLevel.USER) {
                        text = FluxTranslate.SET_ADMIN.t();
                        length = Math.max(64, font.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76, length, 12, FluxConstants.TYPE_SET_ADMIN));
                    } else if (host.selectedPlayer.getAccessLevel() == AccessLevel.ADMIN) {
                        text = FluxTranslate.SET_USER.t();
                        length = Math.max(64, font.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76, length, 12, FluxConstants.TYPE_SET_USER));
                    }
                    ++i;
                }
                if (!host.selectedPlayer.getAccessLevel().canEdit() || ownerPermission) {
                    text = FluxTranslate.CANCEL_MEMBERSHIP.t();
                    length = Math.max(64, font.getStringWidth(text) + 4);
                    popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12,
                            FluxConstants.TYPE_CANCEL_MEMBERSHIP).setTextColor(0xffff5555));
                }
                if (ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, font.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i, length, 12,
                            FluxConstants.TYPE_TRANSFER_OWNERSHIP).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            }
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        //screenUtils.drawRectWithBackground(20, 34, 100, 138, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getText(), 88, 162, 0xffffff);
        drawCenteredString(matrixStack, font, TextFormatting.AQUA + host.selectedPlayer.getCachedName(), 88, 38, 0xffffff);
        drawCenteredString(matrixStack, font, host.selectedPlayer.getAccessLevel().getName(), 88, 48, 0xffffff);
        String text = host.selectedPlayer.getPlayerUUID().toString();
        GlStateManager.scaled(0.625, 0.625, 0.625);
        drawCenteredString(matrixStack, font, "UUID: " + text.substring(0, 16), (int) (88 * 1.6), (int) (60 * 1.6), 0xffffff);
        drawCenteredString(matrixStack, font, text.substring(16), (int) (88 * 1.6), (int) (66 * 1.6), 0xffffff);
        GlStateManager.scaled(1.6, 1.6, 1.6);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (NormalButton button : popButtons) {
            if (button.clickable && button.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                NetworkHandler.INSTANCE.sendToServer(new CEditMemberMessage(host.network.getNetworkID(), host.selectedPlayer.getPlayerUUID(), button.id));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (transferOwnership != null) {
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
