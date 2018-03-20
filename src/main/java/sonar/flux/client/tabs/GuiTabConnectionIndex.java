package sonar.flux.client.tabs;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import sonar.core.SonarCore;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;

public class GuiTabConnectionIndex<T extends TileFlux> extends AbstractGuiTab<T> {

    public SonarTextField fluxName;
    public SonarTextField priority;
    public SonarTextField limit;
        
	public GuiTabConnectionIndex(T tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}
	
    @Override
    public void initGui() {
    	super.initGui();
        int networkColour = common.getNetworkColour().getRGB();
        priority = new SonarTextField(0, getFontRenderer(), 50, 46, 30, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        priority.setMaxStringLength(3);
        priority.setText(String.valueOf(flux.getCurrentPriority()));

        limit = new SonarTextField(1, getFontRenderer(), 110, 46, 58, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(flux.limit.getObject()));

        fluxName = new SonarTextField(1, getFontRenderer(), 38, 28, 130, 12).setBoxOutlineColour(networkColour);
        fluxName.setMaxStringLength(24);
        fluxName.setText(flux.getCustomName());
        
        fieldList.addAll(Lists.newArrayList(priority, limit, fluxName));
    }	

    @Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = common.getNetworkColour().getRGB();
		FontHelper.text(GUI.NETWORK_NAME + ": ", 7, 30, colour);
		FontHelper.text(GUI.PRIORITY + ":", 7, 48, colour);
		FontHelper.text(GUI.MAX + ":", 87, 48, colour);
		FontHelper.text(GUI.IGNORE_LIMIT + ": " + TextFormatting.WHITE + flux.disableLimit.getObject().toString(), 7, 48 + 18, colour);
		renderNetwork(common.getNetworkName(), common.getAccessType(), common.getNetworkColour().getRGB(), true, 11, 8);
	}
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	super.actionPerformed(button);
        if (button.id == 0) {
        	switchTab(GuiTab.NETWORK_SELECT);
            return;
        }
        if (button.id == 5) {
        	switchTab(GuiTab.CONNECTIONS);
        	return;
        }
    }

    @Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
        if (x - getGuiLeft() > 5 && x - getGuiLeft() < 165 && y - getGuiTop() > 66 && y - getGuiTop() < 80) {
            flux.disableLimit.invert();
            SonarCore.sendPacketToServer(flux, -1);
        } else if (x - getGuiLeft() > 5 && x - getGuiLeft() < 165 && y - getGuiTop() > 10 && y - getGuiTop() < 20) {
        	switchTab(GuiTab.NETWORK_SELECT);
        }
    }
    
    @Override
    public void onTextFieldChanged(SonarTextField field) {
    	super.onTextFieldChanged(field);
        if (field == priority) {
            flux.priority.setObject(priority.getIntegerFromText());
            SonarCore.sendPacketToServer(flux, 1);
        } else if (field == limit) {
            flux.limit.setObject(limit.getLongFromText());
            SonarCore.sendPacketToServer(flux, 2);
        } else if (field == fluxName) {
            flux.customName.setObject(fluxName.getText());
            SonarCore.sendPacketToServer(flux, 3);
        }
    }

	@Override
	public ResourceLocation getBackground() {
		return blank_flux_gui;
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.INDEX;
	}

}
