package sonar.flux.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.flux.api.FluxListener;
import sonar.flux.client.states.*;

public abstract class GuiState {
    public static final GuiStateIndex INDEX = new GuiStateIndex();
    public static final GuiStateNetworkSelect NETWORK_SELECT = new GuiStateNetworkSelect();
    public static final GuiStateNetworkConnections CONNECTIONS = new GuiStateNetworkConnections();
    public static final GuiStateNetworkStats NETWORK_STATS = new GuiStateNetworkStats();
    public static final GuiStateNetworkEdit NETWORK_EDIT = new GuiStateNetworkEdit();
    public static final GuiStateNetworkPlayers PLAYERS = new GuiStateNetworkPlayers();
    public static final GuiStateNetworkCreate NETWORK_CREATE = new GuiStateNetworkCreate();
    public static final GuiState[] VALUES = new GuiState[]{INDEX, NETWORK_SELECT, CONNECTIONS, NETWORK_STATS, NETWORK_EDIT, PLAYERS, NETWORK_CREATE};

    public int x, y, texX;
    public String client;
    public GuiTypeMessage type;

    public GuiState(GuiTypeMessage type, int x, int y, int texX, String client) {
        this.type = type;
		this.x = x;
		this.y = y;
        this.texX = texX;
        this.client = client;
    }

    public abstract void draw(GuiFlux flux, int x, int y);

    public abstract void init(GuiFlux flux);

    public abstract void button(GuiFlux flux, GuiButton button);

    public abstract void click(GuiFlux flux, int x, int y, int mouseButton);

    public abstract SonarTextField[] getFields(GuiFlux flux);

    public boolean type(GuiFlux flux, char c, int i) {
        return true;
	}

    public SonarScroller[] getScrollers() {
        return new SonarScroller[0];
	}

    public boolean needsScrollBars() {
        return true;
		}

    public abstract int getSelectionSize(GuiFlux flux);

    public void textboxKeyTyped(GuiFlux flux, SonarTextField field, char c, int i) {
    }

    public FluxListener getViewingType() {
        return type.getViewingType();
	}

	public String getClientName() {
        return client;
    }

    public ResourceLocation getBackground() {
        if (this == GuiState.CONNECTIONS)
            return GuiFluxBase.connections;
        if (this == GuiState.NETWORK_SELECT || this == GuiState.PLAYERS)
            return GuiFluxBase.select;
        return GuiFluxBase.bground;
    }

    public int ordinal() {
        int i = 0;
        for (GuiState state : VALUES) {
            if (state == this)
                return i;
            i++;
        }
        return 0;
    }

    public static GuiState[] values() {
        return VALUES;
	}
}