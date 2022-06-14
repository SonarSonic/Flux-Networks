package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.popup.PopupNetworkPassword;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class GuiTabSelection extends GuiTabPages<FluxNetwork> {

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
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (mElements.isEmpty()) {
            renderNavigationPrompt(poseStack, FluxTranslate.ERROR_NO_NETWORK.get(), FluxTranslate.TAB_CREATE.get());
        } else {
            String total = FluxTranslate.TOTAL.get() + ": " + mElements.size();
            font.draw(poseStack, total, leftPos + 158 - font.width(total), topPos + 24, 0xffffff);
            String sortBy = FluxTranslate.SORT_BY.get() + ": " + ChatFormatting.AQUA + mSortType.getTranslatedName();
            font.draw(poseStack, sortBy, leftPos + 19, topPos + 24, 0xffffff);

            renderNetwork(poseStack, getNetwork().getNetworkName(), getNetwork().getNetworkColor(),
                    leftPos + 20, topPos + 8);
        }
    }

    @Override
    public void init() {
        super.init();
        mGridStartX = leftPos + 15;
        mGridStartY = topPos + 36;
        refreshPages(ClientCache.getAllNetworks());
    }

    @Override
    public void renderElement(PoseStack poseStack, FluxNetwork element, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_BAR);

        boolean selected = getNetwork() == element;
        boolean locked = element.getSecurityLevel() != SecurityLevel.PUBLIC;

        if (locked) {
            if (selected) {
                blit(poseStack, x + 131, y, 159, 16, 16, mElementHeight);
            } else {
                blit(poseStack, x + 131, y, 175, 16, 16, mElementHeight);
            }
        }

        if (selected) {
            fill(poseStack, x - 2, y, x - 1, y + mElementHeight, 0xFFFFFFFF);
            fill(poseStack, x + mElementWidth + 1, y, x + mElementWidth + 2, y + mElementHeight, 0xFFFFFFFF);
        }

        int color = element.getNetworkColor();

        float r = FluxUtils.getRed(color);
        float g = FluxUtils.getGreen(color);
        float b = FluxUtils.getBlue(color);

        RenderSystem.setShaderColor(r, g, b, 1.0f);
        blit(poseStack, x, y, 0, 16, mElementWidth, mElementHeight);
        font.draw(poseStack, element.getNetworkName(), x + 4, y + 2, selected ? 0xffffff : 0x606060);
    }

    @Override
    public void renderElementTooltip(PoseStack poseStack, FluxNetwork element, int mouseX, int mouseY) {
        //TODO
    }

    @Override
    protected void onElementClicked(FluxNetwork element, int mouseButton) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            mSelectedNetwork = element;
            setConnectedNetwork(element.getNetworkID(), ClientCache.getRecentPassword(element.getNetworkID()));
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
                return true;
            }
            if (mElements.isEmpty()) {
                if (mouseX >= leftPos + 20 && mouseX < leftPos + 155 && mouseY >= topPos + 16 && mouseY < topPos + 36) {
                    switchTab(EnumNavigationTab.TAB_CREATE);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (key == FluxConstants.REQUEST_SET_NETWORK) {
            if (code == FluxConstants.RESPONSE_REQUIRE_PASSWORD) {
                openPopup(new PopupNetworkPassword(this));
            } else if (code == FluxConstants.RESPONSE_SUCCESS) {
                if (mSelectedNetwork != null) {
                    if (getCurrentPopup() instanceof PopupNetworkPassword p) {
                        ClientCache.updateRecentPassword(mSelectedNetwork.getNetworkID(), p.mPassword.getValue());
                    }
                    if (menu.mProvider instanceof ItemFluxConfigurator.Provider p) {
                        p.mNetworkID = mSelectedNetwork.getNetworkID();
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
        if (mLabelButton != null) {
            mLabelButton.mColor = getNetwork().getNetworkColor();
        }
    }

    @Override
    protected void sortGrids(SortType sortType) {
        switch (sortType) {
            case ID -> {
                mElements.sort(Comparator.comparing(FluxNetwork::getNetworkID));
                refreshCurrentPageInternal();
            }
            case NAME -> {
                mElements.sort(Comparator.comparing(FluxNetwork::getNetworkName));
                refreshCurrentPageInternal();
            }
        }
    }
}
