package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.List;

public interface ITransferHandler {

    void onLastEndTick();

    long getBuffer();

    long getRequest();

    default long getEnergyStored() {
        return getBuffer();
    }

    long getChange();

    void updateTransfers(Direction... faces);

    List<IFluxTransfer> getTransfers();

    long addToNetwork(long maxAmount);

    long removeFromNetwork(long maxAmount, boolean simulate);

    CompoundNBT writeNetworkedNBT(CompoundNBT tag);

    void readNetworkedNBT(CompoundNBT tag);
}
