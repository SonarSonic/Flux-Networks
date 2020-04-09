package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;

/**
 *
 */
public interface ISuperAdmin {

    void changePermission();

    boolean getPermission();

    CompoundNBT writeToNBT(CompoundNBT nbt);

    void readFromNBT(CompoundNBT nbt);
}
