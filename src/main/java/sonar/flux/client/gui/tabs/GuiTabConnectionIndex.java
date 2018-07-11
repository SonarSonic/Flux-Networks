package sonar.flux.client.gui.tabs;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.core.sync.SyncValueHandler;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.client.gui.buttons.CheckBox;
import sonar.flux.client.gui.buttons.PriorityButton;
import sonar.flux.client.gui.buttons.RedstoneSignalButton;
import sonar.flux.common.tileentity.TileFlux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sonar.flux.connection.NetworkSettings.*;

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
        int networkColour = NETWORK_COLOUR.getValue(common).getRGB();
        priority = new SonarTextField(0, getFontRenderer(), 50, 46, 105, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        priority.setMaxStringLength(8);
        priority.setText(String.valueOf(flux.priority.getValue()));

        limit = new SonarTextField(1, getFontRenderer(), 83, 46+18, 72, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(flux.limit.getValue()));

        fluxName = new SonarTextField(2, getFontRenderer(), 38, 28, 117, 12).setBoxOutlineColour(networkColour);
        fluxName.setMaxStringLength(24);
        fluxName.setText(flux.getCustomName());
           
        fieldList.addAll(Lists.newArrayList(priority, limit, fluxName));
        buttonList.add(new CheckBox(this, 3, getGuiLeft() + 156, getGuiTop() + 64, () -> !flux.disableLimit.getValue(), FluxTranslate.ENABLE_LIMIT.t()));
        buttonList.add(new RedstoneSignalButton(this, 4, getGuiLeft() + 156, getGuiTop() + 28, () -> flux.activation_type.getValue(), ""));
        buttonList.add(new PriorityButton(this, 5, getGuiLeft() + 156, getGuiTop() + 46, () -> flux.priority_type.getValue(), ""));

    }

    @Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = NETWORK_COLOUR.getValue(common).getRGB();
		FontHelper.text(FluxTranslate.NAME.t() + ": ", 7, 30, colour);
		FontHelper.text(FluxTranslate.PRIORITY.t() + ":", 7, 48, colour);
		FontHelper.text(FluxTranslate.TRANSFER_LIMIT.t() + ":", 7, 48+18, colour);
		renderNetwork(NETWORK_NAME.getValue(common), NETWORK_ACCESS.getValue(common), NETWORK_COLOUR.getValue(common).getRGB(), true, 11, 8);
	}
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	super.actionPerformed(button);
        if (button.id == 0) {
        	switchTab(GuiTab.NETWORK_SELECTION);
            return;
        }
        if(button.id == 3){
            SyncValueHandler.invertBoolean(flux.disableLimit);
            SonarCore.sendPacketToServer(flux, -1);
            return;
        }
        if(button.id == 4){
            SyncValueHandler.incrementEnum(flux.activation_type);
            SonarCore.sendPacketToServer(flux, 12);
            return;
        }
        if(button.id == 5){
            SyncValueHandler.incrementEnum(flux.priority_type);
            SonarCore.sendPacketToServer(flux, 16);
            return;
        }
    }

    @Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (x - getGuiLeft() > 5 && x - getGuiLeft() < 165 && y - getGuiTop() > 10 && y - getGuiTop() < 20) {
        	switchTab(GuiTab.NETWORK_SELECTION);
        }
    }
    
    @Override
    public void onTextFieldChanged(SonarTextField field) {
    	super.onTextFieldChanged(field);
        if (field == priority) {
            flux.priority.setValueInternal(priority.getIntegerFromText());
            SonarCore.sendPacketToServer(flux, 1);
        } else if (field == limit) {
            flux.limit.setValueInternal(limit.getLongFromText());
            SonarCore.sendPacketToServer(flux, 2);
        } else if (field == fluxName) {
            flux.customName.setValueInternal(fluxName.getText());
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
