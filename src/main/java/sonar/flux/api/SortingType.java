package sonar.flux.api;

import java.util.Comparator;
import java.util.List;

import sonar.core.helpers.SonarHelper;
import sonar.core.utils.SortingDirection;
import sonar.flux.api.tiles.IFlux.ConnectionType;

public enum SortingType {

	PRIORITY, TRANSFER, TRANSFER_LIMIT, DIMENSION_NO, CONNECTION_TYPE, CONNECTION_NAME;

	SortingType() {}

	public String getDisplayName() {
		switch (this) {
		case CONNECTION_NAME:
			return "Connection Name";
		case CONNECTION_TYPE:
			return "Connection Type";
		case DIMENSION_NO:
			return "Dimension Number";
		case PRIORITY:
			return "Priority";
		case TRANSFER:
			return "Energy Transfer";
		case TRANSFER_LIMIT:
			return "Transfer Limit";
		default:
			break;
		}
		return name();

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
