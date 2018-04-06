package sonar.flux.api;

import java.util.Comparator;
import java.util.List;

import sonar.core.helpers.SonarHelper;
import sonar.core.translate.Localisation;
import sonar.core.utils.SortingDirection;
import sonar.flux.FluxTranslate;
import sonar.flux.api.tiles.IFlux.ConnectionType;

public enum SortingType {

	PRIORITY(FluxTranslate.PRIORITY), //
	TRANSFER(FluxTranslate.SORTING_TRANSFER), //
	TRANSFER_LIMIT(FluxTranslate.TRANSFER_LIMIT), //
	DIMENSION_NO(FluxTranslate.SORTING_DIMENSION), //
	CONNECTION_TYPE(FluxTranslate.SORTING_TYPE), //
	CONNECTION_NAME(FluxTranslate.SORTING_NAME);//

	Localisation message;

	SortingType(Localisation message) {
		this.message = message;
	}

	public String getDisplayName() {
		return message.t();
	}

	public void sort(List<ClientFlux> list, SortingDirection dir) {
		final SortingDirection actual_dir = dir.switchDir();
		switch (this) {
		case CONNECTION_NAME:
			list.sort((flux1, flux2) -> {
				return SonarHelper.compareStringsWithDirection(flux1.getCustomName(), flux2.getCustomName(), actual_dir);
			});
			break;
		case CONNECTION_TYPE:
			list.sort((flux1, flux2) -> {
				return SonarHelper.compareStringsWithDirection(flux1.getConnectionType().getRepresentiveStack().getDisplayName(), flux2.getConnectionType().getRepresentiveStack().getDisplayName(), actual_dir);
			});
			break;
		case DIMENSION_NO:
			list.sort((flux1, flux2) -> {
				return SonarHelper.compareWithDirection(flux1.getCoords().getDimension(), flux2.getCoords().getDimension(), actual_dir);
			});
			break;
		case PRIORITY:
			list.sort((flux1, flux2) -> {
				return SonarHelper.compareWithDirection(flux1.getCurrentPriority(), flux2.getCurrentPriority(), actual_dir);
			});
			break;
		case TRANSFER:
			list.sort((flux1, flux2) -> {
				long transfer1 = flux1.getTransferHandler().getAdded() - flux1.getTransferHandler().getRemoved();
				long transfer2 = flux2.getTransferHandler().getAdded() - flux2.getTransferHandler().getRemoved();
				if(flux1.getConnectionType()== ConnectionType.STORAGE){
					transfer1 = -transfer1;
				}
				if(flux2.getConnectionType()== ConnectionType.STORAGE){
					transfer2 = -transfer2;
				}
				return SonarHelper.compareWithDirection(transfer1, transfer2, actual_dir);
			});
			break;
		case TRANSFER_LIMIT:
			list.sort((flux1, flux2) -> {
				return SonarHelper.compareWithDirection(flux1.getTransferLimit(), flux2.getTransferLimit(), actual_dir);
			});
			break;
		default:
			break;

		}
	}
}
