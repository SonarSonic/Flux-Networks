package fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.*;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.api.NetworkColor;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.List;

public class GuiTabCreate extends GuiTabCore {

    private List<ColorButton> colorButtons = Lists.newArrayList();

    public SecurityType securityType = SecurityType.PUBLIC;
    public EnergyType energyType = EnergyType.RF;
    //public boolean allowConversion = true;
    public ColorButton color;
    public TextboxButton name;
    public TextboxButton password;

    public GuiTabCreate(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);

        drawCenteredString(fontRenderer, "Create New Network", 89, 10, 0xb4b4b4);
        fontRenderer.drawString("Name:", 14, 30, 0x606060);
        fontRenderer.drawString("Security Setting: " + TextFormatting.AQUA + securityType.getName(), 14, 50, 0x606060);
        if(securityType == SecurityType.ENCRYPTED)
            fontRenderer.drawString("Password: ", 14, 64, 0x606060);
        fontRenderer.drawString("Energy Type: " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);
        fontRenderer.drawString("Color:", 14, 97, 0x606060);

        renderNetwork(name.getText(), color.color.color, 20, 129);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
        for(ColorButton button : colorButtons) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {

        colorButtons.clear();
        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7).setMain());

        name = TextboxButton.create("", 1, fontRenderer, 42, 28, 118, 12);
        name.setMaxStringLength(24);
        name.setText(mc.player.getName() + "'s Network");

        password = TextboxButton.create("", 2, fontRenderer, 52, 63, 106, 12).setTextInvisible();
        password.setMaxStringLength(16);
        password.setVisible(false);

        int x = 0, y = 0;
        for(NetworkColor color : NetworkColor.values()) {
            colorButtons.add(new ColorButton(width / 2 - 40 + x * 16, height / 2 + 13 + y * 16, color));
            x++;
            if(x == 7) {
                x = 0;
                y++;
            }
        }
        color = colorButtons.get(0);
        color.selected = true;

        buttons.add(new NormalButton("Create", 70, 150, 36, 12, 3));

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
            /*if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 62 && mouseY < getGuiTop() + 74) {
                allowConversion = !allowConversion;
            }*/
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
            }

            for(ColorButton button : colorButtons) {
                if(button.isMouseHovered(mc, mouseX, mouseY)) {
                    color.selected = false;
                    color = button;
                    color.selected = true;
                }
            }

            for(NormalButton button : buttons) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 3) {
                        PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CREATE_NETWORK, PacketGeneralHandler.getCreateNetworkPacket(name.getText(), color.color.color, securityType, energyType, password.getText())));
                        FMLCommonHandler.instance().showGuiScreen(new GuiTabSelection(player, tileEntity));
                    }
                }
            }
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(!textBoxes.stream().anyMatch(GuiTextField::isFocused)) {
                mc.player.closeScreen();
            }
        }

        for(TextboxButton text : textBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
            }
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) throws IOException {
        super.keyTypedPop(c, k);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
