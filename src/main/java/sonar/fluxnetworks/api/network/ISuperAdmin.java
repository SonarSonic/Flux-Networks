package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.ByteTag;

public interface ISuperAdmin {

    void changePermission();

    boolean hasPermission();

    ByteTag writeNBT();

    void readNBT(ByteTag nbt);
}
