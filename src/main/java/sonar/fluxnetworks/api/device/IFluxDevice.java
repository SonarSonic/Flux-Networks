package sonar.fluxnetworks.api.device;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Defines a network device that can be connected to a specific network.
 * The implementation may not be a logical entity.
 */
public interface IFluxDevice {

    /**
     * Returns the network ID that this connector should be. However, this connector
     * may not be currently connected to the network (waiting to connect).
     *
     * @return the network ID
     */
    int getNetworkID();

    /**
     * A constant value identifying the type of this device.
     *
     * @return the device type
     */
    @Nonnull
    FluxDeviceType getDeviceType();

    @Nonnull
    UUID getOwnerUUID();

    void writeCustomTag(@Nonnull CompoundTag tag, int type);

    void readCustomTag(@Nonnull CompoundTag tag, int type);

    //boolean canPlayerAccess(Player player);

    //int getLogicPriority(); // consider surge, for transfer on server

    int getRawPriority(); // ignore surge, for numeric display on client

    //void setPriority(int priority);

    //long getLogicLimit(); // consider disable limit

    long getRawLimit(); // ignore disable limit

    //void setTransferLimit(long limit);

    /**
     * If this device is storage, this method returns the max energy storage of it,
     * or Long.MAX_VALUE otherwise
     *
     * @return max transfer limit
     */
    long getMaxTransferLimit();

    //boolean isActive();

    boolean isChunkLoaded();

    @Nonnull
    GlobalPos getGlobalPos();

    /*int getFolderID();*/

    String getCustomName();

    //void setCustomName(String customName);

    boolean getDisableLimit();

    //void setDisableLimit(boolean disableLimit);

    boolean getSurgeMode();

    boolean isForcedLoading();

    //void setSurgeMode(boolean surgeMode);

    /**
     * Transfer handler is unavailable on client, this method is mainly used for gui display on client
     * If this device is storage, this method returns the energy stored of it
     *
     * @return internal buffer or energy stored
     */
    long getTransferBuffer();

    long getTransferChange();

    @Nonnull
    ItemStack getDisplayStack();
}
