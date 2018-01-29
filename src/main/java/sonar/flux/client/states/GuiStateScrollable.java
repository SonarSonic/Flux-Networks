package sonar.flux.client.states;

import java.util.List;

import com.google.common.collect.Lists;

import sonar.core.client.gui.GuiGridElement;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;

public abstract class GuiStateScrollable<T> extends GuiState {

    public List<ElementList<T>> lists = Lists.newArrayList();

    public GuiStateScrollable(GuiTypeMessage type, int x, int y, int texX, String client) {
        super(type, x, y, texX, client);
    }

    public float getCurrentScroll(int gridID) {
        return 0;
    }

    public void onGridClicked(int gridID, T selection, int pos, int button, boolean empty) {
    }

    public void renderGridElement(int gridID, T selection, int x, int y, int slot) {
    }

    public void renderElementToolTip(int gridID, T selection, int x, int y) {
    }


    public static class ElementList<T> extends GuiGridElement<T> {
        public final GuiStateScrollable<T> gui;

        public ElementList(GuiStateScrollable gui, int gridID, int xPos, int yPos, int eWidth, int eHeight, int gWidth, int gHeight) {
            super(gridID, xPos, yPos, eWidth, eHeight, gWidth, gHeight);
            this.gui = gui;
        }

        @Override
        public float getCurrentScroll() {
            return gui.getCurrentScroll(gridID);
        }

        @Override
        public void onGridClicked(T selection, int pos, int button, boolean empty) {
            gui.onGridClicked(button, selection, pos, button, empty);
        }

        @Override
        public void renderGridElement(T selection, int x, int y, int slot) {
            gui.renderGridElement(slot, selection, x, y, slot);
        }

        @Override
        public void renderElementToolTip(T selection, int x, int y) {
            gui.renderElementToolTip(y, selection, x, y);
        }

    }

}