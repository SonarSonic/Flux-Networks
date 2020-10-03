package sonar.fluxnetworks.api.device;

import net.minecraft.network.PacketBuffer;

@Deprecated
public interface ITilePacketBuffer {

    void writePacket(PacketBuffer buf, byte id);

    void readPacket(PacketBuffer buf, byte id);
}
