package sonar.fluxnetworks.common.test;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

@Deprecated
public interface IFluxTransfer {

    void onCycleStart();

    void onCycleEnd();

    long sendToTile(long amount, boolean simulate);

    void onEnergyReceived(long amount);

    TileEntity getTile();

    ItemStack getDisplayStack();
}
