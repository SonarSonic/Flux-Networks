package sonar.flux.api.tiles;

import sonar.flux.FluxConfig;

/** implemented by the Flux Controller TileEntity */
public interface IFluxController extends IFluxPoint {

	public enum PriorityMode {
		DEFAULT, LARGEST, SMALLEST;

		public String getName() {
			switch (this) {
			case DEFAULT:
				return "network.default";
			case LARGEST:
				return "network.largest";
			case SMALLEST:
				return "network.smallest";
			}
			return "";
		}
	}

	public enum TransferMode {
		DEFAULT(1), EVEN(1), SURGE(1), HYPER(FluxConfig.hyper), GOD(FluxConfig.god);

		public int repeat;

		TransferMode(int repeat) {
			this.repeat = repeat;
		}

		public String getName() {
			switch (this) {
			case DEFAULT:
				return "network.notransfer";
			case EVEN:
				return "network.transfer.even";
			case SURGE:
				return "network.transfer.surge";
			case HYPER:
				return "network.transfer.hyper";
			case GOD:
				return "network.transfer.god";
			}
			return "";
		}

		public boolean isBanned() {
			if (this == GOD) {
				return FluxConfig.banGod;
			}
			if (this == HYPER) {
				return FluxConfig.banHyper;
			}
			return false;
		}

	}

	public enum TransmitterMode {
		OFF, ON, HOTBAR, HELD_ITEM;

		public String getName() {
			switch (this) {
			case OFF:
				return "network.off";
			case ON:
				return "network.on";
			case HOTBAR:
				return "network.hotbar";
			case HELD_ITEM:
				return "network.held";
			}
			return "";
		}
	}

	/***/
	public PriorityMode getReceiveMode();

	public PriorityMode getSendMode();

	public TransmitterMode getTransmitterMode();

	public TransferMode getTransferMode();

}
