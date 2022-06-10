package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.ClientRepository;
import sonar.fluxnetworks.client.gui.GuiTabType;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class GuiTabSelection extends GuiTabPages<FluxNetwork> {

    //public InvisibleButton redirectButton;

    public FluxNetwork selectedNetwork;

    protected int timer2;

    public GuiTabSelection(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
        mGridHeight = 13;
        mGridPerPage = 10;
        mElementWidth = 146;
        mElementHeight = 12;
    }

    @Override
    public GuiTabType getCurrentTab() {
        return GuiTabType.TAB_SELECTION;
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (mElements.isEmpty()) {
            renderNavigationPrompt(poseStack, FluxTranslate.ERROR_NO_NETWORK.get(), FluxTranslate.TAB_CREATE.get());
        } else {
            String total = FluxTranslate.TOTAL.get() + ": " + mElements.size();
            String sortBy = FluxTranslate.SORT_BY.get() + ": " + ChatFormatting.AQUA + mSortType.getTranslatedName();
            font.draw(poseStack, total, leftPos + 158 - font.width(total), topPos + 10, 0xffffff);
            font.draw(poseStack, sortBy, leftPos + 19, topPos + 10, 0xffffff);
        }
    }

    @Override
    protected void onElementClicked(FluxNetwork element, int mouseButton) {
        if (mouseButton == 0) {
            selectedNetwork = element;
            //setConnectedNetwork(element.getNetworkID(), "");
        }
    }

    @Override
    public void init() {
        super.init();
        mGridStartX = leftPos + 15;
        mGridStartY = topPos + 22;
        /*if (FluxClientCache.getAllNetworks().isEmpty()) {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_CREATE.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_CREATE));
            addButton(redirectButton);
        }*/
        refreshPages(ClientRepository.getAllNetworks());
    }

    @Override
    public void renderElement(PoseStack poseStack, FluxNetwork element, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_BAR);

        int color = element.getNetworkColor();

        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        boolean selected = menu.mProvider.getNetworkID() == element.getNetworkID();
        boolean isEncrypted = element.getSecurityLevel().isEncrypted();

        if (isEncrypted) {
            if (selected) {
                blit(poseStack, x + 131, y, 159, 16, 16, mElementHeight);
            } else {
                blit(poseStack, x + 131, y, 175, 16, 16, mElementHeight);
            }
        }

        String text = element.getNetworkName();

        if (selected) {
            RenderSystem.setShaderColor(f, f1, f2, 1.0f);
            blit(poseStack, x, y, 0, 16, mElementWidth, mElementHeight);
            font.draw(poseStack, text, x + 4, y + 2, 0xffffff);
        } else {
            RenderSystem.setShaderColor(f * 0.75f, f1 * 0.75f, f2 * 0.75f, 1.0f);
            blit(poseStack, x, y, 0, 16, mElementWidth, mElementHeight);
            font.draw(poseStack, text, x + 4, y + 2, 0x404040);
        }
    }

    @Override
    public void renderElementTooltip(PoseStack poseStack, FluxNetwork element, int mouseX, int mouseY) {
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (mouseButton == 0) {
            if (mouseX >= leftPos + 45 && mouseX < leftPos + 75 && mouseY >= topPos + 10 && mouseY < topPos + 17) {
                mSortType = FluxUtils.cycle(mSortType, SortType.values());
                sortGrids(mSortType);
                return true;
            }
        }
        return false;
    }

    /*@Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (info == FeedbackInfo.PASSWORD_REQUIRE) {
            openPopUp(new PopupNetworkPassword(this, player));
        } else if (selectedNetwork != null && info == FeedbackInfo.SUCCESS) {
            closePopUp();
            if (container.bridge instanceof ItemFluxConfigurator.MenuBridge) {
                ((ItemFluxConfigurator.MenuBridge) container.bridge).networkID = selectedNetwork.getNetworkID();
                network = selectedNetwork;
                networkValid = selectedNetwork.isValid();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer2 == 9) {
            refreshPages(FluxClientCache.getAllNetworks());
        }
        timer2++;
        timer2 %= 10;
    }*/

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
