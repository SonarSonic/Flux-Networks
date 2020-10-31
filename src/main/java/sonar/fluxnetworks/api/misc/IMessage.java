package sonar.fluxnetworks.api.misc;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.common.network.NetworkHandler;

import javax.annotation.Nonnull;

/**
 * Network message, this class copied from Modern UI
 */
public interface IMessage {

    /**
     * Encode this message to byte buffer
     *
     * @param buffer buffer to write
     */
    void encode(@Nonnull PacketBuffer buffer);

    /**
     * Decode this message from byte buffer and handle this message on sided effective thread.
     * <p>
     * To get the player {@link NetworkHandler#getPlayer(NetworkEvent.Context)}
     * To reply a message {@link NetworkHandler#reply(IMessage, NetworkEvent.Context)}
     * <p>
     * It is not allowed to call {@link NetworkEvent.Context#setPacketHandled(boolean)}
     * or {@link NetworkEvent.Context#enqueueWork(Runnable)}, they are redundant
     *
     * @param buffer  buffer to read
     * @param context network context
     */
    void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context);
}
