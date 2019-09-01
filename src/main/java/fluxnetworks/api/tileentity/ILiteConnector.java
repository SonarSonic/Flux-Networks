package fluxnetworks.api.tileentity;

import fluxnetworks.api.Coord4D;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public interface ILiteConnector {

    NBTTagCompound writeNetworkData(NBTTagCompound tag);

    void readNetworkData(NBTTagCompound tag);

    int getNetworkID();

    int getPriority();

    UUID getConnectionOwner();

    IFluxConnector.ConnectionType getConnectionType();

    boolean isChunkLoaded();

    long getCurrentLimit();

    Coord4D getCoords();

    int getFolderID();

    String getCustomName();

    boolean getDisableLimit();

    boolean getSurgeMode();

    boolean isDirty();

    void setChunkLoaded(boolean b);

    void updateData(IFluxConnector flux);

    ItemStack getDisplayStack();
}
