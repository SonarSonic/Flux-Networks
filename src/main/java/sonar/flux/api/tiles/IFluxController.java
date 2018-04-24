package sonar.flux.api.tiles;

import sonar.core.translate.Localisation;
import sonar.flux.FluxConfig;
import sonar.flux.FluxTranslate;

/**
 * implemented by the Flux Controller TileEntity
 */
public interface IFluxController extends IFluxPoint {

    enum PriorityMode {
        DEFAULT(FluxTranslate.PRIORITY_DEFAULT), //
        LARGEST(FluxTranslate.PRIORITY_LARGEST), //
        SMALLEST(FluxTranslate.PRIORITY_SMALLEST);//

    	Localisation message;

    	PriorityMode(Localisation message) {
    		this.message = message;
    	}

    	public String getDisplayName() {
    		return message.t();
    	}
    }

    enum TransferMode {
        DEFAULT(FluxTranslate.TRANSFER_NONE,1), //
        EVEN(FluxTranslate.TRANSFER_EVEN, 1), //
        SURGE(FluxTranslate.TRANSFER_SURGE, 1), //
        HYPER(FluxTranslate.TRANSFER_HYPER, FluxConfig.hyper), //
        GOD(FluxTranslate.TRANSFER_GOD, FluxConfig.god);//

        int repeat;
    	Localisation message;

    	TransferMode(Localisation message, int repeat) {
    		this.message = message;
            this.repeat = repeat;
    	}

    	public String getDisplayName() {
    		return message.t();
    	}

        public boolean isBanned() {
            if (this == GOD) {
                return FluxConfig.banGod;
            }
            return this == HYPER && FluxConfig.banHyper;
        }
    }

    enum TransmitterMode {
        OFF(FluxTranslate.OFF), //
        ON(FluxTranslate.ON), //
        HOTBAR(FluxTranslate.TRANSMITTER_HOTBAR_ONLY), //
        HELD_ITEM(FluxTranslate.TRANSMITTER_HELD_ITEM_ONLY);//

    	Localisation message;

    	TransmitterMode(Localisation message) {
    		this.message = message;
    	}

    	public String getDisplayName() {
    		return message.t();
    	}
    }

    /***/
    PriorityMode getReceiveMode();

    PriorityMode getSendMode();

    TransmitterMode getTransmitterMode();

    TransferMode getTransferMode();
}
