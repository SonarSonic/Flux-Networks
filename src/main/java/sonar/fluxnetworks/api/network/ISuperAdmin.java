package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.NBTTagCompound;

/**
 *
 */
public interface ISuperAdmin {

    void changePermission();

    boolean getPermission();

    NBTTagCompound writeToNBT(NBTTagCompound nbt);

    void readFromNBT(NBTTagCompound nbt);
}
