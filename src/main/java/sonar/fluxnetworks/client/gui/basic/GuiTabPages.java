package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.button.PageLabelButton;
import sonar.fluxnetworks.api.network.NetworkSettings;

import java.util.List;

/**for tabs which have multiple pages: e.g. Network Selection, Network Connections */
public abstract class GuiTabPages<T> extends GuiTabCore {

    public List<T> elements = Lists.newArrayList();
    protected List<T> current = Lists.newArrayList();
    protected SortType sortType = SortType.ID;
    protected PageLabelButton labelButton;
    private boolean init;

    public int page = 1, currentPages = 1, pages = 1, gridPerPage = 1, gridStartX = 0, gridStartY = 0, gridHeight = 0, elementHeight = 0, elementWidth = 0;

    public GuiTabPages(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if(pages > 1) {
            labelButton.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop);
        }
        int i = 0;
        for(T s : current) {
            int y = (gridStartY + gridHeight * i);
            renderElement(matrixStack, s, gridStartX, y);
            i++;
        }
        i = 0;
        for(T s : current) {
            int y = (gridStartY + gridHeight * i);
            if(mouseX >= gridStartX + guiLeft && mouseY >= y + guiTop && mouseX < (gridStartX + elementWidth) + guiLeft && mouseY < y + elementHeight + guiTop) {
                renderElementTooltip(matrixStack, s, mouseX - guiLeft, mouseY - guiTop);
            }
            i++;
        }
        /*if(pages > 1) {
            drawCenteredString(fontRenderer, page + " / " + pages, 89, 156, 0xffffff);
        }*/
    }

    @Override
    protected void drawBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
    }

    public T getHoveredElement(int mouseX, int mouseY) {
        if(current.isEmpty())
            return null;
        for(int i = 0; i < currentPages; i++) {
            int y = (gridStartY + gridHeight * i);
            if(mouseX >= gridStartX && mouseY >= y && mouseX < (gridStartX + elementWidth) && mouseY < y + elementHeight) {
                if(current.get(i) != null) {
                    return current.get(i);
                }
            }
        }
        /*int p = (mouseY - gridStartY) / gridHeight;
        if(mouseX >= gridStartX && mouseX < (gridStartX + elementWidth) && mouseY < (p * gridHeight + elementHeight)) {
            if(current.get(p) != null) {
                return (T) current.get(p);
            }
        }*/
        return null;
    }

    protected abstract void onElementClicked(T element, int mouseButton);

    @Override
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        super.mouseClickedMain(mouseX, mouseY, mouseButton);
        T e = getHoveredElement((int)mouseX - guiLeft, (int)mouseY - guiTop);
        if(e != null) {
            onElementClicked(e, mouseButton);
            return true;
        }
        if(pages > 1 && labelButton.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
            if(page != labelButton.hoveredPage) {
                page = Math.max(labelButton.hoveredPage, 1);
                refreshCurrentPage();
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        super.init();
        labelButton = new PageLabelButton(14, 157, page, pages, network.getSetting(NetworkSettings.NETWORK_COLOR), guiLeft, guiTop);
    }

    @Override
    public boolean mouseScrolledMain(double mouseX, double mouseY, double scroll) {
        if(scroll == -1 && page < pages) {
            page++;
            refreshCurrentPage();
            return true;
        } else if (scroll == 1 && page > 1) {
            page--;
            refreshCurrentPage();
            return true;
        }
        return super.mouseScrolledMain(mouseX, mouseY, scroll);
    }

    public abstract void renderElement(MatrixStack matrixStack, T element, int x, int y);

    public abstract void renderElementTooltip(MatrixStack matrixStack, T element, int mouseX, int mouseY);

    protected void refreshPages(List<T> elements) {
        this.elements = elements;
        pages = (int) Math.ceil(elements.size() / (double) gridPerPage);
        sortGrids(sortType);
        if(!init) {
            refreshCurrentPage();
            init = true;
        } else {
            refreshCurrentPageInternal();
        }
    }

    protected void refreshCurrentPage() {
        /*if(elements.size() == 0)
            return;

        current.clear();
        int a = (page - 1) * gridPerPage;
        int b = Math.min(elements.size(), page * gridPerPage);
        currentPages = b - a;
        if(page == pages) {
            for(int i = (page - 1) * gridPerPage; i < elements.size(); i++) {
                current.add(elements.get(i));
            }
        } else {
            for (int i = (page - 1) * gridPerPage; i < page * gridPerPage; i++) {
                current.add(elements.get(i));
            }
        }
        for(int i = a; i < b; i++) {
            current.add(elements.get(i));
        }*/
        refreshCurrentPageInternal();
        labelButton.refreshPages(page, pages);
    }

    protected void refreshCurrentPageInternal() {
        if(elements.size() == 0)
            return;

        current.clear();
        int a = (page - 1) * gridPerPage;
        int b = Math.min(elements.size(), page * gridPerPage);
        currentPages = b - a;

        for(int i = a; i < b; i++) {
            current.add(elements.get(i));
        }
    }

    protected void sortGrids(SortType sortType) {

    }

    public enum SortType {
        ID(FluxTranslate.SORTING_ID),
        NAME(FluxTranslate.SORTING_NAME);

        private FluxTranslate name;

        SortType(FluxTranslate name) {
            this.name = name;
        }

        public String getTranslatedName(){
            return name.t();
        }

    }

}
