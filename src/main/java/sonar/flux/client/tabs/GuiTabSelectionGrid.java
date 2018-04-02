package sonar.flux.client.tabs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.IGridGui;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.common.tileentity.TileFlux;

public abstract class GuiTabSelectionGrid<T extends TileFlux, G> extends AbstractGuiTab<T> implements IGridGui<G> {

	public Map<SelectionGrid, SonarScroller> grids = new HashMap<>();

	public GuiTabSelectionGrid(T tile, List tabs) {
		super(tile, tabs);
	}

	public abstract List getGridList(int gridID);

	@Override
	public void initGui() {
		super.initGui();
		Map<SelectionGrid, SonarScroller> newgrids = new HashMap<>();
		addGrids(newgrids);
		grids = newgrids;

	}

	public abstract void addGrids(Map<SelectionGrid, SonarScroller> grids);

	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if (button == 0 || button == 1) {
			grids.forEach((grid, scroll) -> grid.mouseClicked(this, x, y, button));
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		for (Entry<SelectionGrid, SonarScroller> entry : grids.entrySet()) {
			if (entry.getKey().isScrollable())
				renderScroller(entry.getValue());
			entry.getKey().renderGrid(this, x, y);
		}
	}

	public void renderScroller(SonarScroller scroller) {
		drawRect(scroller.left, scroller.top, scroller.left + scroller.width, scroller.top + scroller.length, grey);
		drawRect(scroller.left + 1, scroller.top + 1, scroller.left + scroller.width - 1, scroller.top + scroller.length - 1, black);
		GlStateManager.color(1, 1, 1, 1);
		bindTexture(this.getBackground());
		drawTexturedModalRect(scroller.left, scroller.top + (int) ((float) (scroller.length - 17) * scroller.getCurrentScroll()), 176, 0, 10, 15);
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public void startToolTipRender(int gridID, G selection, int x, int y) {
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		renderElementToolTip(gridID, selection, x, y);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		grids.forEach((grid, scroll) -> scroll.handleMouse(grid));
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		for (Entry<SelectionGrid, SonarScroller> entry : grids.entrySet()) {
			entry.getKey().setList(Lists.newArrayList(getGridList(entry.getKey().gridID)));
			entry.getValue().drawScreen(x - guiLeft, y - guiTop, entry.getKey().isScrollable());
		}
	}

	@Override
	public float getCurrentScroll(SelectionGrid gridID) {
		return grids.get(gridID).getCurrentScroll();
	}

	@Override
	public ResourceLocation getBackground() {
		return scroller_flux_gui;
	}
}
