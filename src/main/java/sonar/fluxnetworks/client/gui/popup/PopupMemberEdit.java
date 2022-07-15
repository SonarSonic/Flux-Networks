package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiPopupCore;
import sonar.fluxnetworks.client.gui.button.SimpleButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabMembers;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;

public class PopupMemberEdit extends GuiPopupCore<GuiTabMembers> {

    public SimpleButton mSetAsUser;
    public SimpleButton mSetAsAdmin;
    public SimpleButton mCancelMembership;
    public SimpleButton mTransferOwnership;
    public int mTransferOwnershipCount;

    public PopupMemberEdit(GuiTabMembers host) {
        super(host);
    }

    @Override
    public void init() {
        super.init();
        boolean editPermission = mHost.getAccessLevel().canEdit();
        boolean ownerPermission = mHost.getAccessLevel().canDelete();
        AccessLevel targetAccess = mHost.mSelectedMember.getAccessLevel();
        if (targetAccess != AccessLevel.OWNER && editPermission) {
            String text;
            int width;
            text = FluxTranslate.SET_USER.get();
            width = Math.max(64, font.width(text) + 4);
            mSetAsUser = new SimpleButton(this,
                    leftPos + (imageWidth - width) / 2, topPos + 78, width, 12, text);
            mSetAsUser.setClickable(targetAccess == AccessLevel.BLOCKED ||
                    targetAccess == AccessLevel.SUPER_ADMIN ||
                    (targetAccess == AccessLevel.ADMIN && ownerPermission));
            mButtons.add(mSetAsUser);

            text = FluxTranslate.SET_ADMIN.get();
            width = Math.max(64, font.width(text) + 4);
            mSetAsAdmin = new SimpleButton(this,
                    leftPos + (imageWidth - width) / 2, topPos + 78 + 16, width, 12, text);
            mSetAsAdmin.setClickable(targetAccess == AccessLevel.USER && ownerPermission);
            mButtons.add(mSetAsAdmin);

            text = FluxTranslate.CANCEL_MEMBERSHIP.get();
            width = Math.max(64, font.width(text) + 4);
            mCancelMembership = new SimpleButton(this,
                    leftPos + (imageWidth - width) / 2, topPos + 78 + 32, width, 12, text, 0xFFFF5555);
            mCancelMembership.setClickable(targetAccess == AccessLevel.USER ||
                    (targetAccess == AccessLevel.ADMIN && ownerPermission));
            mButtons.add(mCancelMembership);

            text = FluxTranslate.TRANSFER_OWNERSHIP.get();
            width = Math.max(64, font.width(text) + 4);
            mTransferOwnership = new SimpleButton(this,
                    leftPos + (imageWidth - width) / 2, topPos + 78 + 48, width, 12, text, 0xFFFF00FF);
            mTransferOwnership.setClickable(false);
            mButtons.add(mTransferOwnership);
        }
    }

    @Override
    public void drawForegroundLayer(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        drawCenteredString(poseStack, font,
                ChatFormatting.AQUA + mHost.mSelectedMember.getCachedName(),
                leftPos + (imageWidth / 2), topPos + 38, 0xffffff);
        drawCenteredString(poseStack, font, mHost.mSelectedMember.getAccessLevel().getFormattedName(),
                leftPos + (imageWidth / 2), topPos + 48, 0xffffff);

        final String uuid = mHost.mSelectedMember.getPlayerUUID().toString();
        poseStack.pushPose();
        poseStack.scale(0.75f, 0.75f, 1);
        drawCenteredString(poseStack, font, "UUID: " + uuid.substring(0, 16),
                (int) ((leftPos + (imageWidth / 2)) / 0.75f), (int) ((topPos + 60) / 0.75f), 0xffffff);
        drawCenteredString(poseStack, font, uuid.substring(16),
                (int) ((leftPos + (imageWidth / 2)) / 0.75f), (int) ((topPos + 68) / 0.75f), 0xffffff);
        poseStack.popPose();

        if (mTransferOwnership != null &&
                mTransferOwnership.isMouseHovered(mouseX, mouseY) &&
                !mTransferOwnership.isClickable() &&
                mHost.mSelectedMember.getAccessLevel() != AccessLevel.BLOCKED &&
                mHost.getAccessLevel().canDelete()) {
            drawCenteredString(poseStack, font, FluxTranslate.DOUBLE_SHIFT.get(),
                    mTransferOwnership.x + mTransferOwnership.width / 2, mTransferOwnership.y + 14, 0xffffff);
        }
    }

    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }*/

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            byte type = 0;
            boolean send = true;
            if (button == mSetAsUser) {
                type = FluxConstants.MEMBERSHIP_SET_USER;
            } else if (button == mSetAsAdmin) {
                type = FluxConstants.MEMBERSHIP_SET_ADMIN;
            } else if (button == mCancelMembership) {
                type = FluxConstants.MEMBERSHIP_CANCEL_MEMBERSHIP;
            } else if (button == mTransferOwnership) {
                type = FluxConstants.MEMBERSHIP_TRANSFER_OWNERSHIP;
            } else {
                send = false;
            }
            if (send) {
                ClientMessages.editMember(
                        mHost.getToken(), mHost.getNetwork(), mHost.mSelectedMember.getPlayerUUID(), type);
                mHost.closePopup();
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (mTransferOwnership != null) {
            if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                mTransferOwnershipCount++;
                if (mTransferOwnershipCount > 1) {
                    mTransferOwnership.setClickable(mHost.getAccessLevel().canDelete() &&
                            mHost.mSelectedMember.getAccessLevel() != AccessLevel.BLOCKED);
                }
            } else {
                mTransferOwnershipCount = 0;
                mTransferOwnership.setClickable(false);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
