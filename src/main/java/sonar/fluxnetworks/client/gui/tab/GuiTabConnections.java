package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.BatchEditButton;
import sonar.fluxnetworks.client.gui.popup.PopupConnectionEdit;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public class GuiTabConnections extends GuiTabPages<IFluxDevice> {

    //public InvisibleButton redirectButton;

    public final List<IFluxDevice> mBatchConnections = new ArrayList<>();
    public IFluxDevice mSingleConnection;

    public BatchEditButton mClear;
    public BatchEditButton mEdit;
    public BatchEditButton mDisconnect;

    private int timer = 3;

    public GuiTabConnections(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mGridHeight = 19;
        mGridPerPage = 7;
        mElementWidth = 146;
        mElementHeight = 18;
        if (getNetwork().isValid()) {
            ClientMessages.updateNetwork(getToken(), getNetwork(), FluxConstants.NBT_NET_CONNECTIONS);
        }
    }

    @Override
    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_CONNECTION;
    }

    @Override
    public void init() {
        super.init();
        mGridStartX = leftPos + 15;
        mGridStartY = topPos + 22;

        if (getNetwork().isValid()) {
            mClear = new BatchEditButton(this, leftPos + 118, topPos + 8, 0,
                    FluxTranslate.BATCH_CLEAR_BUTTON.get());
            mClear.setClickable(false);
            mEdit = new BatchEditButton(this, leftPos + 132, topPos + 8, 16,
                    FluxTranslate.BATCH_EDIT_BUTTON.get());
            mEdit.setClickable(false);
            mDisconnect = new BatchEditButton(this, leftPos + 146, topPos + 8, 32,
                    FluxTranslate.BATCH_DISCONNECT_BUTTON.get());
            mDisconnect.setClickable(false);

            mButtons.add(mClear);
            mButtons.add(mEdit);
            mButtons.add(mDisconnect);
        }
        refreshPages(getNetwork().getAllConnections());
    }

    @Override
    protected void onElementClicked(IFluxDevice element, int mouseButton) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && mBatchConnections.isEmpty() && element.isChunkLoaded()) {
            mSingleConnection = element;
            openPopup(new PopupConnectionEdit(this, false));
        } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT ||
                (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && mBatchConnections.size() > 0)) {
            if (mBatchConnections.contains(element)) {
                mBatchConnections.remove(element);
                if (mBatchConnections.isEmpty()) {
                    mClear.setClickable(false);
                    mEdit.setClickable(false);
                    mDisconnect.setClickable(false);
                }
            } else if (element.isChunkLoaded()) {
                mBatchConnections.add(element);
                mClear.setClickable(true);
                mEdit.setClickable(true);
                mDisconnect.setClickable(true);
            }
        }
    }

    @Override
    protected void drawBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (getNetwork().isValid()) {
            if (mBatchConnections.size() > 0) {
                font.draw(poseStack,
                        FluxTranslate.SELECTED.get() + ": " + ChatFormatting.AQUA + mBatchConnections.size(),
                        leftPos + 20, topPos + 10,
                        0xffffff);
            } else {
                font.draw(poseStack,
                        FluxTranslate.SORT_BY.get() + ": " + ChatFormatting.AQUA + FluxTranslate.SORTING_SMART.get(),
                        leftPos + 19, topPos + 10, 0xffffff);
            }
        } else {
            renderNavigationPrompt(poseStack, FluxTranslate.ERROR_NO_SELECTED, EnumNavigationTab.TAB_SELECTION);
        }
    }

    @Override
    public void renderElement(PoseStack poseStack, IFluxDevice element, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BARS);

        int color = element.getDeviceType().mColor;

        float r = FluxUtils.getRed(color);
        float g = FluxUtils.getGreen(color);
        float b = FluxUtils.getBlue(color);

        int textColor = 0xffffff;

        if (mBatchConnections.size() > 0) {
            if (mBatchConnections.contains(element)) {
                fill(poseStack, x - 5, y + 1, x - 3, y + mElementHeight - 1, 0xccffffff);
                fill(poseStack, x + mElementWidth + 3, y + 1, x + mElementWidth + 5, y + mElementHeight - 1,
                        0xccffffff);
                RenderSystem.setShaderColor(r, g, b, 1.0f);
                blit(poseStack, x, y, 0, 32, mElementWidth, mElementHeight);
            } else {
                fill(poseStack, x - 5, y + 1, x - 3, y + mElementHeight - 1, 0xaa606060);
                fill(poseStack, x + mElementWidth + 3, y + 1, x + mElementWidth + 5, y + mElementHeight - 1,
                        0xaa606060);
                RenderSystem.setShaderColor(r * 0.5f, g * 0.5f, b * 0.5f, 1.0f);
                blit(poseStack, x, y, 0, 32, mElementWidth, mElementHeight);
                textColor = 0xd0d0d0;
            }
        } else {
            RenderSystem.setShaderColor(r, g, b, 1.0f);
            blit(poseStack, x, y, 0, 32, mElementWidth, mElementHeight);
        }
        int titleY;
        if (element.isChunkLoaded()) {
            poseStack.pushPose();
            poseStack.scale(0.75f, 0.75f, 1);
            font.draw(poseStack, FluxUtils.getTransferInfo(element, EnergyType.FE), (x + 20) / 0.75f,
                    (y + 10) / 0.75f, textColor);
            poseStack.popPose();
            titleY = y + 2;
        } else {
            textColor = 0x808080;
            titleY = y + 5;
        }
        if (element.getCustomName().isEmpty()) {
            font.draw(poseStack,
                    Language.getInstance().getOrDefault(element.getDisplayStack().getItem().getDescriptionId()),
                    x + 20, titleY, textColor);
        } else {
            font.draw(poseStack, element.getCustomName(), x + 21, titleY, textColor);
        }
        renderItemStack(element.getDisplayStack(), x + 2, y + 1);
    }

    @Override
    public void renderElementTooltip(PoseStack poseStack, IFluxDevice element, int mouseX, int mouseY) {
        renderComponentTooltip(poseStack, getElementTooltips(element), mouseX, mouseY);
    }

    protected List<Component> getElementTooltips(@Nonnull IFluxDevice element) {
        List<Component> components = new ArrayList<>();
        if (element.getCustomName().isEmpty()) {
            components.add(new TextComponent("").withStyle(ChatFormatting.BOLD)
                    .append(element.getDisplayStack().getHoverName()));
        } else {
            components.add(new TextComponent(element.getCustomName()).withStyle(ChatFormatting.BOLD));
        }

        if (element.isChunkLoaded()) {
            if (element.isForcedLoading()) {
                components.add(FluxTranslate.FORCED_LOADING.makeComponent().withStyle(ChatFormatting.AQUA));
            }
            components.add(new TextComponent(FluxUtils.getTransferInfo(element, EnergyType.FE)));
        } else {
            components.add(FluxTranslate.CHUNK_UNLOADED.makeComponent().withStyle(ChatFormatting.RED));
        }

        if (element.getDeviceType().isStorage()) {
            components.add(new TextComponent(FluxTranslate.ENERGY_STORED.get() + ": " + ChatFormatting.BLUE +
                    EnergyType.FE.getStorage(element.getTransferBuffer())));
        } else {
            components.add(new TextComponent(FluxTranslate.INTERNAL_BUFFER.get() + ": " + ChatFormatting.BLUE +
                    EnergyType.FE.getStorage(element.getTransferBuffer())));
        }

        components.add(new TextComponent(FluxTranslate.TRANSFER_LIMIT.get() + ": " + ChatFormatting.GREEN +
                (element.getDisableLimit() ? FluxTranslate.UNLIMITED.get() :
                        EnergyType.FE.getStorage(element.getRawLimit()))));
        components.add(new TextComponent(FluxTranslate.PRIORITY.get() + ": " + ChatFormatting.GREEN +
                (element.getSurgeMode() ? FluxTranslate.SURGE.get() : element.getRawPriority())));
        components.add(new TextComponent(FluxUtils.getDisplayPos(element.getGlobalPos()))
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        components.add(new TextComponent(FluxUtils.getDisplayDim(element.getGlobalPos()))
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        return components;
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof BatchEditButton) {
            if (button == mClear) {
                mBatchConnections.clear();
                mClear.setClickable(false);
                mEdit.setClickable(false);
                mDisconnect.setClickable(false);
            } else if (button == mEdit) {
                openPopup(new PopupConnectionEdit(this, true));
            } else if (button == mDisconnect) {
                /*List<GlobalPos> list =
                        mBatchConnections.stream().map(IFluxDevice::getGlobalPos).collect(Collectors.toList());
                C2SNetMsg.disconnect(network.getNetworkID(), list);*/
            }
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (!getNetwork().isValid()) {
            return redirectNavigationPrompt(mouseX, mouseY, mouseButton, EnumNavigationTab.TAB_SELECTION);
        }
        return false;
    }

   /* @Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (info == FeedbackInfo.SUCCESS) {
            closePopUp();
            mBatchConnections.clear();
            mClear.clickable = false;
            mEdit.clickable = false;
            mDisconnect.clickable = false;
            refreshPages(Lists.newArrayList(network.getAllConnections()));
        } else if (info == FeedbackInfo.SUCCESS_2) {
            closePopUp();
            if (container.bridge instanceof IFluxDevice) {
                final GlobalPos p = ((IFluxDevice) container.bridge).getGlobalPos();
                if (mBatchConnections.stream().anyMatch(f -> f.getGlobalPos().equals(p))) {
                    switchTab(EnumNavigationTab.TAB_SELECTION);
                    return;
                }
            }
            elements.removeAll(mBatchConnections);
            mBatchConnections.clear();
            mClear.clickable = false;
            mEdit.clickable = false;
            mDisconnect.clickable = false;
            refreshPages(Lists.newArrayList(network.getAllConnections()));
            page = Math.min(page, pages);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!networkValid)
            return;
        if (timer == 4 || timer == 14) {
            refreshPages(Lists.newArrayList(network.getAllConnections()));
        }
        if (timer % 5 == 0) {
            C2SNetMsg.requestConnectionUpdate(network.getNetworkID(),
                    current.stream().map(IFluxDevice::getGlobalPos).collect(Collectors.toList()));
        }
        timer++;
        timer %= 20;
    }*/

    @Override
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (code == FluxConstants.RESPONSE_REJECT) {
            switchTab(EnumNavigationTab.TAB_HOME, false);
            return;
        }
        if (key == FluxConstants.REQUEST_UPDATE_NETWORK) {
            refreshPages(getNetwork().getAllConnections());
        }
    }

    @Override
    protected void sortGrids(SortType sortType) {
        Comparator<IFluxDevice> comparator =
                Comparator.comparing((Function<IFluxDevice, Boolean>) f -> !f.isChunkLoaded())
                        .thenComparing(f -> f.getDeviceType().isStorage())
                        .thenComparing(f -> f.getDeviceType().isPlug())
                        .thenComparing(f -> f.getDeviceType().isPoint())
                        .thenComparingInt(p -> -p.getRawPriority());
        mElements.sort(comparator);
        refreshCurrentPageInternal();
    }
}
