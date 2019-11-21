package icyllis.fluxnetworks.api.tile;

import net.minecraft.util.Direction;

/**
 * A phantom interface to transfer energy across flux network by other mods
 * Implemented by flux plug
 */
public interface IFluxPhantom extends IFluxTile {

    long addPhantomEnergyToNetwork(Direction side, long amount, boolean simulate);
}
