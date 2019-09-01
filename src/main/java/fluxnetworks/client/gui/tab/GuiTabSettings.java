package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.SecurityType;
import fluxnetworks.client.gui.GuiFluxHome;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.ColorButton;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;

public class GuiTabSettings extends GuiTabCore {

    public SecurityType securityType;
    public EnergyType energyType;
    public TextboxButton name;
    public TextboxButton password;

    public GuiTabSettings(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        securityType = network.getSetting(NetworkSettings.NETWORK_SECURITY);
        energyType = network.getSetting(NetworkSettings.NETWORK_ENERGY);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, "Network Settings", 89, 10, 0xb4b4b4);
        fontRenderer.drawString("Name:", 14, 30, 0x606060);
        fontRenderer.drawString("Security Setting: " + TextFormatting.AQUA + securityType.getName(), 14, 50, 0x606060);
        if(securityType == SecurityType.ENCRYPTED)
            fontRenderer.drawString("Password: ", 14, 64, 0x606060);
        fontRenderer.drawString("Energy Type: " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);

        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().info, 89, 156, 0xffffff);
    }

    @Override
    public void initGui() {
        super.initGui();
        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(6).setMain();

        name = TextboxButton.create("", 1, fontRenderer, 42, 28, 118, 12);
        name.setMaxStringLength(24);
        name.setText(network.getNetworkName());

        password = TextboxButton.create("", 2, fontRenderer, 52, 63, 106, 12).setTextInvisible();
        password.setText(network.getSetting(NetworkSettings.NETWORK_PASSWORD));
        password.setMaxStringLength(16);
        password.setVisible(network.getSetting(NetworkSettings.NETWORK_SECURITY).isEncrypted());

        buttons.add(new NormalButton("Apply", 30, 140, 36, 12, 3));
        buttons.add(new NormalButton("Delete", 100, 140, 36, 12, 4));

        textBoxes.add(name);
        textBoxes.add(password);

    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 48 && mouseY < getGuiTop() + 60) {
                securityType = FluxUtils.incrementEnum(securityType, SecurityType.values());
                password.setText("");
                password.setVisible(!password.getVisible());
            }
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
            }

            for (NormalButton button : buttons) {
                if (button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if (button.id == 4) {
                        PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.DELETE_NETWORK, PacketGeneralHandler.getDeleteNetworkPacket(tileEntity.getNetworkID())));
                    }
                    if(button.id == 3) {
                        PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.EDIT_NETWORK, PacketGeneralHandler.getNetworkEditPacket(network.getNetworkID(), name.getText(), securityType, energyType, password.getText())));
                    }
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS) {
            FMLCommonHandler.instance().showGuiScreen(new GuiFluxHome(player, tileEntity));
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
        }
    }
}
