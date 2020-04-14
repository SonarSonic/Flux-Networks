package sonar.fluxnetworks.api.network;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IFluxTransfer {

    void onStartCycle();

    void onEndCycle();

    long addEnergy(long amount, boolean simulate);

    long removeEnergy(long amount, boolean simulate);

    void onEnergyAdded(long amount);

    void onEnergyRemoved(long amount);

    TileEntity getTile();

    ItemStack getDisplayStack();

    boolean isInvalid();
}
