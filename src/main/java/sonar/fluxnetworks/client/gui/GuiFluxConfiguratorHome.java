package sonar.fluxnetworks.client.gui;

import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.utils.FluxConfigurationType;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.client.gui.button.TextboxButton;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemConfigurator;
import sonar.fluxnetworks.common.network.PacketConfiguratorSettings;
import sonar.fluxnetworks.common.network.PacketNetworkUpdateRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import static sonar.fluxnetworks.common.core.FluxUtils.CONFIGS_TAG;

public class GuiFluxConfiguratorHome extends GuiTabCore {

    public NormalButton apply;
    public TextboxButton fluxName, priority, limit;

    public SlidedSwitchButton surge, disableLimit, chunkLoad;

    public ItemStack stack;
    public NBTTagCompound configTag;

    public String stackCustomName;
    public int stackPriority;
    public long stackLimit;

    public boolean stackSurgeMode;
    public boolean stackDisableLimit;
    public boolean stackChunkLoading;

    private int timer;

    public GuiFluxConfiguratorHome(EntityPlayer player, ItemConfigurator.NetworkConnector connector) {
        super(player, connector);
        this.stack = connector.stack;
        updateSettingsFromTag();
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        fontRenderer.drawString(FluxTranslate.SURGE_MODE.t(), 20, 90, network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(FluxTranslate.DISABLE_LIMIT.t(), 20, 102, network.getSetting(NetworkSettings.NETWORK_COLOR));
    }

    @Override
    public void initGui() {
        super.initGui();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        int color = network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
        fluxName = TextboxButton.create(this, FluxTranslate.NAME.t() + ": ", 0, fontRenderer, 16, 28, 144, 12).setOutlineColor(color);
        fluxName.setMaxStringLength(24);
        fluxName.setText(stackCustomName);

        priority = TextboxButton.create(this, FluxTranslate.PRIORITY.t() + ": ", 1, fontRenderer, 16, 45, 144, 12).setOutlineColor(color).setDigitsOnly();
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(stackPriority));

        limit = TextboxButton.create(this, FluxTranslate.TRANSFER_LIMIT.t() + ": ", 2, fontRenderer, 16, 62, 144, 12).setOutlineColor(color).setDigitsOnly();
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(stackLimit));

        switches.add(surge = new SlidedSwitchButton(140, 90, 1, guiLeft, guiTop, stackSurgeMode));
        switches.add(disableLimit = new SlidedSwitchButton(140, 102, 2, guiLeft, guiTop, stackDisableLimit));
        //switches.add(chunkLoad = new SlidedSwitchButton(140, 144, 3, guiLeft, guiTop, stackChunkLoading));
        buttons.add(apply = new NormalButton(FluxTranslate.APPLY.t(), xSize / 2 - 36 / 2, 138, 36, 12, 3));
        apply.clickable = configTag == null;

        textBoxes.add(fluxName);
        textBoxes.add(priority);
        textBoxes.add(limit);
    }

    @Override
    public void onTextBoxChanged(TextboxButton text) {
        super.onTextBoxChanged(text);
        if (text == fluxName) {
            stackCustomName = fluxName.getText();
            onSettingsChanged();
        } else if (text == priority) {
            stackPriority = priority.getIntegerFromText(false);
            onSettingsChanged();
        } else if (text == limit) {
            stackLimit = Math.min(limit.getLongFromText(true), Long.MAX_VALUE);
            limit.setText(String.valueOf(stackLimit));
            onSettingsChanged();
        }
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (button instanceof SlidedSwitchButton) {
                SlidedSwitchButton switchButton = (SlidedSwitchButton) button;
                switchButton.switchButton();
                switch (switchButton.id) {
                    case 1:
                        stackSurgeMode = switchButton.slideControl;
                        onSettingsChanged();
                        break;
                    case 2:
                        stackDisableLimit = switchButton.slideControl;
                        onSettingsChanged();
                        break;
                    case 3:
                        stackChunkLoading = switchButton.slideControl;
                        break;
                }
            }
            if (button == apply) {
                ///send changes to server.
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger(FluxConfigurationType.NETWORK.getNBTName(), network.getNetworkID());
                tag.setInteger(FluxConfigurationType.PRIORITY.getNBTName(), stackPriority);
                tag.setLong(FluxConfigurationType.TRANSFER.getNBTName(), stackLimit);
                tag.setBoolean(FluxConfigurationType.PRIORITY_SETTING.getNBTName(), stackSurgeMode);
                tag.setBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTName(), stackDisableLimit);

                PacketHandler.network.sendToServer(new PacketConfiguratorSettings.ConfiguratorSettingsMessage(stackCustomName, tag));
                stack.setTagInfo(CONFIGS_TAG, tag);
                updateSettingsFromTag();
                apply.setUnclickable();
            }
        }
    }

    public void updateSettingsFromTag() {
        configTag = stack.getSubCompound(CONFIGS_TAG);
        if (configTag != null) {
            stackCustomName = stack.getDisplayName();
            stackPriority = configTag.getInteger(FluxConfigurationType.PRIORITY.getNBTName());
            stackSurgeMode = configTag.getBoolean(FluxConfigurationType.PRIORITY_SETTING.getNBTName());
            stackLimit = configTag.getLong(FluxConfigurationType.TRANSFER.getNBTName());
            stackDisableLimit = configTag.getBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTName());
            stackChunkLoading = false; //disabled.
        } else {
            stackCustomName = stack.getDisplayName();
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
                    network.getNetworkID() != configTag.getInteger(FluxConfigurationType.NETWORK.getNBTName()) ||
                            stackPriority != configTag.getInteger(FluxConfigurationType.PRIORITY.getNBTName()) ||
                            stackLimit != configTag.getLong(FluxConfigurationType.TRANSFER.getNBTName()) ||
                            stackSurgeMode != configTag.getBoolean(FluxConfigurationType.PRIORITY_SETTING.getNBTName()) ||
                            stackDisableLimit != configTag.getBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTName()) ||
                            !stackCustomName.equals(stack.getDisplayName());
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (timer == 0) {
            PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_GENERAL));
        }
        timer++;
        timer %= 100;
    }

}
