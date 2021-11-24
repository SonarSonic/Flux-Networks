package sonar.fluxnetworks.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * Functions the same as {@link IEnergyStorage}, but allows Long.MAX_VALUE.
 * use the cap in {@link sonar.fluxnetworks.api.misc.FluxCapabilities} to add support to your mod
 */
public interface IFNEnergyStorage {

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    long receiveEnergyL(long maxReceive, boolean simulate);

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    long extractEnergyL(long maxExtract, boolean simulate);

    /**
     * Returns the amount of energy currently stored.
     */
    long getEnergyStoredL();

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    long getMaxEnergyStoredL();

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    boolean canExtract();

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    boolean canReceive();
}
