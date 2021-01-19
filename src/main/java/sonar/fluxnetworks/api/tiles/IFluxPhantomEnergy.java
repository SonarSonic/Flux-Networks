package sonar.fluxnetworks.api.tiles;

import net.minecraft.util.EnumFacing;

public interface IFluxPhantomEnergy extends IFluxConnector {

    long addPhantomEnergyToNetwork(EnumFacing side, long amount, boolean simulate);

}
