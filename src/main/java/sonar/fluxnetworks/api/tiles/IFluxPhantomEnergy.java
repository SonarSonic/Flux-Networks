package sonar.fluxnetworks.api.tiles;

import net.minecraft.util.Direction;

public interface IFluxPhantomEnergy extends IFluxConnector {

    long addPhantomEnergyToNetwork(Direction dir, long amount, boolean simulate);

}
