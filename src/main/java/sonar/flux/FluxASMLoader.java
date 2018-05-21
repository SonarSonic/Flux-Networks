package sonar.flux;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import sonar.core.helpers.ASMLoader;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.ITileEnergyHandler;
import sonar.flux.api.energy.ItemEnergyHandler;
import sonar.flux.api.energy.TileEnergyHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class FluxASMLoader {

	public static void load(@Nonnull ASMDataTable asmDataTable) {
		FluxNetworks.loadedTileEnergyHandlers = getTileEnergyHandlers(asmDataTable);
		FluxNetworks.loadedItemEnergyHandlers = getItemEnergyHandlers(asmDataTable);
	}

	public static List<ITileEnergyHandler> getTileEnergyHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(FluxNetworks.logger, asmDataTable, TileEnergyHandler.class, ITileEnergyHandler.class, true, true);
	}

	public static List<IItemEnergyHandler> getItemEnergyHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(FluxNetworks.logger, asmDataTable, ItemEnergyHandler.class, IItemEnergyHandler.class, true, true);
	}
}
