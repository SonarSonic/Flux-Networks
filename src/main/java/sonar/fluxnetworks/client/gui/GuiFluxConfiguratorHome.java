package sonar.fluxnetworks.client.gui;

/*import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;
import sonar.fluxnetworks.common.network.C2SNetMsg;

import javax.annotation.Nonnull;
import java.util.Objects;

public class GuiFluxConfiguratorHome extends GuiTabCore {

    public InvisibleButton redirectButton;

    public NormalButton apply;
    public FluxTextWidget fluxName, priority, limit;

    public SlidedSwitchButton surge, disableLimit, chunkLoad;

    public ItemStack stack;
    public CompoundNBT configTag;

    public String stackCustomName;
    public int stackPriority;
    public long stackLimit;

    public boolean stackSurgeMode;
    public boolean stackDisableLimit;
    public boolean stackChunkLoading;

    private int timer;

    public GuiFluxConfiguratorHome(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        this.stack = ((ItemFluxConfigurator.MenuBridge) Objects.requireNonNull(container.bridge)).stack;
        updateSettingsFromTag();
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        screenUtils.renderNetwork(matrixStack, network.getNetworkName(), network.getNetworkColor(), 20, 8);
        drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 89, 150, FluxClientCache.getFeedbackColor());

        font.drawString(matrixStack, FluxTranslate.SURGE_MODE.t(), 20, 90, network.getNetworkColor());
        font.drawString(matrixStack, FluxTranslate.DISABLE_LIMIT.t(), 20, 102, network.getNetworkColor());
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12,
                EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
        addButton(redirectButton);

        int color = network.getNetworkColor() | 0xff000000;

        fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 16, guiTop + 28, 144, 12).setOutlineColor(color);
        fluxName.setMaxStringLength(24);
        fluxName.setText(stackCustomName);
        fluxName.setResponder(string -> {
            stackCustomName = fluxName.getText();
            onSettingsChanged();
        });
        addButton(fluxName);

        priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 16, guiTop + 45, 144, 12).setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(stackPriority));
        priority.setResponder(string -> {
            stackPriority = priority.getValidInt();
            onSettingsChanged();
        });
        addButton(priority);

        limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 16, guiTop + 62, 144, 12).setOutlineColor(color).setDigitsOnly().setMaxValue(Long.MAX_VALUE);
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(stackLimit));
        limit.setResponder(string -> {
            stackLimit = limit.getValidLong();
            onSettingsChanged();
        });
        addButton(limit);

        switches.add(surge = new SlidedSwitchButton(140, 90, 1, guiLeft, guiTop, stackSurgeMode));
        switches.add(disableLimit = new SlidedSwitchButton(140, 102, 2, guiLeft, guiTop, stackDisableLimit));
        //switches.add(chunkLoad = new SlidedSwitchButton(140, 144, 3, guiLeft, guiTop, stackChunkLoading));
        buttons.add(apply = new NormalButton(FluxTranslate.APPLY.t(), xSize / 2 - 36 / 2, 138, 36, 12, 3));
        apply.clickable = configTag == null;


    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (button instanceof SlidedSwitchButton) {
                SlidedSwitchButton switchButton = (SlidedSwitchButton) button;
                switchButton.switchButton();
                switch (switchButton.id) {
                    case 1:
                        stackSurgeMode = switchButton.toggled;
                        onSettingsChanged();
                        break;
                    case 2:
                        stackDisableLimit = switchButton.toggled;
                        onSettingsChanged();
                        break;
                    case 3:
                        stackChunkLoading = switchButton.toggled;
                        break;
                }
            }
            if (button == apply) {
                ///send changes to server.
                CompoundNBT tag = new CompoundNBT();
                tag.putInt(FluxConstants.NETWORK_ID, network.getNetworkID());
                tag.putInt(FluxConstants.PRIORITY, stackPriority);
                tag.putLong(FluxConstants.LIMIT, stackLimit);
                tag.putBoolean(FluxConstants.SURGE_MODE, stackSurgeMode);
                tag.putBoolean(FluxConstants.DISABLE_LIMIT, stackDisableLimit);

                C2SNetMsg.configuratorEdit(stackCustomName, tag);
                stack.setTagInfo(FluxConstants.TAG_FLUX_CONFIG, tag);
                updateSettingsFromTag();
                apply.setUnclickable();
            }
        }
    }

    public void updateSettingsFromTag() {
        configTag = stack.getChildTag(FluxConstants.TAG_FLUX_CONFIG);
        if (configTag != null) {
            stackCustomName = stack.getDisplayName().getString();
            stackPriority = configTag.getInt(FluxConstants.PRIORITY);
            stackLimit = configTag.getLong(FluxConstants.LIMIT);
            stackSurgeMode = configTag.getBoolean(FluxConstants.SURGE_MODE);
            stackDisableLimit = configTag.getBoolean(FluxConstants.DISABLE_LIMIT);
            stackChunkLoading = false; //disabled.
        } else {
            stackCustomName = stack.getDisplayName().getString();
            stackPriority = 0;
            stackSurgeMode = false;
            stackLimit = FluxConfig.defaultLimit;
            stackDisableLimit = false;
            stackChunkLoading = false; //disabled.
        }
    }

    public void onSettingsChanged() {
        if (configTag == null) {
            apply.clickable = true;
        } else {
            apply.clickable =
                    network.getNetworkID() != configTag.getInt(FluxConstants.NETWORK_ID) ||
                            stackPriority != configTag.getInt(FluxConstants.PRIORITY) ||
                            stackLimit != configTag.getLong(FluxConstants.LIMIT) ||
                            stackSurgeMode != configTag.getBoolean(FluxConstants.SURGE_MODE) ||
                            stackDisableLimit != configTag.getBoolean(FluxConstants.DISABLE_LIMIT) ||
                            !stackCustomName.equals(stack.getDisplayName().getString());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            C2SNetMsg.requestNetworkUpdate(network, FluxConstants.TYPE_NET_BASIC);
        }
        timer++;
        timer %= 100;
    }

}*/
