package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.popup.PopupMemberEdit;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import java.util.*;

public class GuiTabMembers extends GuiTabPages<NetworkMember> {

    public NetworkMember mSelectedMember;

    //private int timer;

    public GuiTabMembers(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mGridHeight = 13;
        mGridPerPage = 9;
        mElementWidth = 146;
        mElementHeight = 12;
        if (getNetwork().isValid()) {
            ClientMessages.updateNetwork(getToken(), getNetwork(), FluxConstants.NBT_NET_MEMBERS);
        }
    }

    @Nonnull
    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_MEMBER;
    }

    @Override
    protected void drawBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (getNetwork().isValid()) {
            String access = getAccessLevel().getFormattedName();
            font.draw(poseStack, access, leftPos + 158 - font.width(access), topPos + 24, 0xffffff);
            String sortBy = FluxTranslate.SORT_BY.get() + ": " + ChatFormatting.AQUA + mSortType.getTranslatedName();
            font.draw(poseStack, sortBy, leftPos + 19, topPos + 24, 0xffffff);

            renderNetwork(poseStack, getNetwork().getNetworkName(), getNetwork().getNetworkColor(),
                    leftPos + 20, topPos + 8);
        } else {
            renderNavigationPrompt(poseStack, FluxTranslate.ERROR_NO_SELECTED.get(), FluxTranslate.TAB_SELECTION.get());
        }
    }

    @Override
    public void init() {
        super.init();
        mGridStartX = leftPos + 15;
        mGridStartY = topPos + 36;
        refreshPages(getNetwork().getAllMembers());
    }

    @Override
    protected void onElementClicked(NetworkMember element, int mouseButton) {
        /*if(mouseButton == 0) {
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_PERMISSION,
            PacketGeneralHandler.getChangePermissionPacket(network.getNetworkID(), element.getPlayerUUID())));
        } else if(mouseButton == 1) {
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.REMOVE_MEMBER,
            PacketGeneralHandler.getRemoveMemberPacket(network.getNetworkID(), element.getPlayerUUID())));
        }*/
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            mSelectedMember = element;
            openPopup(new PopupMemberEdit(this));
        }
    }

    @Override
    public void renderElement(PoseStack poseStack, NetworkMember element, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int color = element.getAccessLevel().getColor();

        float r = FluxUtils.getRed(color);
        float g = FluxUtils.getGreen(color);
        float b = FluxUtils.getBlue(color);

        RenderSystem.setShaderColor(r, g, b, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_BAR);

        blit(poseStack, x, y, 0, 16, mElementWidth, mElementHeight);

        if (element.getPlayerUUID().equals(mPlayer.getUUID())) {
            fill(poseStack, x - 2, y, x - 1, y + mElementHeight, 0xFFFFFFFF);
            fill(poseStack, x + mElementWidth + 1, y, x + mElementWidth + 2, y + mElementHeight, 0xFFFFFFFF);
        }

        font.draw(poseStack, ChatFormatting.WHITE + element.getCachedName(), x + 4, y + 2, 0xffffff);

        String access = element.getAccessLevel().getFormattedName();
        font.draw(poseStack, access, x + mElementWidth - 4 - font.width(access), y + 2, 0xffffff);
    }

    @Override
    public void renderElementTooltip(PoseStack poseStack, NetworkMember element, int mouseX, int mouseY) {
        List<Component> components = new ArrayList<>();
        components.add(FluxTranslate.USERNAME.makeComponent().append(": " + ChatFormatting.AQUA + element.getCachedName()));
        components.add(FluxTranslate.ACCESS.makeComponent().append(": " + element.getAccessLevel().getFormattedName()));
        //components.add(TextFormatting.GRAY + "UUID: " + TextFormatting.RESET + element.getPlayerUUID().toString());

        /*if(element.getPlayerUUID().equals(player.getUniqueID())) {
            components.add(TextFormatting.WHITE + "You");
        }*/

        renderComponentTooltip(poseStack, components, mouseX, mouseY);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (mouseX >= leftPos + 45 && mouseX < leftPos + 75 && mouseY >= topPos + 24 && mouseY < topPos + 32) {
                mSortType = FluxUtils.cycle(mSortType, SortType.values());
                sortGrids(mSortType);
                return true;
            }
            if (!getNetwork().isValid()) {
                if (mouseX >= leftPos + 20 && mouseX < leftPos + 155 && mouseY >= topPos + 16 && mouseY < topPos + 36) {
                    switchTab(EnumNavigationTab.TAB_SELECTION);
                    return true;
                }
            }
        }
        /*for(NormalButton button : buttons) {
            if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                if(button.id == 1 && !player.getText().isEmpty()) {
                    PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.ADD_MEMBER,
                     PacketGeneralHandler.getAddMemberPacket(network.getNetworkID(), player.getText())));
                    player.setText("");
                }
            }
        }*/
        return false;
    }

    @Override
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (code == FluxConstants.RESPONSE_REJECT) {
            switchTab(EnumNavigationTab.TAB_HOME);
            return;
        }
        if (key == FluxConstants.REQUEST_UPDATE_NETWORK) {
            refreshPages(getNetwork().getAllMembers());
        }
    }

    /*@Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (info == FeedbackInfo.SUCCESS) {
            if (hasActivePopup()) {
                // re-open
                Optional<NetworkMember> n =
                        elements.stream().filter(f -> f.getPlayerUUID().equals(selectedPlayer.getPlayerUUID()))
                        .findFirst();
                if (n.isPresent()) {
                    selectedPlayer = n.get();
                    openPopUp(new PopupMemberEdit(this, player));
                } else {
                    closePopUp();
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            C2SNetMsg.requestAccessUpdate(network.getNetworkID());
        }
        if (timer == 4 || timer == 14) {
            refreshPages(Lists.newArrayList(network.getAllMembers()));
        }
        timer++;
        timer %= 40;
    }*/

    @Override
    protected void sortGrids(SortType sortType) {
        switch (sortType) {
            case ID -> {
                mElements.sort(Comparator.comparing(NetworkMember::getAccessLevel).thenComparing(NetworkMember::getPlayerUUID));
                refreshCurrentPageInternal();
            }
            case NAME -> {
                mElements.sort(Comparator.comparing(NetworkMember::getAccessLevel).thenComparing(NetworkMember::getCachedName));
                refreshCurrentPageInternal();
            }
        }
    }
}
