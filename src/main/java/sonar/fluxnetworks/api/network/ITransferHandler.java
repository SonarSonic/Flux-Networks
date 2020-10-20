package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public interface ITransferHandler {

    void onCycleStart();

    void onCycleEnd();

    @Deprecated
    void setBuffer(long buffer);

    long getBuffer();

    long getRequest();

    /**
     * Get the energy change produced by externals in last tick.
     * For instance, a Plug may receive energy, but not transmit them across the network,
     * so energy change is the amount it received rather than zero, they just went to the buffer.
     * If a Point is requesting 1EU but we can only provide 3FE, the 3FE will go to the
     * Point buffer, and the energy change of the Point is zero rather than 3.
     *
     * @return energy change
     */
    long getChange();

    long addToBuffer(long amount, boolean simulate);

    long removeFromBuffer(long amount, boolean simulate);

    long receiveFromAdjacency(long amount, @Nonnull Direction side, boolean simulate);

    void writeCustomNBT(CompoundNBT tag, int type);

    void readCustomNBT(CompoundNBT tag, int type);

    void writePacket(PacketBuffer buffer, byte id);

    void readPacket(PacketBuffer buffer, byte id);

    default void updateTransfers(@Nonnull Direction... faces) {

    }
}
