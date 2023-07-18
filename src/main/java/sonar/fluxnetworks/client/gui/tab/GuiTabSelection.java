package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.EditButton;
import sonar.fluxnetworks.client.gui.popup.PopupNetworkPassword;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiTabSelection extends GuiTabPages<FluxNetwork> {

    private EditButton mDisconnect;
    public FluxNetwork mSelectedNetwork;

    public GuiTabSelection(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mGridHeight = 13;
        mGridPerPage = 9;
        mElementWidth = 146;
        mElementHeight = 12;
    }

    @Override
    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_SELECTION;
    }

    @Override
    protected void drawBackgroundLayer(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        super.drawBackgroundLayer(gr, mouseX, mouseY, deltaTicks);
        if (mElements.isEmpty()) {
            renderNavigationPrompt(gr, FluxTranslate.ERROR_NO_NETWORK, EnumNavigationTab.TAB_CREATE);
        } else {
            String total = FluxTranslate.TOTAL.get() + ": " + mElements.size();
            gr.drawString(font, total, leftPos + 158 - font.width(total), topPos + 24, 0xffffff);
            String sortBy = FluxTranslate.SORT_BY.get() + ": " + ChatFormatting.AQUA + mSortType.getTranslatedName();
            gr.drawString(font, sortBy, leftPos + 19, topPos + 24, 0xffffff);

            renderNetwork(gr, getNetwork().getNetworkName(), getNetwork().getNetworkColor(), topPos + 8);
        }
    }

    @Override
    public void init() {
        super.init();
        mGridStartX = leftPos + 15;
        mGridStartY = topPos + 36;

        refreshPages(ClientCache.getAllNetworks());

        if (!mElements.isEmpty()) {
            mDisconnect = new EditButton(this, leftPos + 142, topPos + 10, 8, 8, 0, 0,
                    FluxTranslate.BATCH_DISCONNECT_BUTTON.get(), FluxTranslate.BATCH_DISCONNECT_BUTTON.get());
            mDisconnect.setClickable(getNetwork().isValid());
            mButtons.add(mDisconnect);
        }
    }

    @Override
    public void renderElement(GuiGraphics gr, FluxNetwork element, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, ICON);

        boolean selected = getNetwork() == element;
        boolean locked = element.getSecurityLevel() != SecurityLevel.PUBLIC;

        if (locked) {
            if (selected) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
            }
            blitF(gr, x + 133, y + 1, 10, 10, 384, 256, 64, 64);
        }

        int color = element.getNetworkColor();

        float r = FluxUtils.getRed(color);
        float g = FluxUtils.getGreen(color);
        float b = FluxUtils.getBlue(color);

        RenderSystem.setShaderColor(r, g, b, 1.0f);
        if (selected) {
            gr.fill(x - 2, y, x - 1, y + mElementHeight, 0xFFFFFFFF);
            gr.fill(x + mElementWidth + 1, y, x + mElementWidth + 2, y + mElementHeight, 0xFFFFFFFF);
        }

        renderBarAndName(gr, element, x, y, selected);
    }

    protected void renderBarAndName(GuiGraphics gr, FluxNetwork element, int x, int y, boolean selected) {
        blitF(gr, x, y, mElementWidth, mElementHeight, 0, 352, mElementWidth * 2, mElementHeight * 2);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        gr.drawString(font, element.getNetworkName(), x + 4, y + 2, selected ? 0xffffff : 0x606060);
    }

    @Override
    public void renderElementTooltip(GuiGraphics gr, FluxNetwork element, int mouseX, int mouseY) {
        gr.renderComponentTooltip(font, getElementTooltips(element), mouseX, mouseY);
    }

    protected List<Component> getElementTooltips(@Nonnull FluxNetwork element) {
        List<Component> components = new ArrayList<>();
        components.add(Component.literal("ID: " + element.getNetworkID()));
        components.add(FluxTranslate.NETWORK_NAME.makeComponent().append(": " +
                ChatFormatting.AQUA + element.getNetworkName()));
        components.add(FluxTranslate.NETWORK_SECURITY.makeComponent().append(": " +
                ChatFormatting.GOLD + element.getSecurityLevel().getName()));
        AccessLevel access = element.getPlayerAccess(mPlayer);
        if (access != AccessLevel.BLOCKED) {
            components.add(FluxTranslate.ACCESS.makeComponent().append(": " + access.getFormattedName()));
        }
        if (ClientCache.sWirelessNetwork == element.getNetworkID()) {
            components.add(FluxTranslate.EFFECTIVE_WIRELESS_NETWORK.makeComponent()
                    .withStyle(ChatFormatting.YELLOW));
        }
        return components;
    }

    @Override
    protected void onElementClicked(FluxNetwork element, int mouseButton) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            mSelectedNetwork = element;
            setConnectedNetwork(element, ClientCache.getRecentPassword(element.getNetworkID()));
        }
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
                refreshCurrentPage();
                return true;
            }
            if (mElements.isEmpty()) {
                return redirectNavigationPrompt(mouseX, mouseY, mouseButton, EnumNavigationTab.TAB_CREATE);
            }
        }
        return false;
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && button == mDisconnect) {
            setConnectedNetwork(FluxNetwork.INVALID, "");
        }
    }

    @Override
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (code == FluxConstants.RESPONSE_REJECT) {
            switchTab(EnumNavigationTab.TAB_HOME, false);
            return;
        }
        if (key == FluxConstants.REQUEST_TILE_NETWORK) {
            if (code == FluxConstants.RESPONSE_REQUIRE_PASSWORD) {
                openPopup(new PopupNetworkPassword(this));
            } else if (code == FluxConstants.RESPONSE_SUCCESS) {
                if (mSelectedNetwork != null) {
                    if (getCurrentPopup() instanceof PopupNetworkPassword p) {
                        ClientCache.updateRecentPassword(mSelectedNetwork.getNetworkID(), p.mPassword.getValue());
                    }
                    if (menu.mProvider instanceof ItemFluxConfigurator.Provider p) {
                        CompoundTag tag = p.mStack.getOrCreateTagElement(FluxConstants.TAG_FLUX_DATA);
                        tag.putInt(FluxConstants.NETWORK_ID, mSelectedNetwork.getNetworkID());
                    }
                }
                closePopup();
                mSelectedNetwork = null;
            }
        } else if (key == FluxConstants.REQUEST_UPDATE_NETWORK) {
            refreshPages(ClientCache.getAllNetworks());
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (mDisconnect != null) {
            mDisconnect.setClickable(getNetwork().isValid());
        }
    }

    @Override
    protected void sortGrids(SortType sortType) {
        switch (sortType) {
            case ID -> mElements.sort(Comparator.comparing(FluxNetwork::getNetworkID));
            case NAME -> mElements.sort(Comparator.comparing(FluxNetwork::getNetworkName));
        }
    }
}
