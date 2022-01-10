package sonar.fluxnetworks.register;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.registry.RegistryItems;
import sonar.fluxnetworks.common.storage.FluxChunkManager;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Network messages.
 */
@SuppressWarnings("unused")
public class NetworkHandler {

    private static final ResourceLocation NAME = new ResourceLocation(FluxNetworks.MODID, "main_network");
    private static final String PROTOCOL = "620";

    public static void register() {
        EventNetworkChannel network = NetworkRegistry.newEventChannel(NAME,
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
        if (FMLEnvironment.dist.isClient()) {
            network.addListener(NetworkHandler::onClientCustomPayload);
        }
        network.addListener(NetworkHandler::onServerCustomPayload);
    }

    @OnlyIn(Dist.CLIENT)
    private static void onClientCustomPayload(@Nonnull NetworkEvent.ServerCustomPayloadEvent event) {
        // received on main thread of effective side
        if (event.getLoginIndex() == Integer.MIN_VALUE) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                C.handle(event.getPayload().readShort(), event.getPayload(), player);
            }
            event.getPayload().release();
        }
        event.getSource().get().setPacketHandled(true);
    }

    private static void onServerCustomPayload(@Nonnull NetworkEvent.ClientCustomPayloadEvent event) {
        // received on main thread of effective side
        if (event.getLoginIndex() == Integer.MIN_VALUE) {
            ServerPlayerEntity player = event.getSource().get().getSender();
            if (player != null) {
                handle(event.getPayload().readShort(), event.getPayload(), player);
            }
        }
        event.getSource().get().setPacketHandled(true);
    }

    @Nonnull
    public static PacketBuffer S2C_TileEntity(@Nonnull TileFluxDevice tile, byte type) {
        PacketBuffer buf = buffer(0);
        buf.writeBlockPos(tile.getPos());
        buf.writeByte(type);
        tile.writePacket(buf, type);
        return buf;
    }

    private static void S2C_Response(@Nonnull FeedbackInfo code, @Nonnull ServerPlayerEntity player) {
        PacketBuffer buf = buffer(1);
        buf.writeVarInt(code.ordinal());
        sendToPlayer(buf, player);
    }

    // update client super admin state
    public static void S2C_SuperAdmin(boolean state, PlayerEntity player) {
        PacketBuffer buf = buffer(2);
        buf.writeBoolean(state);
        sendToPlayer(buf, player);
    }

    // update flux network data
    @Nonnull
    public static PacketBuffer S2C_UpdateNetwork(@Nonnull IFluxNetwork network, int type) {
        PacketBuffer buf = buffer(3);
        buf.writeVarInt(type);
        buf.writeVarInt(1); // size = 1
        buf.writeVarInt(network.getNetworkID());
        final CompoundNBT tag = new CompoundNBT();
        network.writeCustomNBT(tag, type);
        buf.writeCompoundTag(tag);
        return buf;
    }

    // update flux networks data
    @Nonnull
    public static PacketBuffer S2C_UpdateNetwork(@Nonnull Collection<IFluxNetwork> networks, int type) {
        PacketBuffer buf = buffer(3);
        buf.writeVarInt(type);
        buf.writeVarInt(networks.size());
        for (IFluxNetwork network : networks) {
            buf.writeVarInt(network.getNetworkID());
            final CompoundNBT tag = new CompoundNBT();
            network.writeCustomNBT(tag, type);
            buf.writeCompoundTag(tag);
        }
        return buf;
    }

    private static void S2C_UpdateAccess(@Nonnull AccessLevel level, ServerPlayerEntity player) {
        PacketBuffer buf = buffer(4);
        buf.writeVarInt(level.ordinal());
        sendToPlayer(buf, player);
    }

    private static void S2C_UpdateConnection(int networkID, @Nonnull List<CompoundNBT> tags,
                                             ServerPlayerEntity player) {
        PacketBuffer buf = buffer(5);
        buf.writeVarInt(networkID);
        buf.writeVarInt(tags.size());
        tags.forEach(buf::writeCompoundTag);
        sendToPlayer(buf, player);
    }

    private static void handle(short index, @Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        switch (index) {
            case 0:
                tileEntity(buf, player);
                break;
            case 1:
                responseSuperAdmin(buf, player);
                break;
            case 2:
                editMember(buf, player);
                break;
            case 3:
                editNetwork(buf, player);
                break;
            case 4:
                editWireless(buf, player);
                break;
            case 5:
                responseNetworkUpdate(buf, player);
                break;
            case 6:
                setNetwork(buf, player);
                break;
            case 7:
                createNetwork(buf, player);
                break;
            case 8:
                deleteNetwork(buf, player);
                break;
            case 9:
                responseAccessUpdate(buf, player);
                break;
            case 10:
                editConnections(buf, player);
                break;
            case 11:
                responseConnectionUpdate(buf, player);
                break;
            case 12:
                configuratorNet(buf, player);
                break;
            case 13:
                configuratorEdit(buf, player);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    ///  HANDLING  \\\

    private static void tileEntity(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        final TileEntity tile = player.world.getTileEntity(buf.readBlockPos());
        if (tile instanceof TileFluxDevice) {
            final TileFluxDevice flux = (TileFluxDevice) tile;
            // security check
            if (!flux.canPlayerAccess(player)) {
                return;
            }
            final byte type = buf.readByte();
            flux.readPacket(buf, type);
            if (type == FluxConstants.C2S_CHUNK_LOADING && !FluxConfig.enableChunkLoading) {
                S2C_Response(FeedbackInfo.BANNED_LOADING, player);
            }
        }
    }

    private static void responseSuperAdmin(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        ISuperAdmin sa = FluxUtils.get(player.getCapability(FluxCapabilities.SUPER_ADMIN));
        if (sa != null && (sa.hasPermission() || SuperAdmin.canActivateSuperAdmin(player))) {
            sa.changePermission();
            if (sa.hasPermission()) {
                S2C_Response(FeedbackInfo.SA_ON, player);
            } else {
                S2C_Response(FeedbackInfo.SA_OFF, player);
            }
            S2C_SuperAdmin(sa.hasPermission(), player);
        }
    }

    private static void editMember(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity sender) {
        final IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }

        final AccessLevel senderAccess = network.getPlayerAccess(sender);
        // check permission
        if (!senderAccess.canEdit()) {
            S2C_Response(FeedbackInfo.NO_ADMIN, sender);
            return;
        }

        final UUID targetUUID = buf.readUniqueId();
        final int type = buf.readVarInt();

        // editing yourself
        final boolean self = PlayerEntity.getUUID(sender.getGameProfile()).equals(targetUUID);
        // current member in the network
        final Optional<NetworkMember> current = network.getMemberByUUID(targetUUID);

        // create new member
        if (type == FluxConstants.TYPE_NEW_MEMBER) {
            final PlayerEntity target = ServerLifecycleHooks.getCurrentServer()
                    .getPlayerList().getPlayerByUUID(targetUUID);
            // is online and not in the network
            if (target != null && !current.isPresent()) {
                NetworkMember m = NetworkMember.create(target, AccessLevel.USER);
                network.getRawMemberMap().put(m.getPlayerUUID(), m);
                S2C_Response(FeedbackInfo.SUCCESS, sender);
                sendToPlayer(S2C_UpdateNetwork(network, FluxConstants.TYPE_NET_MEMBERS), sender);
            } else {
                S2C_Response(FeedbackInfo.INVALID_USER, sender);
            }
        } else if (current.isPresent()) {
            final NetworkMember c = current.get();
            if (self || c.getAccessLevel() == AccessLevel.OWNER) {
                return;
            }
            if (type == FluxConstants.TYPE_SET_ADMIN) {
                // we are not owner or super admin
                if (!senderAccess.canDelete()) {
                    S2C_Response(FeedbackInfo.NO_OWNER, sender);
                    return;
                }
                c.setAccessLevel(AccessLevel.ADMIN);
            } else if (type == FluxConstants.TYPE_SET_USER) {
                c.setAccessLevel(AccessLevel.USER);
            } else if (type == FluxConstants.TYPE_CANCEL_MEMBERSHIP) {
                network.getRawMemberMap().remove(targetUUID);
            } else if (type == FluxConstants.TYPE_TRANSFER_OWNERSHIP) {
                if (!senderAccess.canDelete()) {
                    S2C_Response(FeedbackInfo.NO_OWNER, sender);
                    return;
                }
                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                    .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s
                    .setAccessPermission(AccessPermission.USER));*/
                network.getAllMembers().removeIf(f -> f.getAccessLevel().canDelete());
                network.setOwnerUUID(targetUUID);
                c.setAccessLevel(AccessLevel.OWNER);
            }
            S2C_Response(FeedbackInfo.SUCCESS, sender);
            sendToPlayer(S2C_UpdateNetwork(network, FluxConstants.TYPE_NET_MEMBERS), sender);
        } else if (type == FluxConstants.TYPE_TRANSFER_OWNERSHIP) {
            if (!senderAccess.canDelete()) {
                S2C_Response(FeedbackInfo.NO_OWNER, sender);
                return;
            }
            // super admin can still transfer ownership to self
            if (self && senderAccess == AccessLevel.OWNER) {
                return;
            }
            PlayerEntity target = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(targetUUID);
            // is online
            if (target != null) {
                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s
                        .setAccessPermission(AccessPermission.USER));*/
                network.getAllMembers().removeIf(f -> f.getAccessLevel().canDelete());
                NetworkMember m = NetworkMember.create(target, AccessLevel.OWNER);
                network.getRawMemberMap().put(m.getPlayerUUID(), m);
                network.setOwnerUUID(targetUUID);
                S2C_Response(FeedbackInfo.SUCCESS, sender);
                sendToPlayer(S2C_UpdateNetwork(network, FluxConstants.TYPE_NET_MEMBERS), sender);
            } else {
                S2C_Response(FeedbackInfo.INVALID_USER, sender);
            }
        } else {
            S2C_Response(FeedbackInfo.INVALID_USER, sender);
        }
    }

    private static void editNetwork(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }
        final String name = buf.readString(256);
        final int color = buf.readInt();
        final SecurityType security = SecurityType.values()[buf.readVarInt()];
        final String password = buf.readString(256);

        if (network.getPlayerAccess(player).canEdit()) {
            if (!network.getNetworkName().equals(name)) {
                network.setNetworkName(name);
            }
            if (network.getNetworkColor() != color) {
                network.setNetworkColor(color);
                network.getConnections(FluxLogicType.ANY).forEach(device -> {
                    if (device instanceof TileFluxDevice) {
                        ((TileFluxDevice) device).sendFullUpdatePacket();
                    }
                }); // update appearance
            }
            if (FluxUtils.isLegalPassword(password)) {
                network.getSecurity().set(security, password);
            } else {
                S2C_Response(FeedbackInfo.ILLEGAL_PASSWORD, player);
            }
            sendToPlayer(S2C_UpdateNetwork(network, FluxConstants.TYPE_NET_BASIC), player);
            S2C_Response(FeedbackInfo.SUCCESS_2, player);
        } else {
            S2C_Response(FeedbackInfo.NO_ADMIN, player);
        }
    }

    private static void editWireless(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }
        if (network.getPlayerAccess(player).canEdit()) {
            network.setWirelessMode(buf.readVarInt());
            sendToPlayer(S2C_UpdateNetwork(network, FluxConstants.TYPE_NET_BASIC), player);
            S2C_Response(FeedbackInfo.SUCCESS, player);
        } else {
            S2C_Response(FeedbackInfo.NO_ADMIN, player);
        }
    }

    private static void responseNetworkUpdate(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        final int type = buf.readVarInt();
        final int size = buf.readVarInt();
        List<IFluxNetwork> networks = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
            if (network.isValid()) {
                networks.add(network);
            }
        }
        if (!networks.isEmpty()) {
            sendToPlayer(S2C_UpdateNetwork(networks, type), player);
        }
    }

    private static void setNetwork(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        TileEntity tile = player.world.getTileEntity(buf.readBlockPos());
        if (!(tile instanceof TileFluxDevice)) {
            return;
        }
        TileFluxDevice flux = (TileFluxDevice) tile;
        final int networkID = buf.readVarInt();
        if (flux.getNetworkID() == networkID) {
            return;
        }
        // we can connect to an invalid network (i.e disconnect)
        final IFluxNetwork network = FluxNetworkData.getNetwork(networkID);

        if (network.isValid() &&
                flux.getDeviceType().isController() &&
                !network.getConnections(FluxLogicType.CONTROLLER).isEmpty()) {
            S2C_Response(FeedbackInfo.HAS_CONTROLLER, player);
        } else {
            if (network.isValid() && noAccess(buf.readString(256), player, network))
                return;
            if (network.isValid()) {
                flux.setConnectionOwner(PlayerEntity.getUUID(player.getGameProfile()));
            }
            flux.connect(network);
            S2C_Response(FeedbackInfo.SUCCESS, player);
        }
    }

    private static boolean noAccess(String password, ServerPlayerEntity player, @Nonnull IFluxNetwork network) {
        // not a member
        if (!network.getPlayerAccess(player).canUse()) {
            if (network.getSecurity().getType() == SecurityType.PRIVATE) {
                S2C_Response(FeedbackInfo.REJECT, player);
                return true;
            }
            if (password.isEmpty()) {
                S2C_Response(FeedbackInfo.PASSWORD_REQUIRE, player);
                return true;
            }
            if (!password.equals(network.getSecurity().getPassword())) {
                S2C_Response(FeedbackInfo.REJECT, player);
                return true;
            }
        }
        return false;
    }

    private static void createNetwork(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        final String name = buf.readString(256);
        final int color = buf.readInt();
        final SecurityType security = SecurityType.values()[buf.readVarInt()];
        final String password = buf.readString(256);
        if (FluxUtils.isLegalPassword(password)) {
            if (FluxNetworkData.get().createNetwork(player, name, color, security, password) != null) {
                S2C_Response(FeedbackInfo.SUCCESS, player);
            } else {
                S2C_Response(FeedbackInfo.NO_SPACE, player);
            }
        } else {
            S2C_Response(FeedbackInfo.ILLEGAL_PASSWORD, player);
        }
    }

    private static void deleteNetwork(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (network.isValid()) {
            if (network.getPlayerAccess(player).canDelete()) {
                FluxNetworkData.get().deleteNetwork(network);
                S2C_Response(FeedbackInfo.SUCCESS, player);
            } else {
                S2C_Response(FeedbackInfo.NO_OWNER, player);
            }
        }
    }

    private static void responseAccessUpdate(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        AccessLevel access = network.getPlayerAccess(player);
        S2C_UpdateAccess(access, player);
    }

    private static void editConnections(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        final IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }
        if (!network.getPlayerAccess(player).canEdit()) {
            S2C_Response(FeedbackInfo.NO_ADMIN, player);
            return;
        }
        final int flags = buf.readVarInt();
        final int size = buf.readVarInt();
        if (size == 0) {
            return;
        }
        List<IFluxDevice> toEdit = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            network.getConnectionByPos(FluxUtils.readGlobalPos(buf)).ifPresent(toEdit::add);
        }
        if (toEdit.isEmpty()) {
            return;
        }
        if ((flags & FluxConstants.FLAG_EDIT_DISCONNECT) != 0) {
            toEdit.forEach(IFluxDevice::disconnect);
            sendToPlayer(S2C_UpdateNetwork(network, FluxConstants.TYPE_NET_CONNECTIONS), player);
            S2C_Response(FeedbackInfo.SUCCESS_2, player);
        } else {
            boolean editName = (flags & FluxConstants.FLAG_EDIT_NAME) != 0;
            boolean editPriority = (flags & FluxConstants.FLAG_EDIT_PRIORITY) != 0;
            boolean editLimit = (flags & FluxConstants.FLAG_EDIT_LIMIT) != 0;
            boolean editSurgeMode = (flags & FluxConstants.FLAG_EDIT_SURGE_MODE) != 0;
            boolean editDisableLimit = (flags & FluxConstants.FLAG_EDIT_DISABLE_LIMIT) != 0;
            boolean editChunkLoading = (flags & FluxConstants.FLAG_EDIT_CHUNK_LOADING) != 0;
            String name = null;
            int priority = 0;
            long limit = 0;
            boolean surgeMode = false;
            boolean disableLimit = false;
            boolean chunkLoading = false;
            if (editName) {
                name = buf.readString(0x100);
            }
            if (editPriority) {
                priority = buf.readInt();
            }
            if (editLimit) {
                limit = buf.readLong();
            }
            if (editSurgeMode) {
                surgeMode = buf.readBoolean();
            }
            if (editDisableLimit) {
                disableLimit = buf.readBoolean();
            }
            if (editChunkLoading) {
                chunkLoading = buf.readBoolean();
            }
            boolean sendBannedLoading = false;
            for (IFluxDevice d : toEdit) {
                if (!(d instanceof TileFluxDevice)) {
                    continue;
                }
                TileFluxDevice t = (TileFluxDevice) d;
                if (editName) {
                    t.setCustomName(name);
                }
                if (editPriority) {
                    t.setPriority(priority);
                }
                if (editLimit) {
                    t.setTransferLimit(limit);
                }
                if (editSurgeMode) {
                    t.setSurgeMode(surgeMode);
                }
                if (editDisableLimit) {
                    t.setDisableLimit(disableLimit);
                }
                if (editChunkLoading && !t.getDeviceType().isStorage()) {
                    if (FluxConfig.enableChunkLoading) {
                        if (chunkLoading && !t.isForcedLoading()) {
                            FluxChunkManager.addChunkLoader(t);
                        } else if (!chunkLoading && t.isForcedLoading()) {
                            FluxChunkManager.removeChunkLoader(t);
                        }
                        t.setForcedLoading(FluxChunkManager.isChunkLoader(t));
                    } else {
                        t.setForcedLoading(false);
                        sendBannedLoading = true;
                    }
                }
                t.sendFullUpdatePacket();
            }
            S2C_Response(FeedbackInfo.SUCCESS, player);
            if (sendBannedLoading) {
                S2C_Response(FeedbackInfo.BANNED_LOADING, player);
            }
        }
    }

    private static void responseConnectionUpdate(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        final int networkID = buf.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (!network.isValid()) {
            return;
        }
        int size = buf.readVarInt();
        List<CompoundNBT> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            GlobalPos pos = FluxUtils.readGlobalPos(buf);
            network.getConnectionByPos(pos).ifPresent(c -> {
                CompoundNBT tag = new CompoundNBT();
                c.writeCustomNBT(tag, FluxConstants.TYPE_CONNECTION_UPDATE);
                tags.add(tag);
            });
        }
        if (!tags.isEmpty()) {
            S2C_UpdateConnection(networkID, tags, player);
        }
    }

    private static void configuratorNet(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        int networkID = buf.readVarInt();
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (noAccess(buf.readString(256), player, network))
                return;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() == RegistryItems.FLUX_CONFIGURATOR) {
                CompoundNBT configs = stack.getOrCreateChildTag(FluxConstants.TAG_FLUX_CONFIG);
                configs.putInt(FluxConstants.NETWORK_ID, networkID);
            }
            S2C_Response(FeedbackInfo.SUCCESS, player);
        }
    }

    private static void configuratorEdit(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        String customName = buf.readString(256);
        CompoundNBT tag = buf.readCompoundTag();
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() == RegistryItems.FLUX_CONFIGURATOR) {
            if (tag != null && !tag.isEmpty()) {
                stack.setTagInfo(FluxConstants.TAG_FLUX_CONFIG, tag);
            }
            stack.setDisplayName(new StringTextComponent(customName));
        }
    }

    /**
     * Allocate a heap buffer to write packet data with index.
     *
     * @param index The message index used on the opposite side, range from 0 to 32767
     * @return a byte buf to write the packet data (message)
     */
    @Nonnull
    private static PacketBuffer buffer(int index) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeShort(index);
        return buffer;
    }

    /**
     * Send a message to server
     * <p>
     * This is the only method to be called on the client, the rest needs
     * to be called on the server side
     */
    @OnlyIn(Dist.CLIENT)
    private static void sendToServer(@Nonnull PacketBuffer buf) {
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        if (connection != null)
            connection.sendPacket(new CCustomPayloadPacket(NAME, buf));
        else
            buf.release();
    }

    /**
     * Send a message to a player
     *
     * @param player the server player
     */
    public static void sendToPlayer(@Nonnull PacketBuffer buf, @Nonnull PlayerEntity player) {
        ((ServerPlayerEntity) player).connection.sendPacket(new SCustomPayloadPlayPacket(NAME, buf));
    }

    /**
     * Send a message to a player
     *
     * @param player the server player
     */
    public static void sendToPlayer(@Nonnull PacketBuffer buf, @Nonnull ServerPlayerEntity player) {
        player.connection.sendPacket(new SCustomPayloadPlayPacket(NAME, buf));
    }

    /**
     * Send a message to all specific players
     *
     * @param players players on server
     */
    public static void sendToPlayers(@Nonnull PacketBuffer buf, @Nonnull Iterable<? extends PlayerEntity> players) {
        final IPacket<?> packet = new SCustomPayloadPlayPacket(NAME, buf);
        for (PlayerEntity player : players)
            ((ServerPlayerEntity) player).connection.sendPacket(packet);
    }

    /**
     * Send a message to all players on the server
     */
    public static void sendToAll(@Nonnull PacketBuffer buf) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList()
                .sendPacketToAllPlayers(new SCustomPayloadPlayPacket(NAME, buf));
    }

    /**
     * Send a message to all players in specified dimension
     *
     * @param dimension dimension that players in
     */
    public static void sendToDimension(@Nonnull PacketBuffer buf, @Nonnull RegistryKey<World> dimension) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList()
                .func_232642_a_(new SCustomPayloadPlayPacket(NAME, buf), dimension);
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
    public static void sendToNearby(@Nonnull PacketBuffer buf, @Nullable PlayerEntity excluded,
                                    double x, double y, double z, double radius,
                                    @Nonnull RegistryKey<World> dimension) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().sendToAllNearExcept(excluded,
                x, y, z, radius, dimension, new SCustomPayloadPlayPacket(NAME, buf));
    }

    /**
     * Send a message to all players tracking the specified entity. If a chunk that player loaded
     * on the client contains the chunk where the entity is located, and then the player is
     * tracking the entity changes.
     *
     * @param entity entity is tracking
     */
    public static void sendToTrackingEntity(@Nonnull PacketBuffer buf, @Nonnull Entity entity) {
        ((ServerWorld) entity.world).getChunkProvider().sendToAllTracking(
                entity, new SCustomPayloadPlayPacket(NAME, buf));
    }

    /**
     * Send a message to all players tracking the specified entity, and also send the message to
     * the entity if it is a player. If a chunk that player loaded on the client contains the
     * chunk where the entity is located, and then the player is tracking the entity changes.
     *
     * @param entity the entity is tracking
     */
    public static void sendToTrackingAndSelf(@Nonnull PacketBuffer buf, @Nonnull Entity entity) {
        ((ServerWorld) entity.world).getChunkProvider().sendToTrackingAndSelf(
                entity, new SCustomPayloadPlayPacket(NAME, buf));
    }

    /**
     * Send a message to all players who loaded the specified chunk
     *
     * @param chunk the chunk that players in
     */
    public static void sendToTrackingChunk(@Nonnull PacketBuffer buf, @Nonnull Chunk chunk) {
        final IPacket<?> packet = new SCustomPayloadPlayPacket(NAME, buf);
        ((ServerWorld) chunk.getWorld()).getChunkProvider().chunkManager.getTrackingPlayers(
                chunk.getPos(), false).forEach(player -> player.connection.sendPacket(packet));
    }

    // request to change flux tile entity settings
    @OnlyIn(Dist.CLIENT)
    public static void C2S_TileEntity(@Nonnull TileFluxDevice tile, byte type) {
        PacketBuffer buf = buffer(0);
        buf.writeBlockPos(tile.getPos());
        buf.writeByte(type);
        tile.writePacket(buf, type);
        sendToServer(buf);
    }

    // request to enable/disable super admin permission
    @OnlyIn(Dist.CLIENT)
    public static void C2S_RequestSuperAdmin() {
        PacketBuffer buf = buffer(1);
        sendToServer(buf);
    }

    // edit member permission
    @OnlyIn(Dist.CLIENT)
    public static void C2S_EditMember(int networkID, UUID playerChanged, int type) {
        PacketBuffer buf = buffer(2);
        buf.writeVarInt(networkID);
        buf.writeUniqueId(playerChanged);
        buf.writeVarInt(type);
        sendToServer(buf);
    }

    // edit network settings
    @OnlyIn(Dist.CLIENT)
    public static void C2S_EditNetwork(int networkID, String name, int color, @Nonnull SecurityType security,
                                       String password) {
        PacketBuffer buf = buffer(3);
        buf.writeVarInt(networkID);
        buf.writeString(name, 256);
        buf.writeInt(color);
        buf.writeVarInt(security.ordinal());
        buf.writeString(password, 256);
        sendToServer(buf);
    }

    // edit wireless mode
    @OnlyIn(Dist.CLIENT)
    public static void C2S_EditWireless(int networkID, int mode) {
        PacketBuffer buf = buffer(4);
        buf.writeVarInt(networkID);
        buf.writeVarInt(mode);
        sendToServer(buf);
    }

    // request a network update
    @OnlyIn(Dist.CLIENT)
    public static void C2S_RequestNetworkUpdate(@Nonnull IFluxNetwork network, int type) {
        PacketBuffer buf = buffer(5);
        buf.writeVarInt(type);
        buf.writeVarInt(1); // size = 1
        buf.writeVarInt(network.getNetworkID());
        sendToServer(buf);
    }

    // request a network update
    @OnlyIn(Dist.CLIENT)
    public static void C2S_RequestNetworkUpdate(@Nonnull Collection<IFluxNetwork> networks, int type) {
        PacketBuffer buf = buffer(5);
        buf.writeVarInt(type);
        buf.writeVarInt(networks.size());
        for (IFluxNetwork network : networks) {
            buf.writeVarInt(network.getNetworkID());
        }
        sendToServer(buf);
    }

    // set (connect to) network for a flux tile entity
    @OnlyIn(Dist.CLIENT)
    public static void C2S_SetNetwork(BlockPos pos, int networkID, String password) {
        PacketBuffer buf = buffer(6);
        buf.writeBlockPos(pos);
        buf.writeVarInt(networkID);
        buf.writeString(password, 256);
        sendToServer(buf);
    }

    // create a flux network
    @OnlyIn(Dist.CLIENT)
    public static void C2S_CreateNetwork(String name, int color, @Nonnull SecurityType security, String password) {
        PacketBuffer buf = buffer(7);
        buf.writeString(name, 256);
        buf.writeInt(color);
        buf.writeVarInt(security.ordinal());
        buf.writeString(password, 256);
        sendToServer(buf);
    }

    // delete a flux network
    @OnlyIn(Dist.CLIENT)
    public static void C2S_DeleteNetwork(int networkID) {
        PacketBuffer buf = buffer(8);
        buf.writeVarInt(networkID);
        sendToServer(buf);
    }

    // request a network access level update for GUI
    @OnlyIn(Dist.CLIENT)
    public static void C2S_RequestAccessUpdate(int networkID) {
        PacketBuffer buf = buffer(9);
        buf.writeVarInt(networkID);
        sendToServer(buf);
    }

    @OnlyIn(Dist.CLIENT)
    public static void C2S_Disconnect(int networkID, @Nonnull List<GlobalPos> list) {
        PacketBuffer buf = buffer(10);
        buf.writeVarInt(networkID);
        buf.writeVarInt(FluxConstants.FLAG_EDIT_DISCONNECT);
        buf.writeVarInt(list.size());
        for (GlobalPos pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        sendToServer(buf);
    }

    @OnlyIn(Dist.CLIENT)
    public static void C2S_EditConnections(int networkID, @Nonnull List<GlobalPos> list, int flags, String name,
                                           int priority, long limit, boolean surgeMode, boolean disableLimit,
                                           boolean chunkLoading) {
        PacketBuffer buf = buffer(10);
        buf.writeVarInt(networkID);
        buf.writeVarInt(flags);
        buf.writeVarInt(list.size());
        for (GlobalPos pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        if ((flags & FluxConstants.FLAG_EDIT_NAME) != 0) {
            buf.writeString(name, 0x100);
        }
        if ((flags & FluxConstants.FLAG_EDIT_PRIORITY) != 0) {
            buf.writeInt(priority);
        }
        if ((flags & FluxConstants.FLAG_EDIT_LIMIT) != 0) {
            buf.writeLong(limit);
        }
        if ((flags & FluxConstants.FLAG_EDIT_SURGE_MODE) != 0) {
            buf.writeBoolean(surgeMode);
        }
        if ((flags & FluxConstants.FLAG_EDIT_DISABLE_LIMIT) != 0) {
            buf.writeBoolean(disableLimit);
        }
        if ((flags & FluxConstants.FLAG_EDIT_CHUNK_LOADING) != 0) {
            buf.writeBoolean(chunkLoading);
        }
        sendToServer(buf);
    }

    @OnlyIn(Dist.CLIENT)
    public static void C2S_RequestConnectionUpdate(int networkID, @Nonnull List<GlobalPos> list) {
        PacketBuffer buf = buffer(11);
        buf.writeVarInt(networkID);
        buf.writeVarInt(list.size());
        for (GlobalPos pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        sendToServer(buf);
    }

    @OnlyIn(Dist.CLIENT)
    public static void C2S_ConfiguratorNet(int id, String password) {
        PacketBuffer buf = buffer(12);
        buf.writeVarInt(id);
        buf.writeString(password, 256);
        sendToServer(buf);
    }

    @OnlyIn(Dist.CLIENT)
    public static void C2S_ConfiguratorEdit(String customName, CompoundNBT tag) {
        PacketBuffer buf = buffer(13);
        buf.writeString(customName, 256);
        buf.writeCompoundTag(tag);
        sendToServer(buf);
    }

    @OnlyIn(Dist.CLIENT)
    private static class C {

        private static void handle(short index, @Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            switch (index) {
                case 0:
                    tileEntity(buf, player);
                    break;
                case 1:
                    response(buf, player);
                    break;
                case 2:
                    updateSuperAdmin(buf, player);
                    break;
                case 3:
                    updateNetwork(buf, player);
                    break;
                case 4:
                    updateAccess(buf, player);
                    break;
                case 5:
                    updateConnection(buf, player);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        ///  HANDLING  \\\

        private static void tileEntity(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            final TileEntity tile = player.world.getTileEntity(buf.readBlockPos());
            if (tile instanceof TileFluxDevice) {
                ((TileFluxDevice) tile).readPacket(buf, buf.readByte());
            }
        }

        private static void response(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            final FeedbackInfo info = FeedbackInfo.values()[buf.readVarInt()];
            final boolean action = info.action();
            final Screen screen = Minecraft.getInstance().currentScreen;
            if (!action) {
                FluxClientCache.setFeedbackText(info);
            } else if (screen instanceof GuiFluxCore) {
                ((GuiFluxCore) screen).onFeedbackAction(info);
            }
        }

        private static void updateSuperAdmin(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            FluxClientCache.superAdmin = buf.readBoolean();
            final Screen screen = Minecraft.getInstance().currentScreen;
            if (screen instanceof GuiFluxAdminHome) {
                ((GuiFluxAdminHome) screen).superAdmin.toggled = FluxClientCache.superAdmin;
            }
        }

        private static void updateNetwork(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            final int type = buf.readVarInt();
            Int2ObjectMap<CompoundNBT> updatedNetworks = new Int2ObjectArrayMap<>();
            final int size = buf.readVarInt();
            if (size == 0) {
                return;
            }
            for (int i = 0; i < size; i++) {
                updatedNetworks.put(buf.readVarInt(), buf.readCompoundTag());
            }
            FluxClientCache.updateNetworks(updatedNetworks, type);
        }

        private static void updateAccess(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            AccessLevel access = AccessLevel.values()[buf.readVarInt()];
            Screen screen = Minecraft.getInstance().currentScreen;
            if (screen instanceof GuiFluxCore) {
                GuiFluxCore gui = (GuiFluxCore) screen;
                gui.accessLevel = access;
            }
        }

        private static void updateConnection(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
            final int networkID = buf.readVarInt();
            final int size = buf.readVarInt();
            final List<CompoundNBT> tags = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                tags.add(buf.readCompoundTag());
            }
            FluxClientCache.updateConnections(networkID, tags);
        }
    }
}
