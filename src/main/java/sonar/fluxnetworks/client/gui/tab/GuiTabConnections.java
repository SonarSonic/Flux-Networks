package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.EditButton;
import sonar.fluxnetworks.client.gui.popup.PopupConnectionEdit;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

public class GuiTabConnections extends GuiTabPages<IFluxDevice> {

    //public InvisibleButton redirectButton;

    public final LinkedHashSet<IFluxDevice> mSelected = new LinkedHashSet<>();
    public boolean mSelectionMode;

    public EditButton mMultiselect;
    public EditButton mEdit;
    public EditButton mDisconnect;

    private int timer = 0;

    public GuiTabConnections(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mGridHeight = 19;
        mGridPerPage = 7;
        mElementWidth = 146;
        mElementHeight = 18;
        if (getNetwork().isValid()) {
            ClientMessages.updateNetwork(getToken(), getNetwork(), FluxConstants.NBT_NET_ALL_CONNECTIONS);
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
            mMultiselect = new EditButton(this, leftPos + 146, topPos + 9, 128, 64,
                    FluxTranslate.BATCH_CLEAR_BUTTON.get(), FluxTranslate.BATCH_SELECT_BUTTON.get());
            mEdit = new EditButton(this, leftPos + 118, topPos + 9, 192, 192,
                    FluxTranslate.BATCH_EDIT_BUTTON.get(), FluxTranslate.BATCH_EDIT_BUTTON.get());
            mEdit.setClickable(false);
            mDisconnect = new EditButton(this, leftPos + 132, topPos + 9, 0, 0,
                    FluxTranslate.BATCH_DISCONNECT_BUTTON.get(), FluxTranslate.BATCH_DISCONNECT_BUTTON.get());
            mDisconnect.setClickable(false);

            mButtons.add(mMultiselect);
            mButtons.add(mEdit);
            mButtons.add(mDisconnect);
        }
        refreshPages(getNetwork().getAllConnections());
    }

    @Override
    protected void drawBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (getNetwork().isValid()) {
            if (mSelectionMode) {
                font.draw(poseStack,
                        FluxTranslate.SELECTED.format(ChatFormatting.AQUA.toString() + mSelected.size() + ChatFormatting.RESET),
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
        RenderSystem.setShaderTexture(0, ICON);

        int color = element.getDeviceType().mColor;

        float r = FluxUtils.getRed(color);
        float g = FluxUtils.getGreen(color);
        float b = FluxUtils.getBlue(color);

        int textColor = 0xffffff;

        if (mSelectionMode) {
            if (mSelected.contains(element)) {
                fill(poseStack, x - 5, y + 1, x - 3, y + mElementHeight - 1, 0xccffffff);
                fill(poseStack, x + mElementWidth + 3, y + 1, x + mElementWidth + 5, y + mElementHeight - 1,
                        0xccffffff);
                RenderSystem.setShaderColor(r, g, b, 1.0f);
            } else {
                fill(poseStack, x - 5, y + 1, x - 3, y + mElementHeight - 1, 0xaa606060);
                fill(poseStack, x + mElementWidth + 3, y + 1, x + mElementWidth + 5, y + mElementHeight - 1,
                        0xaa606060);
                RenderSystem.setShaderColor(r * 0.5f, g * 0.5f, b * 0.5f, 1.0f);
                textColor = 0xd0d0d0;
            }
        } else {
            RenderSystem.setShaderColor(r, g, b, 1.0f);
        }
        blitF(poseStack, x, y, mElementWidth, mElementHeight, 0, 384, mElementWidth * 2, mElementHeight * 2);
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
            components.add(Component.literal("").withStyle(ChatFormatting.BOLD)
                    .append(element.getDisplayStack().getHoverName()));
        } else {
            components.add(Component.literal(element.getCustomName()).withStyle(ChatFormatting.BOLD));
        }

        if (element.isChunkLoaded()) {
            if (element.isForcedLoading()) {
                components.add(FluxTranslate.FORCED_LOADING.makeComponent().withStyle(ChatFormatting.AQUA));
            }
            components.add(Component.literal(FluxUtils.getTransferInfo(element, EnergyType.FE)));
        } else {
            components.add(FluxTranslate.CHUNK_UNLOADED.makeComponent().withStyle(ChatFormatting.RED));
        }

        if (element.getDeviceType().isStorage()) {
            components.add(Component.literal(FluxTranslate.ENERGY_STORED.get() + ": " + ChatFormatting.BLUE +
                    EnergyType.FE.getStorage(element.getTransferBuffer())));
        } else {
            components.add(Component.literal(FluxTranslate.INTERNAL_BUFFER.get() + ": " + ChatFormatting.BLUE +
                    EnergyType.FE.getStorage(element.getTransferBuffer())));
        }

        components.add(Component.literal(FluxTranslate.TRANSFER_LIMIT.get() + ": " + ChatFormatting.GREEN +
                (element.getDisableLimit() ? FluxTranslate.UNLIMITED.get() :
                        EnergyType.FE.getStorage(element.getRawLimit()))));
        components.add(Component.literal(FluxTranslate.PRIORITY.get() + ": " + ChatFormatting.GREEN +
                (element.getSurgeMode() ? FluxTranslate.SURGE.get() : element.getRawPriority())));
        components.add(Component.literal(FluxUtils.getDisplayPos(element.getGlobalPos()))
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        components.add(Component.literal(FluxUtils.getDisplayDim(element.getGlobalPos()))
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        return components;
    }

    @Override
    protected void onElementClicked(IFluxDevice element, int mouseButton) {
        if (mSelectionMode &&
                (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT || mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            if (mSelected.remove(element)) {
                if (mSelected.isEmpty()) {
                    mEdit.setClickable(false);
                    mDisconnect.setClickable(false);
                }
            } else if (element.isChunkLoaded()) {
                mSelected.add(element);
                mEdit.setClickable(true);
                mDisconnect.setClickable(true);
            }
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof EditButton) {
            if (button == mMultiselect) {
                if (mMultiselect.isChecked()) {
                    mMultiselect.setChecked(false);
                    mSelectionMode = false;
                } else {
                    mMultiselect.setChecked(true);
                    mSelectionMode = true;
                }
                mSelected.clear();
                mEdit.setClickable(false);
                mDisconnect.setClickable(false);
            } else if (button == mEdit) {
                assert mSelectionMode && !mSelected.isEmpty();
                openPopup(new PopupConnectionEdit(this));
            } else if (button == mDisconnect) {
                assert mSelectionMode && !mSelected.isEmpty();
                ClientMessages.disconnect(getToken(), getNetwork(), mSelected);
                mDisconnect.setClickable(false);
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
        } else if (code == FluxConstants.RESPONSE_SUCCESS) {
            closePopup();
            if (key == FluxConstants.REQUEST_DISCONNECT) {
                if (mSelected.stream().anyMatch(
                        f -> f.getGlobalPos().equals(((IFluxDevice) menu.mProvider).getGlobalPos()))) {
                    switchTab(EnumNavigationTab.TAB_HOME, false);
                    return;
                }
                mElements.removeAll(mSelected);
                refreshCurrentPage();
            }
            mSelected.clear();
            mSelectionMode = false;
            mMultiselect.setChecked(false);
            mEdit.setClickable(false);
            mDisconnect.setClickable(false);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        timer = (timer + 1) % 20;
        if (getCurrentPopup() == null && timer % 5 == 0) {
            ClientMessages.updateConnections(getToken(), getNetwork(), mCurrent);
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
    }
}
