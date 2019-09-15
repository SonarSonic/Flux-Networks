package fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import fluxnetworks.FluxConfig;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.ConnectionType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.registry.RegistrySounds;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.scale;

public abstract class GuiFluxCore extends GuiCore {

    public IFluxNetwork network;
    protected boolean networkValid;
    private int timer1;

    public GuiFluxCore(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        this.network = FluxNetworkCache.instance.getClientNetwork(tileEntity.networkID);
        this.networkValid = !network.isInvalid();
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        super.drawPopupForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NavigationButton button : navigationButtons) {
                if(button.isMouseHovered(mc, mouseX, mouseY)) {
                    button.switchTab(button.buttonNavigationId, player, tileEntity);
                    if(FluxConfig.enableButtonSound)
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(RegistrySounds.BUTTON_CLICK, 1.0F));
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer1 == 0) {
            this.network = FluxNetworkCache.instance.getClientNetwork(tileEntity.networkID);
            this.networkValid = !network.isInvalid();
        }
        timer1++;
        timer1 %= 20;
    }

    @Override
    protected void drawFluxDefaultBackground() {
        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        float f = (float)(network.getSetting(NetworkSettings.NETWORK_COLOR) >> 16 & 255) / 255.0F;
        float f1 = (float)(network.getSetting(NetworkSettings.NETWORK_COLOR) >> 8 & 255) / 255.0F;
        float f2 = (float)(network.getSetting(NetworkSettings.NETWORK_COLOR) & 255) / 255.0F;
        GlStateManager.color(f, f1, f2);
        mc.getTextureManager().bindTexture(FRAME);
        this.drawTexturedModalRect(width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        GlStateManager.popMatrix();
    }

    protected void renderNetwork(String name, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        GlStateManager.color(f, f1, f2);

        mc.getTextureManager().bindTexture(GUI_BAR);
        drawTexturedModalRect(x, y, 0, 0, 135, 12);
        fontRenderer.drawString(name, x + 4, y + 2, 0xffffff);

        GlStateManager.popMatrix();
    }

    protected void renderTransfer(ITransferHandler handler, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);

        fontRenderer.drawString(getTransferInfo(tileEntity.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), handler.getChange()), x, y, color);
        fontRenderer.drawString(FluxTranslate.BUFFER.t() + ": " + TextFormatting.BLUE + FluxUtils.format(handler.getBuffer(), FluxUtils.TypeNumberFormat.COMMAS, network.getSetting(NetworkSettings.NETWORK_ENERGY), false), x, y + 10, 0xffffff);

        renderItemStack(tileEntity.getDisplayStack(), x - 20, y + 1);

        GlStateManager.popMatrix();
    }

    protected void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.enableDepth();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;

        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, "");
        RenderHelper.disableStandardItemLighting();

        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
        GlStateManager.disableDepth();
    }

    protected List<String> getFluxInfo(IFluxConnector flux) {
        List<String> list = Lists.newArrayList();
        list.add(TextFormatting.BOLD + flux.getCustomName());
        NBTTagCompound tag = flux.getDisplayStack().getSubCompound(FluxUtils.FLUX_DATA);
        if(flux.isChunkLoaded()) {
            if(flux.isForcedLoading()) {
                list.add(TextFormatting.AQUA + FluxTranslate.FORCED_LOADING.t());
            }
            list.add(getTransferInfo(flux.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), flux.getChange()));
            if(flux.getConnectionType() == ConnectionType.STORAGE) {
                list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            } else {
                list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
            }
        } else {
            list.add(TextFormatting.RED + FluxTranslate.CHUNK_UNLOADED.t());
            if(tag != null) {
                if (tag.hasKey("energy")) {
                    list.add(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getInteger("energy")) + "RF");
                } else {
                    list.add(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.BLUE + NumberFormat.getInstance().format(tag.getLong("buffer")) + "RF");
                }
            }
        }

        list.add(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ? FluxTranslate.UNLIMITED.t() : flux.getCurrentLimit()));
        list.add(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ? FluxTranslate.SURGE.t() : flux.getPriority()));
        list.add(TextFormatting.ITALIC + flux.getCoords().getStringInfo());
        return list;
    }

    protected String getTransferInfo(ConnectionType type, EnergyType energyType, long change) {
        if(type.canAddEnergy()) {
            String b = FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            if(change == 0) {
                return FluxTranslate.INPUT.t() + ": " + TextFormatting.GOLD + b;
            } else {
                return FluxTranslate.INPUT.t() + ": " + TextFormatting.GREEN + "+" + b;
            }
        }
        if(type.canRemoveEnergy() || type.isController()) {
            String b = FluxUtils.format(-change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            if(change == 0) {
                return FluxTranslate.OUTPUT.t() + ": " + TextFormatting.GOLD + b;
            } else {
                return FluxTranslate.OUTPUT.t() + ": " + TextFormatting.RED + "-" + b;
            }
        }
        // Storage are inverted
        if(type == ConnectionType.STORAGE) {
            if(change == 0) {
                return FluxTranslate.CHANGE.t() + ": " + TextFormatting.GOLD + change + energyType.getUsageSuffix();
            } else if(change > 0) {
                return FluxTranslate.CHANGE.t() + ": " + TextFormatting.RED + "-" + FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            } else {
                return FluxTranslate.CHANGE.t() + ": " + TextFormatting.GREEN + "+" + FluxUtils.format(-change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            }
        }
        return "";
    }

    protected void renderNavigationPrompt(String error, String prompt) {
        GlStateManager.pushMatrix();
        drawCenteredString(fontRenderer, error, xSize / 2, 16, 0x808080);
        GlStateManager.scale(0.625, 0.625, 0.625);
        drawCenteredString(fontRenderer, FluxTranslate.CLICK.t() + TextFormatting.AQUA + ' ' + prompt + ' ' + TextFormatting.RESET + FluxTranslate.ABOVE.t(), (int) (xSize / 2 * 1.6), (int) (26 * 1.6), 0x808080);
        GlStateManager.scale(1.6, 1.6, 1.6);
        GlStateManager.popMatrix();
    }
}
