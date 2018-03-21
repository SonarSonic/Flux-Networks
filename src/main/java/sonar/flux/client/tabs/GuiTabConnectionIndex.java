package sonar.flux.client.tabs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.client.CheckBox;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;

public class GuiTabConnectionIndex<T extends TileFlux, G> extends GuiTabSelectionGrid<T, G> {

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
        priority = new SonarTextField(0, getFontRenderer(), 50, 46, 118, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        priority.setMaxStringLength(8);
        priority.setText(String.valueOf(flux.getCurrentPriority()));

        limit = new SonarTextField(1, getFontRenderer(), 83, 46+18, 72, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(flux.limit.getObject()));

        fluxName = new SonarTextField(2, getFontRenderer(), 38, 28, 130, 12).setBoxOutlineColour(networkColour);
        fluxName.setMaxStringLength(24);
        fluxName.setText(flux.getCustomName());
           
        fieldList.addAll(Lists.newArrayList(priority, limit, fluxName));
        buttonList.add(new CheckBox(this, 3, getGuiLeft() + 156, getGuiTop() + 64, !flux.disableLimit.getObject(), "Enable Limit: " + !flux.disableLimit.getObject()));     
    }	

    @Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = common.getNetworkColour().getRGB();
		FontHelper.text(GUI.NETWORK_NAME + ": ", 7, 30, colour);
		FontHelper.text(GUI.PRIORITY + ":", 7, 48, colour);
		FontHelper.text(GUI.TRANSFER_LIMIT + ":", 7, 48+18, colour);
		//FontHelper.text(GUI.IGNORE_LIMIT + ": " + TextFormatting.WHITE + flux.disableLimit.getObject().toString(), 7, 48 + 18, colour);
		renderNetwork(common.getNetworkName(), common.getAccessType(), common.getNetworkColour().getRGB(), true, 11, 8);
	}
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	super.actionPerformed(button);
        if (button.id == 0) {
        	switchTab(GuiTab.NETWORK_SELECT);
            return;
        }
        if(button.id == 3){
            flux.disableLimit.invert();
            SonarCore.sendPacketToServer(flux, -1);
            this.doReset();
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
		if (x - getGuiLeft() > 5 && x - getGuiLeft() < 165 && y - getGuiTop() > 10 && y - getGuiTop() < 20) {
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
		return scroller_flux_gui;
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.INDEX;
	}

	//// GRIDS \\\\
	
	@Override
	public void onGridClicked(int gridID, G element, int x, int y, int pos, int button, boolean empty) {}

	@Override
	public void renderGridElement(int gridID, G element, int x, int y, int slot) {}

	@Override
	public void renderElementToolTip(int gridID, G element, int x, int y) {}

	@Override
	public List getGridList(int gridID) {
		return new ArrayList<>();
	}

	@Override
	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {}

}
