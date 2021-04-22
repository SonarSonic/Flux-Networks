package sonar.fluxnetworks.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.codec.digest.DigestUtils;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * This class copied from Modern UI
 */
@SuppressWarnings("unused")
public class NetworkHandler {

    static final NetworkHandler sInstance = new NetworkHandler(FluxNetworks.MODID, "main_network");

    private final ResourceLocation mChannel;
    private final String mProtocol;
    private final Broadcaster mBroadcaster = this.new Broadcaster();

    public NetworkHandler(@Nonnull String modid, @Nonnull String name) {
        mProtocol = DigestUtils.md5Hex(ModList.get().getModFileById(modid).getMods().stream()
                .map(iModInfo -> iModInfo.getVersion().getQualifier())
                .collect(Collectors.joining(",")).getBytes(StandardCharsets.UTF_8));
        EventNetworkChannel network = NetworkRegistry.ChannelBuilder
                .named(mChannel = new ResourceLocation(modid, name))
                .networkProtocolVersion(this::getProtocolVersion)
                .clientAcceptedVersions(this::checkS2CProtocol)
                .serverAcceptedVersions(this::checkC2SProtocol)
                .eventNetworkChannel();
        if (FMLEnvironment.dist.isClient()) {
            network.addListener(this::onS2CMessageReceived);
        }
        network.addListener(this::onC2SMessageReceived);
    }

    public static void initialize() {
        // invoke static initializer
    }

    /**
     * Get the protocol version of this channel on current side
     *
     * @return the protocol
     */
    public String getProtocolVersion() {
        return mProtocol;
    }

    /**
     * This method will run on client to verify the server protocol that sent by handshake network channel
     *
     * @param serverProtocol the protocol of this channel sent from server side
     * @return {@code true} to accept the protocol, {@code false} otherwise
     */
    private boolean checkS2CProtocol(@Nonnull String serverProtocol) {
        return serverProtocol.equals(mProtocol);
    }

    /**
     * This method will run on server to verify the remote client protocol that sent by handshake network channel
     *
     * @param clientProtocol the protocol of this channel sent from client side
     * @return {@code true} to accept the protocol, {@code false} otherwise
     */
    private boolean checkC2SProtocol(@Nonnull String clientProtocol) {
        return clientProtocol.equals(mProtocol);
    }

    @OnlyIn(Dist.CLIENT)
    private void onS2CMessageReceived(@Nonnull NetworkEvent.ServerCustomPayloadEvent event) {
        // received on main thread of effective side
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null)
            C2SNetMsg.sFunctors[event.getPayload().readShort()].f(event.getPayload(), player);
        event.getPayload().release(); // forge disabled this on client, see ClientPacketListener.handleCustomPayload() finally {}
        event.getSource().get().setPacketHandled(true);
    }

    private void onC2SMessageReceived(@Nonnull NetworkEvent.ClientCustomPayloadEvent event) {
        // received on main thread of effective side
        ServerPlayerEntity player = event.getSource().get().getSender();
        if (player != null)
            S2CNetMsg.sFunctors[event.getPayload().readShort()].f(event.getPayload(), player);
        event.getSource().get().setPacketHandled(true);
    }

    /**
     * Allocate a buffer to write packet data with index. Once you done that,
     * pass the value returned here to {@link #getBroadcaster(PacketBuffer)}
     *
     * @param index The message index used on the opposite side, range from 0 to 32767
     * @return a byte buf to write the packet data (message)
     */
    @Nonnull
    public PacketBuffer targetAt(int index) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeShort(index);
        return buffer;
    }

    /**
     * Being ready to send a packet, you must dispatch this packet right after
     * calling this, for example {@link Broadcaster#sendToPlayer(PlayerEntity)}
     * Only called from server thread
     *
     * @param buf The packet message
     * @return a broadcaster to dispatch/send/broadcast packet
     * @see #targetAt(int)
     */
    @Nonnull
    public Broadcaster getBroadcaster(@Nonnull PacketBuffer buf) {
        Broadcaster b = mBroadcaster;
        b.buf = buf;
        return b;
    }

    /**
     * Send a message to server
     * <p>
     * This is the only method to be called on the client, the rest needs
     * to be called on the server side
     */
    @OnlyIn(Dist.CLIENT)
    public void sendToServer(@Nonnull PacketBuffer buf) {
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        if (connection != null)
            connection.sendPacket(new CCustomPayloadPacket(mChannel, buf));
    }

    public class Broadcaster {

        // a ByteBuf wrapper for write data more friendly
        private PacketBuffer buf;

        /**
         * Send a message to a player
         *
         * @param player the server player
         */
        public void sendToPlayer(@Nonnull PlayerEntity player) {
            ((ServerPlayerEntity) player).connection.sendPacket(new SCustomPayloadPlayPacket(mChannel, buf));
            buf = null;
        }

        /**
         * Send a message to a player
         *
         * @param player the server player
         */
        public void sendToPlayer(@Nonnull ServerPlayerEntity player) {
            player.connection.sendPacket(new SCustomPayloadPlayPacket(mChannel, buf));
            buf = null;
        }

        /**
         * Send a message to all specific players
         *
         * @param players players on server
         */
        public void sendToPlayers(@Nonnull Iterable<? extends PlayerEntity> players) {
            final IPacket<?> packet = new SCustomPayloadPlayPacket(mChannel, buf);
            for (PlayerEntity player : players)
                ((ServerPlayerEntity) player).connection.sendPacket(packet);
            buf = null;
        }

        /**
         * Send a message to all players on the server
         */
        public void sendToAll() {
            ServerLifecycleHooks.getCurrentServer().getPlayerList()
                    .sendPacketToAllPlayers(new SCustomPayloadPlayPacket(mChannel, buf));
            buf = null;
        }

        /**
         * Send a message to all players in specified dimension
         *
         * @param dimension dimension that players in
         */
        public void sendToDimension(@Nonnull RegistryKey<World> dimension) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList()
                    .func_232642_a_(new SCustomPayloadPlayPacket(mChannel, buf), dimension);
            buf = null;
        }

        /**
         * Send a message to all players nearby a point with specified radius in specified dimension
         *
         * @param excluded  the player that will not be sent the packet
         * @param x         target point x
         * @param y         target point y
         * @param z         target point z
         * @param radius    radius to target point
         * @param dimension dimension that players in
         */
        public void sendToNearby(@Nullable ServerPlayerEntity excluded,
                                 double x, double y, double z, double radius,
                                 @Nonnull RegistryKey<World> dimension) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().sendToAllNearExcept(excluded,
                    x, y, z, radius, dimension, new SCustomPayloadPlayPacket(mChannel, buf));
            buf = null;
        }

        /**
         * Send a message to all players tracking the specified entity. If a chunk that player loaded
         * on the client contains the chunk where the entity is located, and then the player is
         * tracking the entity changes.
         *
         * @param entity entity is tracking
         */
        public void sendToTrackingEntity(@Nonnull Entity entity) {
            ((ServerWorld) entity.world).getChunkProvider().sendToAllTracking(
                    entity, new SCustomPayloadPlayPacket(mChannel, buf));
            buf = null;
        }

        /**
         * Send a message to all players tracking the specified entity, and also send the message to
         * the entity if it is a player. If a chunk that player loaded on the client contains the
         * chunk where the entity is located, and then the player is tracking the entity changes.
         *
         * @param entity the entity is tracking
         */
        public void sendToTrackingAndSelf(@Nonnull Entity entity) {
            ((ServerWorld) entity.world).getChunkProvider().sendToTrackingAndSelf(
                    entity, new SCustomPayloadPlayPacket(mChannel, buf));
            buf = null;
        }

        /**
         * Send a message to all players who loaded the specified chunk
         *
         * @param chunk the chunk that players in
         */
        public void sendToTrackingChunk(@Nonnull Chunk chunk) {
            final IPacket<?> packet = new SCustomPayloadPlayPacket(mChannel, buf);
            ((ServerWorld) chunk.getWorld()).getChunkProvider().chunkManager.getTrackingPlayers(
                    chunk.getPos(), false).forEach(player -> player.connection.sendPacket(packet));
            buf = null;
        }
    }
}
