package sonar.flux;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import sonar.core.helpers.ASMLoader;
import sonar.flux.api.energy.FluxEnergyHandler;
import sonar.flux.api.energy.IFluxEnergyHandler;

public class FluxASMLoader {

	public static void load(@Nonnull ASMDataTable asmDataTable) {
		FluxNetworks.loadedEnergyHandlers = getEnergyHandlers(asmDataTable);
	}

	public static List<IFluxEnergyHandler> getEnergyHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(FluxNetworks.logger, asmDataTable, FluxEnergyHandler.class, IFluxEnergyHandler.class, true, true);
	}
}
