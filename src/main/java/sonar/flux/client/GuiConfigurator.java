package sonar.flux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.configurator.FluxConfigurationType;
import sonar.flux.common.ContainerConfigurator;
import sonar.flux.common.item.FluxConfigurator;
import sonar.flux.network.PacketConfiguratorSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class GuiConfigurator extends GuiContainer {

    public HashMap<FluxConfigurationType, Boolean> configs = new HashMap<>();

    public GuiConfigurator(EntityPlayer player, ItemStack configurator) {
        super(new ContainerConfigurator(player));
        NBTTagCompound tag = configurator.getOrCreateSubCompound(FluxConfigurator.DISABLED_TAG);
        for (FluxConfigurationType type : FluxConfigurationType.values()) {
            boolean disabled = tag.getBoolean(type.getNBTName());
            configs.put(type, disabled);
        }
    }

    public void initGui() {
        super.initGui();
        for (Entry<FluxConfigurationType, Boolean> entry : configs.entrySet()) {
            int ordinal = entry.getKey().ordinal();
            buttonList.add(new GuiButton(ordinal, guiLeft + 100, guiTop + 20 + ordinal * 24, 60, 20, entry.getValue() ? TextFormatting.RED + "DISABLED" : TextFormatting.GREEN + "ENABLED"));
        }
    }

    protected void actionPerformed(GuiButton button) {
        FluxConfigurationType type = FluxConfigurationType.values()[button.id];
        configs.put(type, !configs.get(type));

        this.buttonList.clear();
        this.initGui();
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        FontHelper.textCentre(TextFormatting.UNDERLINE + "Flux Configurator", this.xSize, 8, -1);
        for (Entry<FluxConfigurationType, Boolean> entry : configs.entrySet()) {
            FontHelper.text(entry.getKey().name(), 8, 26 + entry.getKey().ordinal() * 24, -1);
        }
    }

    public NBTTagCompound getNewDisabledTag() {
        NBTTagCompound disabled = new NBTTagCompound();
        for (Entry<FluxConfigurationType, Boolean> entry : configs.entrySet()) {
            if (entry.getValue()) {
                disabled.setBoolean(entry.getKey().getNBTName(), true);
            }
        }
        return disabled;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiFluxBase.bground);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            FluxNetworks.network.sendToServer(new PacketConfiguratorSettings(getNewDisabledTag()));
        }
        super.keyTyped(typedChar, keyCode);
    }
}