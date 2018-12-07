package sonar.flux.client.gui.tabs;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.SonarHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.EnumActivationType;
import sonar.flux.api.EnumPriorityType;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.GuiTabAbstractGrid;
import sonar.flux.client.gui.buttons.CheckBox;
import sonar.flux.client.gui.buttons.FluxTextField;
import sonar.flux.client.gui.buttons.PriorityButton;
import sonar.flux.client.gui.buttons.RedstoneSignalButton;
import sonar.flux.network.PacketEditedTiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabIndexSingleEditing<G> extends GuiTabAbstractGrid<G> {

    public ClientFlux flux;

    public SonarTextField fluxName, priority, limit;

	public GuiTabIndexSingleEditing(GuiTabAbstract origin, ClientFlux flux, List<EnumGuiTab> tabs) {
		super(tabs);
        this.flux = flux;
        this.setOrigin(origin);
	}
	
    @Override
    public void initGui() {
    	super.initGui();
        int networkColour = NETWORK_COLOUR.getValue(common).getRGB();
        priority = FluxTextField.create(FluxTranslate.PRIORITY.t() + ": ", 0, getFontRenderer(), 8, 46, 147, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        priority.setMaxStringLength(8);
        priority.setText(String.valueOf(flux.priority));

        limit = FluxTextField.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", 1, getFontRenderer(), 8, 46+18, 147, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(flux.limit));

        fluxName = FluxTextField.create(FluxTranslate.NAME.t() + ": ", 2, getFontRenderer(), 8, 28, 147, 12).setBoxOutlineColour(networkColour);
        fluxName.setMaxStringLength(24);
        fluxName.setText(flux.getCustomName());
           
        fieldList.addAll(Lists.newArrayList(priority, limit, fluxName));
        buttonList.add(new CheckBox(this, 3, getGuiLeft() + 156, getGuiTop() + 64, () -> !flux.disableLimit, FluxTranslate.ENABLE_LIMIT.t()));
        buttonList.add(new RedstoneSignalButton( this, 4, getGuiLeft() + 156, getGuiTop() + 28, () -> flux.activation_type, ""));
        buttonList.add(new PriorityButton(this, 5, getGuiLeft() + 156, getGuiTop() + 46, () -> flux.priority_type, ""));
    }

    @Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
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
            flux.disableLimit = !flux.disableLimit;
            return;
        }
        if(button.id == 4){
            flux.activation_type = SonarHelper.incrementEnum(flux.activation_type, EnumActivationType.values());
            return;
        }
        if(button.id == 5){
            flux.priority_type = SonarHelper.incrementEnum(flux.priority_type, EnumPriorityType.values());
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
            flux.priority = priority.getIntegerFromText();
        } else if (field == limit) {
            flux.limit = limit.getLongFromText();
        } else if (field == fluxName) {
            flux.customName = fluxName.getText();
        }
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (isCloseKey(keyCode)) {
            FluxNetworks.network.sendToServer(new PacketEditedTiles(Lists.newArrayList(flux)));
        }
        super.keyTyped(typedChar, keyCode);
    }

	@Override
	public ResourceLocation getBackground() {
		return scroller_flux_gui;
	}

	@Override
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.CONNECTIONS;
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
