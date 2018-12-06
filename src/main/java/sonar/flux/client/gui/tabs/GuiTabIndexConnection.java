package sonar.flux.client.gui.tabs;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.sync.SyncValueHandler;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstractGrid;
import sonar.flux.client.gui.buttons.CheckBox;
import sonar.flux.client.gui.buttons.FluxTextField;
import sonar.flux.client.gui.buttons.PriorityButton;
import sonar.flux.client.gui.buttons.RedstoneSignalButton;
import sonar.flux.common.tileentity.TileFlux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabIndexConnection<G> extends GuiTabAbstractGrid<G> {

    public TileFlux flux;

    public SonarTextField fluxName, priority, limit;
        
	public GuiTabIndexConnection(List<EnumGuiTab> tabs) {
		super(tabs);
        flux = FluxNetworks.proxy.getFluxTile();
	}
	
    @Override
    public void initGui() {
    	super.initGui();
        int networkColour = NETWORK_COLOUR.getValue(common).getRGB();
        priority = FluxTextField.create(FluxTranslate.PRIORITY.t() + ": ", 0, getFontRenderer(), 8, 46, 147, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        priority.setMaxStringLength(8);
        priority.setText(String.valueOf(flux.priority.getValue()));

        limit = FluxTextField.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", 1, getFontRenderer(), 8, 46+18, 147, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(flux.limit.getValue()));

        fluxName = FluxTextField.create(FluxTranslate.NAME.t() + ": ", 2, getFontRenderer(), 8, 28, 147, 12).setBoxOutlineColour(networkColour);
        fluxName.setMaxStringLength(24);
        fluxName.setText(flux.getCustomName());
           
        fieldList.addAll(Lists.newArrayList(priority, limit, fluxName));
        buttonList.add(new CheckBox(this, 3, getGuiLeft() + 156, getGuiTop() + 64, () -> !flux.disableLimit.getValue(), FluxTranslate.ENABLE_LIMIT.t()));
        buttonList.add(new RedstoneSignalButton( this, 4, getGuiLeft() + 156, getGuiTop() + 28, () -> flux.activation_type.getValue(), ""));
        buttonList.add(new PriorityButton(this, 5, getGuiLeft() + 156, getGuiTop() + 46, () -> flux.priority_type.getValue(), ""));

    }

    @Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = NETWORK_COLOUR.getValue(common).getRGB();
		//FontHelper.text(FluxTranslate.NAME.t() + ": ", 7, 30, colour);
		//FontHelper.text(FluxTranslate.PRIORITY.t() + ":", 7, 48, colour);
		//FontHelper.text(FluxTranslate.TRANSFER_LIMIT.t() + ":", 7, 48+18, colour);
		renderNetwork(NETWORK_NAME.getValue(common), NETWORK_ACCESS.getValue(common), NETWORK_COLOUR.getValue(common).getRGB(), true, 11, 8);
	}
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	super.actionPerformed(button);
        if (button.id == 0) {
        	switchTab(EnumGuiTab.NETWORK_SELECTION);
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
        	switchTab(EnumGuiTab.NETWORK_SELECTION);
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
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.INDEX;
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
