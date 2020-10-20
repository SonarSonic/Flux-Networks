package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.network.CConfiguratorSettingMessage;
import sonar.fluxnetworks.common.network.CNetworkUpdateMessage;

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

    public GuiFluxConfiguratorHome(PlayerEntity player, ItemFluxConfigurator.ContainerProvider connector) {
        super(player, connector);
        this.stack = connector.stack;
        updateSettingsFromTag();
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        screenUtils.renderNetwork(matrixStack, network.getNetworkName(), network.getNetworkColor(), 20, 8);
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        font.drawString(matrixStack, FluxTranslate.SURGE_MODE.t(), 20, 90, network.getNetworkColor());
        font.drawString(matrixStack, FluxTranslate.DISABLE_LIMIT.t(), 20, 102, network.getNetworkColor());
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
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
                tag.putInt(FluxConfigurationType.NETWORK.getNBTKey(), network.getNetworkID());
                tag.putInt(FluxConfigurationType.PRIORITY.getNBTKey(), stackPriority);
                tag.putLong(FluxConfigurationType.TRANSFER.getNBTKey(), stackLimit);
                tag.putBoolean(FluxConfigurationType.PRIORITY_SETTING.getNBTKey(), stackSurgeMode);
                tag.putBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTKey(), stackDisableLimit);

                NetworkHandler.INSTANCE.sendToServer(new CConfiguratorSettingMessage(stackCustomName, tag));
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
            stackPriority = configTag.getInt(FluxConfigurationType.PRIORITY.getNBTKey());
            stackSurgeMode = configTag.getBoolean(FluxConfigurationType.PRIORITY_SETTING.getNBTKey());
            stackLimit = configTag.getLong(FluxConfigurationType.TRANSFER.getNBTKey());
            stackDisableLimit = configTag.getBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTKey());
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
                    network.getNetworkID() != configTag.getInt(FluxConfigurationType.NETWORK.getNBTKey()) ||
                            stackPriority != configTag.getInt(FluxConfigurationType.PRIORITY.getNBTKey()) ||
                            stackLimit != configTag.getLong(FluxConfigurationType.TRANSFER.getNBTKey()) ||
                            stackSurgeMode != configTag.getBoolean(FluxConfigurationType.PRIORITY_SETTING.getNBTKey()) ||
                            stackDisableLimit != configTag.getBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTKey()) ||
                            !stackCustomName.equals(stack.getDisplayName());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            NetworkHandler.INSTANCE.sendToServer(new CNetworkUpdateMessage(network.getNetworkID(), FluxConstants.TYPE_NET_BASIC));
        }
        timer++;
        timer %= 100;
    }

}
