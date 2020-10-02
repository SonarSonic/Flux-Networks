package sonar.fluxnetworks.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * Functions the same as {@link IEnergyStorage}  but allows Long.MAX_VALUE
 * use the cap in {@link FNEnergyCapability} to add support to your mod
 */
public interface IFNEnergyStorage {

    long receiveEnergyL(long maxReceive, boolean simulate);

    long extractEnergyL(long maxExtract, boolean simulate);

    long getEnergyStoredL();

    long getMaxEnergyStoredL();

    boolean canExtractL();

    boolean canReceiveL();
}
