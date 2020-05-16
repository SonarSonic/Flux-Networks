package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;

public interface ISuperAdmin {

    void iterateSuperAdmin();

    boolean isSuperAdmin();

    CompoundNBT writeToNBT(CompoundNBT nbt);

    void readFromNBT(CompoundNBT nbt);
}
