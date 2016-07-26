package sonar.flux.client;

import net.minecraft.util.ResourceLocation;


public enum GuiState {
	INDEX(176, 166), NETWORK_SELECT(176, 166), NETWORK_STATS(176, 166), NETWORK_CREATE(176, 166), NETWORK_EDIT(176, 166), CONNECTIONS(176, 166);
	int x, y;

	GuiState(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public ResourceLocation getBackground() {
		if (this == NETWORK_SELECT || this == CONNECTIONS)
			return GuiFlux.select;
		return GuiFlux.bground;
	}

	public boolean hasScrollBar() {
		return (this == NETWORK_SELECT || this == CONNECTIONS);
	}
}