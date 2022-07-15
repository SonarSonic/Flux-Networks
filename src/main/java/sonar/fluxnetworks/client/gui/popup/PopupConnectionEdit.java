package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiPopupCore;
import sonar.fluxnetworks.client.gui.button.*;
import sonar.fluxnetworks.client.gui.tab.GuiTabConnections;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class PopupConnectionEdit extends GuiPopupCore<GuiTabConnections> {

    public SimpleButton mCancel;
    public SimpleButton mApply;

    public FluxEditBox mCustomName;
    public FluxEditBox mPriority;
    public FluxEditBox mLimit;

    public SwitchButton mSurgeMode;
    public SwitchButton mDisableLimit;
    public SwitchButton mChunkLoading;

    public ToggleButton mEditCustomName;
    public ToggleButton mEditPriority;
    public ToggleButton mEditLimit;
    public ToggleButton mEditSurgeMode;
    public ToggleButton mEditDisableLimit;
    public ToggleButton mEditChunkLoading;

    public final boolean mBatchMode;

    public PopupConnectionEdit(GuiTabConnections host, boolean batchMode) {
        super(host);
        mBatchMode = batchMode;
    }

    @Override
    public void init() {
        super.init();

        mCancel = new SimpleButton(this, leftPos + (imageWidth / 2) - 12 - 48, topPos + 146, 48, 12,
                FluxTranslate.CANCEL.get());
        mButtons.add(mCancel);
        mApply = new SimpleButton(this, leftPos + (imageWidth / 2) + 12, topPos + 146, 48, 12,
                FluxTranslate.APPLY.get());
        mApply.setClickable(false);
        mButtons.add(mApply);

        int color = mHost.getNetwork().getNetworkColor() | 0xFF000000;
        if (mBatchMode) {
            mCustomName = FluxEditBox.create(FluxTranslate.NAME.get() + ": ", font, leftPos + 20, topPos + 30, 136, 12)
                    .setOutlineColor(color);
            mCustomName.setMaxLength(TileFluxDevice.MAX_CUSTOM_NAME_LENGTH);
            addRenderableWidget(mCustomName);

            mPriority = FluxEditBox.create(FluxTranslate.PRIORITY.get() + ": ", font, leftPos + 20, topPos + 47, 136,
                            12)
                    .setOutlineColor(color)
                    .setDigitsOnly()
                    .setAllowNegatives(true);
            mPriority.setMaxLength(5);
            mPriority.setValue(Integer.toString(0));
            addRenderableWidget(mPriority);

            mLimit = FluxEditBox.create(FluxTranslate.TRANSFER_LIMIT.get() + ": ", font, leftPos + 20, topPos + 64,
                            136, 12)
                    .setOutlineColor(color)
                    .setDigitsOnly()
                    .setMaxValue(Long.MAX_VALUE);
            mLimit.setMaxLength(9);
            mLimit.setValue(Integer.toString(0));
            addRenderableWidget(mLimit);

            mEditCustomName = new ToggleButton(this, leftPos + 10, topPos + 33);
            mEditPriority = new ToggleButton(this, leftPos + 10, topPos + 50);
            mEditLimit = new ToggleButton(this, leftPos + 10, topPos + 67);

            mEditSurgeMode = new ToggleButton(this, leftPos + 10, topPos + 82);
            mEditDisableLimit = new ToggleButton(this, leftPos + 10, topPos + 94);
            mEditChunkLoading = new ToggleButton(this, leftPos + 10, topPos + 106);

            mButtons.add(mEditCustomName);
            mButtons.add(mEditPriority);
            mButtons.add(mEditLimit);

            mButtons.add(mEditSurgeMode);
            mButtons.add(mEditDisableLimit);
            mButtons.add(mEditChunkLoading);

            mSurgeMode = new SwitchButton(this, leftPos + 140, topPos + 82, false);
            mDisableLimit = new SwitchButton(this, leftPos + 140, topPos + 94, false);
            mChunkLoading = new SwitchButton(this, leftPos + 140, topPos + 106, false);

            mButtons.add(mSurgeMode);
            mButtons.add(mDisableLimit);
            mButtons.add(mChunkLoading);
        } else {
            mCustomName = FluxEditBox.create(FluxTranslate.NAME.get() + ": ", font, leftPos + 18, topPos + 30, 140, 12)
                    .setOutlineColor(color);
            mCustomName.setMaxLength(TileFluxDevice.MAX_CUSTOM_NAME_LENGTH);
            mCustomName.setValue(mHost.mSingleConnection.getCustomName());
            addRenderableWidget(mCustomName);

            mPriority = FluxEditBox.create(FluxTranslate.PRIORITY.get() + ": ", font, leftPos + 18, topPos + 47, 140,
                            12)
                    .setOutlineColor(color)
                    .setDigitsOnly()
                    .setAllowNegatives(true);
            mPriority.setMaxLength(5);
            mPriority.setValue(Integer.toString(mHost.mSingleConnection.getRawPriority()));
            addRenderableWidget(mPriority);

            mLimit = FluxEditBox.create(FluxTranslate.TRANSFER_LIMIT.get() + ": ", font, leftPos + 18, topPos + 64,
                            140, 12)
                    .setOutlineColor(color)
                    .setDigitsOnly()
                    .setMaxValue(Long.MAX_VALUE);
            mLimit.setMaxLength(9);
            mLimit.setValue(Long.toString(mHost.mSingleConnection.getRawPriority()));
            addRenderableWidget(mLimit);

            mSurgeMode = new SwitchButton(this, leftPos + 140, topPos + 82, mHost.mSingleConnection.getSurgeMode());
            mDisableLimit = new SwitchButton(this, leftPos + 140, topPos + 94,
                    mHost.mSingleConnection.getDisableLimit());

            mButtons.add(mSurgeMode);
            mButtons.add(mDisableLimit);

            if (!mHost.mSingleConnection.getDeviceType().isStorage()) {
                mChunkLoading = new SwitchButton(this, leftPos + 140, topPos + 106,
                        mHost.mSingleConnection.isForcedLoading());
                mButtons.add(mChunkLoading);
            }
        }
    }

    @Override
    public void drawForegroundLayer(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (!mBatchMode) {
            drawCenteredString(poseStack, font, FluxTranslate.SINGLE_EDIT.get(),
                    leftPos + 88, topPos + 14, 0xffffff);
            drawCenteredString(poseStack, font, FluxUtils.getDisplayPos(mHost.mSingleConnection.getGlobalPos()),
                    leftPos + 88, topPos + 121, 0xffffff);
            drawCenteredString(poseStack, font, FluxUtils.getDisplayDim(mHost.mSingleConnection.getGlobalPos()),
                    leftPos + 88, topPos + 130, 0xffffff);
        } else {
            drawCenteredString(poseStack, font, FluxTranslate.BATCH_EDIT.get(),
                    leftPos + 88, topPos + 14, 0xffffff);
            drawCenteredString(poseStack, font,
                    FluxTranslate.EDITING_CONNECTIONS.format(mHost.mBatchConnections.size()),
                    leftPos + 88, topPos + 122, 0xffffff);
        }
        font.draw(poseStack, FluxTranslate.SURGE_MODE.get(),
                leftPos + 20, topPos + 82, mHost.getNetwork().getNetworkColor());
        font.draw(poseStack, FluxTranslate.DISABLE_LIMIT.get(),
                leftPos + 20, topPos + 94, mHost.getNetwork().getNetworkColor());
        if (mChunkLoading != null) {
            font.draw(poseStack, FluxTranslate.CHUNK_LOADING.get(),
                    leftPos + 20, topPos + 106, mHost.getNetwork().getNetworkColor());
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        if (button instanceof SwitchButton switchButton) {
            switchButton.toggle();
            if (!mBatchMode) {
                mApply.setClickable(true);
            }
        } else if (button instanceof ToggleButton toggleButton) {
            toggleButton.toggle();
            mApply.setClickable(mButtons.stream().filter(b -> b instanceof ToggleButton)
                    .anyMatch(t -> ((ToggleButton) t).isChecked()));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            /*for (NormalButton button : popButtons) {
                if (button.clickable && button.isMouseHovered(this, (int) mouseX - guiLeft,
                        (int) mouseY - guiTop)) {
                    if (button.id == 11) {
                        host.closePopUp();
                        return true;
                    }
                    if (button.id == 12) {
                        List<GlobalPos> list;
                        int flags = 0;
                        if (mBatchMode) {
                            list = host.batchConnections.stream().map(IFluxDevice::getGlobalPos).collect(Collectors
                                    .toList());
                            if (mEditCustomName.checked) {
                                flags |= FluxConstants.FLAG_EDIT_NAME;
                            }
                            if (mEditPriority.checked) {
                                flags |= FluxConstants.FLAG_EDIT_PRIORITY;
                            }
                            if (mEditLimit.checked) {
                                flags |= FluxConstants.FLAG_EDIT_LIMIT;
                            }
                            if (mEditSurgeMode.checked) {
                                flags |= FluxConstants.FLAG_EDIT_SURGE_MODE;
                            }
                            if (mEditDisableLimit.checked) {
                                flags |= FluxConstants.FLAG_EDIT_DISABLE_LIMIT;
                            }
                            if (mEditChunkLoading.checked) {
                                flags |= FluxConstants.FLAG_EDIT_CHUNK_LOADING;
                            }
                        } else {
                            list = Lists.newArrayList(host.singleConnection.getGlobalPos());
                            flags |= FluxConstants.FLAG_EDIT_NAME | FluxConstants.FLAG_EDIT_PRIORITY
                                    | FluxConstants.FLAG_EDIT_LIMIT | FluxConstants.FLAG_EDIT_SURGE_MODE
                                    | FluxConstants.FLAG_EDIT_DISABLE_LIMIT | FluxConstants.FLAG_EDIT_CHUNK_LOADING;
                        }
                        //CompoundNBT tag = FluxUtils.getBatchEditingTag(fluxName, priority, limit, surgeMode,
                        // disableLimit, chunkLoading);
                        C2SNetMsg.editConnections(host.network.getNetworkID(), list, flags,
                                fluxName.getText(), priority.getIntegerFromText(true), limit.getLongFromText(false),
                                surgeMode != null && surgeMode.toggled,
                                disableLimit != null && disableLimit.toggled,
                                chunkLoading != null && chunkLoading.toggled);
                        //PacketHandler.CHANNEL.sendToServer(new CEditConnectionsMessage(host.network.getNetworkID(),
                        // list, tag, b));
                        return true;
                    }
                }
            }*/

        }
        return false;
    }
}
