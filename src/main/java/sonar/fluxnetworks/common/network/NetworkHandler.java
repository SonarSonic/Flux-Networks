package sonar.fluxnetworks.common.network;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ByteArrayMap;
import net.minecraft.client.Minecraft;
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
import sonar.fluxnetworks.api.misc.IMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class copied from Modern UI
 */
@SuppressWarnings("unused")
public enum NetworkHandler {
    INSTANCE(FluxNetworks.MODID, "main_network");

    private final Byte2ObjectArrayMap<Supplier<? extends IMessage>> indices = new Byte2ObjectArrayMap<>();
    private final Object2ByteArrayMap<Class<? extends IMessage>> types = new Object2ByteArrayMap<>();

    private final ResourceLocation channel;

    private final String protocol;

    private byte index = Byte.MIN_VALUE;

    {
        types.defaultReturnValue(Byte.MAX_VALUE);
    }

    NetworkHandler(@Nonnull String modid, @Nonnull String name) {
        protocol = DigestUtils.md5Hex(ModList.get().getModFileById(modid).getMods().stream()
                .map(iModInfo -> iModInfo.getVersion().getQualifier())
                .collect(Collectors.joining(",")).getBytes(StandardCharsets.UTF_8));
        EventNetworkChannel network = NetworkRegistry.ChannelBuilder
                .named(channel = new ResourceLocation(modid, name))
                .networkProtocolVersion(this::getProtocolVersion)
                .clientAcceptedVersions(this::verifyServerProtocol)
                .serverAcceptedVersions(this::verifyClientProtocol)
                .eventNetworkChannel();
        if (FMLEnvironment.dist.isClient()) {
            network.addListener(this::onS2CMessageReceived);
        }
        network.addListener(this::onC2SMessageReceived);
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(FluxTileMessage.class, FluxTileMessage::new);
        INSTANCE.registerMessage(SFeedbackMessage.class, SFeedbackMessage::new);
        INSTANCE.registerMessage(SSuperAdminMessage.class, SSuperAdminMessage::new);
        INSTANCE.registerMessage(CSuperAdminMessage.class, CSuperAdminMessage::new);
        INSTANCE.registerMessage(CEditMemberMessage.class, CEditMemberMessage::new);
        INSTANCE.registerMessage(CEditNetworkMessage.class, CEditNetworkMessage::new);
        INSTANCE.registerMessage(CEditWirelessMessage.class, CEditWirelessMessage::new);
        INSTANCE.registerMessage(SLavaParticleMessage.class, SLavaParticleMessage::new);
        INSTANCE.registerMessage(CNetworkUpdateMessage.class, CNetworkUpdateMessage::new);
        INSTANCE.registerMessage(SNetworkUpdateMessage.class, SNetworkUpdateMessage::new);
        INSTANCE.registerMessage(CSelectNetworkMessage.class, CSelectNetworkMessage::new);
        INSTANCE.registerMessage(CCreateNetworkMessage.class, CCreateNetworkMessage::new);
        INSTANCE.registerMessage(CDeleteNetworkMessage.class, CDeleteNetworkMessage::new);
        INSTANCE.registerMessage(CGuiPermissionMessage.class, CGuiPermissionMessage::new);
        INSTANCE.registerMessage(SGuiPermissionMessage.class, SGuiPermissionMessage::new);
        INSTANCE.registerMessage(CEditConnectionsMessage.class, CEditConnectionsMessage::new);
        INSTANCE.registerMessage(CConnectionUpdateMessage.class, CConnectionUpdateMessage::new);
        INSTANCE.registerMessage(SConnectionUpdateMessage.class, SConnectionUpdateMessage::new);
        INSTANCE.registerMessage(CConfiguratorConnectMessage.class, CConfiguratorConnectMessage::new);
        INSTANCE.registerMessage(CConfiguratorSettingMessage.class, CConfiguratorSettingMessage::new);
    }

    /**
     * Get player on current side depending on given network context
     *
     * @param context network context
     * @return player entity
     */
    @Nullable
    public static PlayerEntity getPlayer(@Nonnull NetworkEvent.Context context) {
        if (context.getDirection().getOriginationSide().isClient()) {
            return context.getSender();
        } else {
            return Helper.getPlayer();
        }
    }

    /**
     * Get the protocol version of this channel on current side
     *
     * @return the protocol
     */
    public String getProtocolVersion() {
        return protocol;
    }

    /**
     * This method will run on client to verify the server protocol that sent by handshake network channel
     *
     * @param serverProtocol the protocol of this channel sent from server side
     * @return {@code true} to accept the protocol, {@code false} otherwise
     */
    public boolean verifyServerProtocol(@Nonnull String serverProtocol) {
        return serverProtocol.equals(protocol);
    }

    /**
     * This method will run on server to verify the remote client protocol that sent by handshake network channel
     *
     * @param clientProtocol the protocol of this channel sent from client side
     * @return {@code true} to accept the protocol, {@code false} otherwise
     */
    public boolean verifyClientProtocol(@Nonnull String clientProtocol) {
        return clientProtocol.equals(protocol);
    }

    /**
     * Register a network message, for example
     * {@code registerMessage(MyMessage.class, MyMessage::new)}
     *
     * @param clazz   message class
     * @param factory factory to create new instance
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void registerMessage(@Nonnull Class<MSG> clazz, @Nonnull Supplier<? extends MSG> factory) {
        /*CHANNEL.messageBuilder(type, ++index, direction)
                .encoder(IMessage::encode)
                .decoder(buf -> decode(factory, buf))
                .consumer((BiConsumer<MSG, Supplier<NetworkEvent.Context>>) this::handle)
                .add();*/
        synchronized (this) {
            if (index == Byte.MAX_VALUE) {
                throw new IllegalStateException("Maximum index reached when registering message");
            }
            indices.put(index, factory);
            if (types.put(clazz, index++) != Byte.MAX_VALUE) {
                throw new IllegalStateException("Duplicated registration when registering message");
            }
        }
    }

    private void onS2CMessageReceived(NetworkEvent.ServerCustomPayloadEvent event) {
        // received on main thread of effective side
        handleMessage(event.getPayload(), event.getSource());
    }

    private void onC2SMessageReceived(NetworkEvent.ClientCustomPayloadEvent event) {
        // received on main thread of effective side
        handleMessage(event.getPayload(), event.getSource());
    }

    private void handleMessage(PacketBuffer buffer, Supplier<NetworkEvent.Context> ctx) {
        byte index = buffer.readByte();
        Supplier<? extends IMessage> factory = indices.get(index);
        if (factory == null) {
            throw new IllegalStateException("Unregistered message with index: " + index);
        }
        IMessage message = factory.get();
        message.handle(buffer, ctx.get());
        ctx.get().setPacketHandled(true);
    }

    /*@Nonnull
    private static <MSG extends IMessage> MSG decode(@Nonnull Supplier<MSG> factory, PacketBuffer buf) {
        MSG msg = factory.get();
        msg.decode(buf);
        return msg;
    }

    private static <MSG extends IMessage> void handle(@Nonnull MSG message, @Nonnull Supplier<NetworkEvent.Context> ctx) {
        message.handle(ctx.get());
        ctx.get().setPacketHandled(true);
    }*/

    @Nonnull
    private <MSG extends IMessage> PacketBuffer toBuffer(@Nonnull MSG message) {
        final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        Class<? extends IMessage> clazz = message.getClass();
        byte index = types.getByte(clazz);
        if (index == Byte.MAX_VALUE) {
            throw new IllegalStateException("Unregistered message with type: " + clazz);
        }
        buffer.writeByte(index);
        message.encode(buffer);
        return buffer;
    }

    /**
     * Reply a message depending on network context
     *
     * @param message message to reply
     * @param context network context
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void reply(MSG message, NetworkEvent.Context context) {
        context.getPacketDispatcher().sendPacket(channel, toBuffer(message));
    }

    /**
     * Send a message to server, call this on client side
     *
     * @param message message to send
     * @param <MSG>   message type
     */
    @OnlyIn(Dist.CLIENT)
    public <MSG extends IMessage> void sendToServer(MSG message) {
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.sendPacket(new CCustomPayloadPacket(channel, toBuffer(message)));
        }
    }

    @Nonnull
    private <MSG extends IMessage> IPacket<?> getS2CPacket(MSG message) {
        return new SCustomPayloadPlayPacket(channel, toBuffer(message));
    }

    /**
     * Send a message to a player
     *
     * @param message message to send
     * @param player  player entity on server
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void sendToPlayer(MSG message, @Nonnull PlayerEntity player) {
        ((ServerPlayerEntity) player).connection.sendPacket(getS2CPacket(message));
    }

    /**
     * Send a message to all specific players
     *
     * @param message message to send
     * @param players players on server
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void sendToPlayers(MSG message, @Nonnull Iterable<? extends PlayerEntity> players) {
        final IPacket<?> packet = getS2CPacket(message);
        for (PlayerEntity player : players) {
            ((ServerPlayerEntity) player).connection.sendPacket(packet);
        }
    }

    /**
     * Send a message to all players on the server
     *
     * @param message message to send
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void sendToAll(MSG message) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().sendPacketToAllPlayers(getS2CPacket(message));
    }

    /**
     * Send a message to all players in specified dimension
     *
     * @param message   message to send
     * @param dimension dimension that players in
     * @param <MSG>     message type
     */
    public <MSG extends IMessage> void sendToDimension(MSG message, @Nonnull RegistryKey<World> dimension) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().func_232642_a_(getS2CPacket(message), dimension);
    }

    /**
     * Send a message to all players nearby a point with specified radius in specified dimension
     *
     * @param message   message to send
     * @param excluded  excluded player to send the packet
     * @param x         target point x
     * @param y         target point y
     * @param z         target point z
     * @param radius    radius to target point
     * @param dimension dimension that players in
     * @param <MSG>     message type
     */
    public <MSG extends IMessage> void sendToAllNear(MSG message, @Nullable ServerPlayerEntity excluded,
                                                     double x, double y, double z, double radius,
                                                     @Nonnull RegistryKey<World> dimension) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().sendToAllNearExcept(
                excluded, x, y, z, radius, dimension, getS2CPacket(message));
    }

    /**
     * Send a message to all players tracking the specified entity. If a chunk that player loaded
     * on the client contains the chunk where the entity is located, and then the player is
     * tracking the entity.
     *
     * @param message message to send
     * @param entity  entity is tracking
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void sendToTrackingEntity(MSG message, @Nonnull Entity entity) {
        ((ServerWorld) entity.getEntityWorld()).getChunkProvider().sendToAllTracking(
                entity, getS2CPacket(message));
    }

    /**
     * Send a message to all players tracking the specified entity, and also send the message to
     * the entity if it is a player. If a chunk that player loaded on the client contains the
     * chunk where the entity is located, and then the player is tracking the entity.
     *
     * @param message message to send
     * @param entity  entity is tracking
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void sendToTrackingAndSelf(MSG message, @Nonnull Entity entity) {
        ((ServerWorld) entity.getEntityWorld()).getChunkProvider().sendToTrackingAndSelf(
                entity, getS2CPacket(message));
    }

    /**
     * Send a message to all players who loaded the specified chunk
     *
     * @param message message to send
     * @param chunk   chunk that players in
     * @param <MSG>   message type
     */
    public <MSG extends IMessage> void sendToTrackingChunk(MSG message, @Nonnull Chunk chunk) {
        final IPacket<?> packet = getS2CPacket(message);
        ((ServerWorld) chunk.getWorld()).getChunkProvider().chunkManager.getTrackingPlayers(
                chunk.getPos(), false).forEach(player -> player.connection.sendPacket(packet));
    }

    @OnlyIn(Dist.CLIENT)
    private static class Helper {

        private Helper() {
        }

        @Nullable
        private static PlayerEntity getPlayer() {
            return Minecraft.getInstance().player;
        }
    }
}
