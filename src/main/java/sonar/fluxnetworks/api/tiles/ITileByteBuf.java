package sonar.fluxnetworks.api.tiles;

import net.minecraft.network.PacketBuffer;

public interface ITileByteBuf {

    void writePacket(PacketBuffer buf, int id);

    void readPacket(PacketBuffer buf, int id);
}
