package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.client.gui.button.SwitchButton;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.device.TransferHandler;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;

/**
 * The home page.
 */
public class GuiFluxDeviceHome extends GuiTabCore {

    //public InvisibleButton redirectButton;
    public FluxEditBox mCustomName, mPriority, mLimit;

    public SwitchButton mSurgeMode, mDisableLimit, mChunkLoading;

    private int timer;

    public GuiFluxDeviceHome(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    @Override
    public GuiTabType getCurrentTab() {
        return GuiTabType.TAB_HOME;
    }

    public TileFluxDevice getDevice() {
        return (TileFluxDevice) menu.mProvider;
    }

    @Override
    public void init() {
        super.init();

        /*redirectButton = new InvisibleButton(leftPos + 20, topPos + 8, 135, 12,
                EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
        addButton(redirectButton);*/

        int color = mNetwork.getNetworkColor() | 0xFF000000;
        mCustomName = FluxEditBox.create(FluxTranslate.NAME.get() + ": ", font,
                        leftPos + 16, topPos + 28, 144, 12)
                .setOutlineColor(color);
        mCustomName.setMaxLength(24);
        mCustomName.setValue(getDevice().getCustomName());
        mCustomName.setResponder(string -> {
            CompoundTag tag = new CompoundTag();
            tag.putString(FluxConstants.CUSTOM_NAME, mCustomName.getValue());
            ClientMessages.sendEditDevice(getDevice(), tag);
        });
        addRenderableWidget(mCustomName);

        mPriority = FluxEditBox.create(FluxTranslate.PRIORITY.get() + ": ", font,
                        leftPos + 16, topPos + 45, 144, 12)
                .setOutlineColor(color)
                .setDigitsOnly()
                .setAllowNegatives(true);
        mPriority.setMaxLength(5);
        mPriority.setValue(String.valueOf(getDevice().getLiteralPriority()));
        mPriority.setResponder(string -> {
            int priority = Mth.clamp(mPriority.getValidInt(),
                    TransferHandler.PRI_USER_MIN, TransferHandler.PRI_USER_MAX);
            CompoundTag tag = new CompoundTag();
            tag.putInt(FluxConstants.PRIORITY, priority);
            ClientMessages.sendEditDevice(getDevice(), tag);
        });
        addRenderableWidget(mPriority);

        mLimit = FluxEditBox.create(FluxTranslate.TRANSFER_LIMIT.get() + ": ", font,
                        leftPos + 16, topPos + 62, 144, 12)
                .setOutlineColor(color)
                .setDigitsOnly()
                .setMaxValue(getDevice().getMaxTransferLimit());
        mLimit.setMaxLength(9);
        mLimit.setValue(String.valueOf(getDevice().getLiteralLimit()));
        mLimit.setResponder(string -> {
            long limit = mLimit.getValidLong();
            CompoundTag tag = new CompoundTag();
            tag.putLong(FluxConstants.LIMIT, limit);
            ClientMessages.sendEditDevice(getDevice(), tag);
        });
        addRenderableWidget(mLimit);

        mSurgeMode = new SwitchButton(minecraft, leftPos + 140, topPos + 120, getDevice().getSurgeMode());
        mDisableLimit = new SwitchButton(minecraft, leftPos + 140, topPos + 132, getDevice().getDisableLimit());
        mButtons.add(mSurgeMode);
        mButtons.add(mDisableLimit);

        if (!getDevice().getDeviceType().isStorage()) {
            mChunkLoading = new SwitchButton(minecraft, leftPos + 140, topPos + 144, getDevice().isForcedLoading());
            mButtons.add(mChunkLoading);
        }
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);

        renderNetwork(poseStack, leftPos + 20, topPos + 8);
        renderTransfer(poseStack, getDevice(), leftPos + 30, topPos + 90);

        if (mCustomName.getValue().isEmpty()) {
            int y = mCustomName.y + (mCustomName.getHeight() - 8) / 2;
            font.draw(poseStack,
                    Language.getInstance().getOrDefault(getDevice().getBlockState().getBlock().getDescriptionId()),
                    mCustomName.x + 4, y, FluxConstants.INVALID_NETWORK_COLOR);
        }

        font.draw(poseStack, FluxTranslate.SURGE_MODE.get(), 20 + leftPos, 120 + topPos, mNetwork.getNetworkColor());
        font.draw(poseStack, FluxTranslate.DISABLE_LIMIT.get(), 20 + leftPos, 132 + topPos, mNetwork.getNetworkColor());

        if (mChunkLoading != null) {
            font.draw(poseStack, FluxTranslate.CHUNK_LOADING.get(), 20 + leftPos, 144 + topPos, mNetwork.getNetworkColor());
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && button instanceof SwitchButton switchButton) {
            switchButton.toggle();
            if (switchButton == mSurgeMode) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean(FluxConstants.SURGE_MODE, mSurgeMode.isChecked());
                ClientMessages.sendEditDevice(getDevice(), tag);
            } else if (switchButton == mDisableLimit) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean(FluxConstants.DISABLE_LIMIT, mDisableLimit.isChecked());
                ClientMessages.sendEditDevice(getDevice(), tag);
            }/* else if (switchButton == mChunkLoading) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean(FluxConstants.DISABLE_LIMIT, mDisableLimit.isChecked());
                ClientMessages.sendEditDevice(menu.mDevice, tag);
            }*/
            //FIXME chunk loading
        }
    }

    /*@Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            C2SNetMsg.requestNetworkUpdate(network, FluxConstants.TYPE_NET_BASIC);
        }
        if (chunkLoading != null) {
            chunkLoading.toggled = tileEntity.isForcedLoading();
        }
        timer++;
        timer %= 100;
    }*/
}
