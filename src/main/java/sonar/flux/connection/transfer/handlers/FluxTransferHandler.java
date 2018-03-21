package sonar.flux.connection.transfer.handlers;

import java.util.List;

import sonar.flux.api.tiles.IFlux;

public abstract class FluxTransferHandler<T extends IFlux> extends BaseTransferHandler {

	public final T flux;

	public FluxTransferHandler(T flux) {
		this.flux = flux;
	}

	@Override
	public long getMaxRemove() {
		return flux.getTransferLimit();
	}

	@Override
	public long getMaxAdd() {
		return flux.getTransferLimit();
	}
}
