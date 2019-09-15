package fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.NetworkColor;
import fluxnetworks.api.SecurityType;
import fluxnetworks.client.gui.GuiFluxHome;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.basic.GuiTextField;
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
import fluxnetworks.common.registry.RegistrySounds;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.List;

public class GuiTabSettings extends GuiTabCore {

    private List<ColorButton> colorButtons = Lists.newArrayList();

    public SecurityType securityType;
    public EnergyType energyType;
    public ColorButton color;
    public TextboxButton name;
    public TextboxButton password;
    public TextboxButton customColor;
    public NormalButton apply, delete, colorApply;
    public int deleteCount;

    public GuiTabSettings(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        if(networkValid) {
            securityType = network.getSetting(NetworkSettings.NETWORK_SECURITY);
            energyType = network.getSetting(NetworkSettings.NETWORK_ENERGY);
        }
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            if(mouseX > 30 + guiLeft && mouseX < 66 + guiLeft && mouseY > 140 + guiTop && mouseY < 152 + guiTop) {
                if(delete.clickable) {
                    drawCenteredString(fontRenderer, TextFormatting.BOLD + FluxTranslate.DELETE_NETWORK.t(), 48, 128, 0xff0000);
                } else {
                    drawCenteredString(fontRenderer, FluxTranslate.DOUBLE_SHIFT.t(), 48, 128, 0xffffff);
                }
            }
            drawCenteredString(fontRenderer, FluxTranslate.TAB_SETTING.t(), 89, 10, 0xb4b4b4);
            fontRenderer.drawString(FluxTranslate.NETWORK_NAME.t() + ":", 14, 30, 0x606060);
            fontRenderer.drawString(FluxTranslate.NETWORK_SECURITY.t() + ": " + TextFormatting.AQUA + securityType.getName(), 14, 50, 0x606060);
            if (securityType == SecurityType.ENCRYPTED)
                fontRenderer.drawString(FluxTranslate.NETWORK_PASSWORD.t() + ": ", 14, 64, 0x606060);
            fontRenderer.drawString(FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);
            fontRenderer.drawString(FluxTranslate.NETWORK_COLOR.t() + ":", 14, 97, 0x606060);

            drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 89, 156, 0xffffff);
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(30, 44, 60, 118, 0xccffffff, 0x80000000);
        super.drawPopupForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, FluxTranslate.CUSTOM_COLOR.t(), 89, 48, 0xffffff);
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
        if(!main) {
            initPopGui();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(6).setMain();

        if(networkValid) {
            int l = fontRenderer.getStringWidth(FluxTranslate.NETWORK_NAME.t());
            name = TextboxButton.create(this, "", 1, fontRenderer, 20 + l, 28, 140 - l, 12);
            name.setMaxStringLength(24);
            name.setText(network.getNetworkName());

            l = fontRenderer.getStringWidth(FluxTranslate.NETWORK_PASSWORD.t());
            password = TextboxButton.create(this, "", 2, fontRenderer, 20 + l, 62, 140 - l, 12).setTextInvisible();
            password.setText(network.getSetting(NetworkSettings.NETWORK_PASSWORD));
            password.setMaxStringLength(16);
            password.setVisible(network.getSetting(NetworkSettings.NETWORK_SECURITY).isEncrypted());

            apply = new NormalButton(FluxTranslate.APPLY.t(), 112, 140, 36, 12, 3).setUnclickable();
            delete = new NormalButton(FluxTranslate.DELETE.t(), 30, 140, 36, 12, 4).setUnclickable();
            buttons.add(apply);
            buttons.add(delete);

            int x = 0, y = 0;
            boolean colorSet = false;
            for (NetworkColor color : NetworkColor.values()) {
                ColorButton b = new ColorButton(width / 2 - 40 + x * 16, height / 2 + 13 + y * 16, color.color);
                colorButtons.add(b);
                if(!colorSet && color.color == network.getSetting(NetworkSettings.NETWORK_COLOR)) {
                    this.color = b;
                    this.color.selected = true;
                    colorSet = true;
                }
                x++;
                if (x == 7) {
                    x = 0;
                    y++;
                }
            }
            if(!colorSet) {
                ColorButton c = new ColorButton(width / 2 - 56, height / 2 + 29, network.getSetting(NetworkSettings.NETWORK_COLOR));
                colorButtons.add(c);
                this.color = c;
                this.color.selected = true;
            }

            textBoxes.add(name);
            textBoxes.add(password);
        }

    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(networkValid) {
            if(mouseButton == 0 || mouseButton == 1) {
                if (mouseButton == 0) {
                    if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 48 && mouseY < getGuiTop() + 60) {
                        securityType = FluxUtils.incrementEnum(securityType, SecurityType.values());
                        password.setText("");
                        password.setVisible(!password.getVisible());
                        apply.clickable = ((!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() != 0);
                    }
                    if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                        energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
                        apply.clickable = ((!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() != 0);
                    }

                    for (NormalButton button : buttons) {
                        if (button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                            if (button.id == 4) {
                                PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.DELETE_NETWORK, PacketGeneralHandler.getDeleteNetworkPacket(tileEntity.getNetworkID())));
                            }
                            if (button.id == 3) {
                                PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.EDIT_NETWORK, PacketGeneralHandler.getNetworkEditPacket(network.getNetworkID(), name.getText(), color.color, securityType, energyType, password.getText())));
                            }
                        }
                    }
                }
                for (ColorButton button : colorButtons) {
                    if (button.isMouseHovered(mc, mouseX, mouseY)) {
                        color.selected = false;
                        color = button;
                        color.selected = true;
                        apply.clickable = ((!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() != 0);
                        if (mouseButton == 1) {
                            main = false;
                            initPopGui();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mousePopupClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 11) {
                        main = true;
                    }
                    if(button.id == 12) {
                        color.color = customColor.getIntegerFromHex();
                        main = true;
                    }
                }
            }
            if(main) {
                backToMain();
            }
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(textBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                FMLCommonHandler.instance().showGuiScreen(new GuiFluxHome(player, tileEntity));
                if(FluxConfig.enableButtonSound)
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(RegistrySounds.BUTTON_CLICK, 1.0F));
            }
        }
        for(TextboxButton text : textBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                apply.clickable = (!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() !=0;
            }
        }
        if(k == 42) {
            deleteCount++;
            if(deleteCount > 1) {
                delete.clickable = true;
            }
        } else {
            deleteCount = 0;
            delete.clickable = false;
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                backToMain();
            }
        }
        for(TextboxButton text : popBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                colorApply.clickable = text.getText().length() == 6;
            }
        }
    }

    private void initPopGui() {
        popBoxes.clear();
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 86, 36, 12, 11));
        colorApply = new NormalButton(FluxTranslate.APPLY.t(), 100, 86, 36, 12, 12);
        popButtons.add(colorApply);

        customColor = TextboxButton.create(this, "0x", 7, fontRenderer, 57, 64, 64, 12).setHexOnly();
        customColor.setMaxStringLength(6);
        customColor.setText(Integer.toHexString(color.color));

        popBoxes.add(customColor);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS) {
            FMLCommonHandler.instance().showGuiScreen(new GuiFluxHome(player, tileEntity));
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
        }
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS_2) {
            apply.clickable = false;
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
        }
    }
}
