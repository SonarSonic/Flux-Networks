package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.ByteNBT;

public interface ISuperAdmin {

    void changePermission();

    boolean hasPermission();

    ByteNBT writeNBT();

    void readNBT(ByteNBT nbt);
}
