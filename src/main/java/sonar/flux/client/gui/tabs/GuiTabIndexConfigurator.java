package sonar.flux.client.gui.tabs;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.SonarHelper;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.EnumActivationType;
import sonar.flux.api.EnumPriorityType;
import sonar.flux.api.configurator.FluxConfigurationType;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.buttons.CheckBox;
import sonar.flux.client.gui.buttons.ConfiguratorSettingButton;
import sonar.flux.client.gui.buttons.FluxTextField;
import sonar.flux.common.item.ItemConfigurator;
import sonar.flux.connection.NetworkSettings;
import sonar.flux.network.PacketUpdateGuiItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabIndexConfigurator extends GuiTabAbstract {

    public HashMap<FluxConfigurationType, Boolean> configs = new HashMap<>();
    public ItemStack stack;
    public NBTTagCompound tag;
    public boolean isConfigured = false;
    public SonarTextField priority_field, limit_field;

    public EnumActivationType activationType;
    public int priority;
    public EnumPriorityType priorityType;
    public long transfer_limit;
    public boolean enable_limit;


    public GuiTabIndexConfigurator(List<EnumGuiTab> tabs) {
        super(tabs);
        stack = FluxNetworks.proxy.getFluxStack();
        NBTTagCompound disabledTag = stack.getOrCreateSubCompound(ItemConfigurator.DISABLED_TAG);
        tag = stack.getOrCreateSubCompound(ItemConfigurator.CONFIGS_TAG);
        activationType = tag.hasKey(FluxConfigurationType.REDSTONE_SETTING.getNBTName()) ? EnumActivationType.values()[tag.getInteger(FluxConfigurationType.REDSTONE_SETTING.getNBTName())] : EnumActivationType.ACTIVATED;
        priority = tag.hasKey(FluxConfigurationType.PRIORITY.getNBTName()) ? tag.getInteger(FluxConfigurationType.PRIORITY.getNBTName()) : 0;
        priorityType = tag.hasKey(FluxConfigurationType.PRIORITY_SETTING.getNBTName()) ? EnumPriorityType.values()[tag.getInteger(FluxConfigurationType.PRIORITY_SETTING.getNBTName())] : EnumPriorityType.NORMAL;
        transfer_limit = tag.hasKey(FluxConfigurationType.TRANSFER.getNBTName()) ? tag.getLong(FluxConfigurationType.TRANSFER.getNBTName()) : FluxConfig.defaultLimit;
        enable_limit = !tag.hasKey(FluxConfigurationType.TRANSFER_SETTING.getNBTName()) || tag.getBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTName());
        isConfigured = !disabledTag.hasNoTags();
        for (FluxConfigurationType type : FluxConfigurationType.VALUES) {
            boolean disabled = disabledTag.getBoolean(type.getNBTName());
            configs.put(type, disabled);
        }
    }

    public void initGui() {
        super.initGui();
        int colour = NETWORK_COLOUR.getValue(common).getRGB();
        for (Map.Entry<FluxConfigurationType, Boolean> entry : configs.entrySet()) {
            int ordinal = entry.getKey().ordinal;
            buttonList.add(new CheckBox(this, ordinal+1, getGuiLeft() + 156, getGuiTop() + 28 + ordinal*18, () -> !configs.get(entry.getKey()), "Copy Setting"));
        }
        buttonList.add(new ConfiguratorSettingButton(this, 10, getGuiLeft() + 8, getGuiTop() + 28, 147, 12, colour, "Network: ", common.isFakeNetwork() ? "NONE" :common.getSyncSetting(NetworkSettings.NETWORK_NAME).getValue()));
        buttonList.add(new ConfiguratorSettingButton(this, 11, getGuiLeft() + 8, getGuiTop() + 46, 147, 12, colour, "Redstone: ", activationType.comment.t()));
        buttonList.add(new ConfiguratorSettingButton(this, 13, getGuiLeft() + 8, getGuiTop() + 82, 147, 12, colour, FluxTranslate.PRIORITY_MODE.t() + ": ", priorityType.comment.t()));
        buttonList.add(new ConfiguratorSettingButton(this, 15, getGuiLeft() + 8, getGuiTop() + 118, 147, 12, colour, FluxTranslate.ENABLE_LIMIT.t() + ": ", "" + !enable_limit));

        priority_field = FluxTextField.create(FluxTranslate.PRIORITY.t() + ": ", 12, getFontRenderer(), 8, 64, 147, 12).setBoxOutlineColour(colour).setDigitsOnly(true);
        priority_field.setMaxStringLength(8);
        priority_field.setText("" + priority);


        limit_field = FluxTextField.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", 14, getFontRenderer(), 8, 100, 147, 12).setBoxOutlineColour(colour).setDigitsOnly(true);
        limit_field.setMaxStringLength(8);
        limit_field.setText("" + transfer_limit);


        fieldList.addAll(Lists.newArrayList(priority_field, limit_field));
    }

    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button.id >= 1 && button.id < FluxConfigurationType.VALUES.length+1) {
            FluxConfigurationType type = FluxConfigurationType.VALUES[button.id-1];
            configs.put(type, !configs.get(type));
            doReset();
        }else if(button.id >= 10){

            switch(button.id){
                case 10://network
                    switchTab(EnumGuiTab.NETWORK_SELECTION);
                    break;
                case 11:
                    activationType = SonarHelper.incrementEnum(activationType, EnumActivationType.values());
                    doReset();
                    break;
                case 12://priority
                    break;
                case 13:
                    priorityType = SonarHelper.incrementEnum(priorityType, EnumPriorityType.values());
                    doReset();
                    break;
                case 14://transfer limit
                    break;
                case 15:
                    enable_limit = !enable_limit;
                    doReset();
                    break;
            }
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        renderNetwork(NETWORK_NAME.getValue(common), NETWORK_ACCESS.getValue(common), NETWORK_COLOUR.getValue(common).getRGB(), true, 11, 8);
    }

    public NBTTagCompound getNewDisabledTag() {
        NBTTagCompound disabled = new NBTTagCompound();
        for (Map.Entry<FluxConfigurationType, Boolean> entry : configs.entrySet()) {
            if (entry.getValue()) {
                disabled.setBoolean(entry.getKey().getNBTName(), true);
            }
        }
        return disabled;
    }

    public NBTTagCompound getNewConfigsTag() {
        NBTTagCompound configs = new NBTTagCompound();
        configs.setInteger(FluxConfigurationType.NETWORK.getNBTName(), getNetworkID());
        configs.setInteger(FluxConfigurationType.REDSTONE_SETTING.getNBTName(), activationType.ordinal());
        configs.setInteger(FluxConfigurationType.PRIORITY.getNBTName(), priority);
        configs.setInteger(FluxConfigurationType.PRIORITY_SETTING.getNBTName(), priorityType.ordinal());
        configs.setLong(FluxConfigurationType.TRANSFER.getNBTName(), transfer_limit);
        configs.setBoolean(FluxConfigurationType.TRANSFER_SETTING.getNBTName(), enable_limit);
        return configs;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTabAbstract.blank_flux_gui);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (isCloseKey(keyCode)) {
            sendGuiStackToServer();
        }
        super.keyTyped(typedChar, keyCode);
    }

    public void sendGuiStackToServer(){
        ItemStack stack = FluxNetworks.proxy.getFluxStack();
        if(stack != null) {
            stack.setTagInfo(ItemConfigurator.DISABLED_TAG, getNewDisabledTag());
            stack.setTagInfo(ItemConfigurator.CONFIGS_TAG, getNewConfigsTag());
            FluxNetworks.network.sendToServer(new PacketUpdateGuiItem(stack));
        }
    }

    @Override
    public EnumGuiTab getCurrentTab() {
        return EnumGuiTab.INDEX;
    }

    @Override
    public ResourceLocation getBackground() {
        return scroller_flux_gui;
    }
}
