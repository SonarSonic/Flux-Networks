package sonar.fluxnetworks.api.tiles;

import net.minecraft.network.PacketBuffer;

public interface ITilePacketBuffer {

    void writePacket(PacketBuffer buf, byte id);

    void readPacket(PacketBuffer buf, byte id);
}
