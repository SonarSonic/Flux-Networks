package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public interface ITransferHandler {

    void onStartCycle();

    void onEndCycle();

    void setBuffer(long buffer);

    long getBuffer();

    long getChange();

    long getRequest();

    long addEnergyToBuffer(long maxAmount, boolean simulate);

    long removeEnergyFromBuffer(long maxAmount, boolean simulate);

    CompoundNBT writeNetworkedNBT(CompoundNBT tag);

    void readNetworkedNBT(CompoundNBT tag);

    default void updateTransfers(Direction... faces) {

    }

}
