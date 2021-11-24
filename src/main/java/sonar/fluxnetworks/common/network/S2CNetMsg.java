package sonar.fluxnetworks.common.network;

public final class S2CNetMsg {

    //private static final NetworkHandler sNetwork = NetworkHandler.sInstance;

    /*// indices are defined in C2SNetMsg
    static final S2CNetMsg.Functor[] sFunctors = new Functor[]{
            (buf, player) -> tileEntity(buf, player), // 0
            (buf, player) -> responseSuperAdmin(buf, player), // 1
            (buf, sender) -> editMember(buf, sender), // 2
            (buf, player) -> editNetwork(buf, player), // 3
            (buf, player) -> editWireless(buf, player), // 4
            (buf, player) -> responseNetworkUpdate(buf, player), // 5
            (buf, player) -> setNetwork(buf, player), // 6
            (buf, player) -> createNetwork(buf, player), // 7
            (buf, player) -> deleteNetwork(buf, player), // 8
            (buf, player) -> responseAccessUpdate(buf, player), // 9
            (buf, player) -> editConnections(buf, player), // 10
            (buf, player) -> responseConnectionUpdate(buf, player), // 11
            (buf, player) -> configuratorNet(buf, player), // 12
            (buf, player) -> configuratorEdit(buf, player)}; // 13

    @FunctionalInterface
    interface Functor {

        // handle C2S packets
        void f(@Nonnull FriendlyByteBuf payload, @Nonnull ServerPlayer player);
    }

    @Nonnull
    public static NetworkHandler.Broadcaster tileEntity(@Nonnull FluxDeviceEntity tile, byte type) {
        FriendlyByteBuf buf = sNetwork.targetAt(0);
        buf.writeBlockPos(tile.getPos());
        buf.writeByte(type);
        tile.writePacket(buf, type);
        return sNetwork.getBroadcaster(buf);
    }

    private static void feedback(@Nonnull FeedbackInfo info, @Nonnull ServerPlayer player) {
        FriendlyByteBuf buf = sNetwork.targetAt(1);
        buf.writeVarInt(info.ordinal());
        sNetwork.getBroadcaster(buf).sendToPlayer(player);
    }

    // update client super admin state
    @Nonnull
    public static NetworkHandler.Broadcaster updateSuperAdmin(boolean hasPermission) {
        FriendlyByteBuf buf = sNetwork.targetAt(2);
        buf.writeBoolean(hasPermission);
        return sNetwork.getBroadcaster(buf);
    }

    // generate lava particles
    @Nonnull
    public static NetworkHandler.Broadcaster lavaEffect(BlockPos pos, int count) {
        FriendlyByteBuf buf = sNetwork.targetAt(3);
        buf.writeBlockPos(pos);
        buf.writeVarInt(count);
        return sNetwork.getBroadcaster(buf);
    }

    // update flux network data
    @Nonnull
    public static NetworkHandler.Broadcaster updateNetwork(@Nonnull IFluxNetwork net, int type) {
        FriendlyByteBuf buf = sNetwork.targetAt(4);
        buf.writeVarInt(type);
        buf.writeVarInt(1); // size = 1
        buf.writeVarInt(net.getNetworkID());
        final CompoundNBT tag = new CompoundNBT();
        net.writeCustomTag(tag, type);
        buf.writeCompoundTag(tag);
        return sNetwork.getBroadcaster(buf);
    }

    // update flux networks data
    @Nonnull
    public static NetworkHandler.Broadcaster updateNetwork(@Nonnull Collection<IFluxNetwork> networks, int type) {
        FriendlyByteBuf buf = sNetwork.targetAt(4);
        buf.writeVarInt(type);
        buf.writeVarInt(networks.size());
        networks.forEach(net -> {
            buf.writeVarInt(net.getNetworkID());
            final CompoundNBT tag = new CompoundNBT();
            net.writeCustomTag(tag, type);
            buf.writeCompoundTag(tag);
        });
        return sNetwork.getBroadcaster(buf);
    }

    private static void updateAccess(@Nonnull AccessLevel access, ServerPlayer player) {
        FriendlyByteBuf buf = sNetwork.targetAt(5);
        buf.writeVarInt(access.ordinal());
        sNetwork.getBroadcaster(buf).sendToPlayer(player);
    }

    private static void updateConnection(int networkID, @Nonnull List<CompoundNBT> tags, ServerPlayer player) {
        FriendlyByteBuf buf = sNetwork.targetAt(6);
        buf.writeVarInt(networkID);
        buf.writeVarInt(tags.size());
        tags.forEach(buf::writeCompoundTag);
        sNetwork.getBroadcaster(buf).sendToPlayer(player);
    }

    ///  HANDLING  \\\

    private static void tileEntity(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        final TileEntity tile = player.world.getTileEntity(buf.readBlockPos());
        if (tile instanceof FluxDeviceEntity) {
            final FluxDeviceEntity flux = (FluxDeviceEntity) tile;
            // security check
            if (!flux.canPlayerAccess(player)) {
                return;
            }
            final byte type = buf.readByte();
            flux.readPacket(buf, type);
            if (type == FluxConstants.C2S_CHUNK_LOADING && !FluxConfig.enableChunkLoading) {
                feedback(FeedbackInfo.BANNED_LOADING, player);
            }
        }
    }

    private static void responseSuperAdmin(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        ISuperAdmin sa = FluxUtils.get(player.getCapability(FluxCapabilities.SUPER_ADMIN));
        if (sa != null && (sa.hasPermission() || SuperAdmin.canActivateSuperAdmin(player))) {
            sa.changePermission();
            if (sa.hasPermission()) {
                feedback(FeedbackInfo.SA_ON, player);
            } else {
                feedback(FeedbackInfo.SA_OFF, player);
            }
            updateSuperAdmin(sa.hasPermission()).sendToPlayer(player);
        }
    }

    private static void editMember(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer sender) {
        final IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }

        final AccessLevel senderAccess = network.getPlayerAccess(sender);
        // check if have permission
        if (!senderAccess.canEdit()) {
            feedback(FeedbackInfo.NO_ADMIN, sender);
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
                feedback(FeedbackInfo.SUCCESS, sender);
                updateNetwork(network, FluxConstants.TYPE_NET_MEMBERS).sendToPlayer(sender);
            } else {
                feedback(FeedbackInfo.INVALID_USER, sender);
            }
        } else if (current.isPresent()) {
            final NetworkMember c = current.get();
            if (self || c.getAccessLevel() == AccessLevel.OWNER) {
                return;
            }
            if (type == FluxConstants.TYPE_SET_ADMIN) {
                // we are not owner or super admin
                if (!senderAccess.canDelete()) {
                    feedback(FeedbackInfo.NO_OWNER, sender);
                    return;
                }
                c.setAccessLevel(AccessLevel.ADMIN);
            } else if (type == FluxConstants.TYPE_SET_USER) {
                c.setAccessLevel(AccessLevel.USER);
            } else if (type == FluxConstants.TYPE_CANCEL_MEMBERSHIP) {
                network.getRawMemberMap().remove(targetUUID);
            } else if (type == FluxConstants.TYPE_TRANSFER_OWNERSHIP) {
                if (!senderAccess.canDelete()) {
                    feedback(FeedbackInfo.NO_OWNER, sender);
                    return;
                }
                *//*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                    .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s
                    .setAccessPermission(AccessPermission.USER));*//*
                network.getAllMembers().removeIf(f -> f.getAccessLevel().canDelete());
                network.setOwnerUUID(targetUUID);
                c.setAccessLevel(AccessLevel.OWNER);
            }
            feedback(FeedbackInfo.SUCCESS, sender);
            updateNetwork(network, FluxConstants.TYPE_NET_MEMBERS).sendToPlayer(sender);
        } else if (type == FluxConstants.TYPE_TRANSFER_OWNERSHIP) {
            if (!senderAccess.canDelete()) {
                feedback(FeedbackInfo.NO_OWNER, sender);
                return;
            }
            // super admin can still transfer ownership to self
            if (self && senderAccess == AccessLevel.OWNER) {
                return;
            }
            PlayerEntity target = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(targetUUID);
            // is online
            if (target != null) {
                *//*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s
                        .setAccessPermission(AccessPermission.USER));*//*
                network.getAllMembers().removeIf(f -> f.getAccessLevel().canDelete());
                NetworkMember m = NetworkMember.create(target, AccessLevel.OWNER);
                network.getRawMemberMap().put(m.getPlayerUUID(), m);
                network.setOwnerUUID(targetUUID);
                feedback(FeedbackInfo.SUCCESS, sender);
                updateNetwork(network, FluxConstants.TYPE_NET_MEMBERS).sendToPlayer(sender);
            } else {
                feedback(FeedbackInfo.INVALID_USER, sender);
            }
        } else {
            feedback(FeedbackInfo.INVALID_USER, sender);
        }
    }

    private static void editNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }
        final String name = buf.readString(256);
        final int color = buf.readInt();
        final SecurityLevel security = SecurityLevel.values()[buf.readVarInt()];
        final String password = buf.readString(256);

        if (network.getPlayerAccess(player).canEdit()) {
            if (!network.getNetworkName().equals(name)) {
                network.setNetworkName(name);
            }
            if (network.getNetworkColor() != color) {
                network.setNetworkColor(color);
                network.getConnections(FluxLogicalType.ANY).forEach(device -> {
                    if (device instanceof FluxDeviceEntity) {
                        ((FluxDeviceEntity) device).sendBlockUpdate();
                    }
                }); // update appearance
            }
            if (FluxUtils.isLegalPassword(password)) {
                network.getSecurity().set(security, password);
            } else {
                feedback(FeedbackInfo.ILLEGAL_PASSWORD, player);
            }
            updateNetwork(network, FluxConstants.TYPE_NET_BASIC).sendToPlayer(player);
            feedback(FeedbackInfo.SUCCESS_2, player);
        } else {
            feedback(FeedbackInfo.NO_ADMIN, player);
        }
    }

    private static void editWireless(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }
        if (network.getPlayerAccess(player).canEdit()) {
            network.setWirelessMode(buf.readVarInt());
            updateNetwork(network, FluxConstants.TYPE_NET_BASIC).sendToPlayer(player);
            feedback(FeedbackInfo.SUCCESS, player);
        } else {
            feedback(FeedbackInfo.NO_ADMIN, player);
        }
    }

    private static void responseNetworkUpdate(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
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
            updateNetwork(networks, type).sendToPlayer(player);
        }
    }

    private static void setNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        TileEntity tile = player.world.getTileEntity(buf.readBlockPos());
        if (!(tile instanceof FluxDeviceEntity)) {
            return;
        }
        FluxDeviceEntity flux = (FluxDeviceEntity) tile;
        final int networkID = buf.readVarInt();
        if (flux.getNetworkID() == networkID) {
            return;
        }
        // we can connect to an invalid network (i.e disconnect)
        final IFluxNetwork network = FluxNetworkData.getNetwork(networkID);

        if (network.isValid() &&
                flux.getDeviceType().isController() &&
                !network.getConnections(FluxLogicalType.CONTROLLER).isEmpty()) {
            feedback(FeedbackInfo.HAS_CONTROLLER, player);
        } else {
            if (network.isValid() && noAccess(buf.readString(256), player, network))
                return;
            if (network.isValid()) {
                flux.setConnectionOwner(PlayerEntity.getUUID(player.getGameProfile()));
            }
            flux.connect(network);
            feedback(FeedbackInfo.SUCCESS, player);
        }
    }

    private static boolean noAccess(String password, ServerPlayer player, @Nonnull IFluxNetwork network) {
        // not a member
        if (!network.getPlayerAccess(player).canUse()) {
            if (network.getSecurity().getLevel() == SecurityLevel.PRIVATE) {
                feedback(FeedbackInfo.REJECT, player);
                return true;
            }
            if (password.isEmpty()) {
                feedback(FeedbackInfo.PASSWORD_REQUIRE, player);
                return true;
            }
            if (!password.equals(network.getSecurity().getPassword())) {
                feedback(FeedbackInfo.REJECT, player);
                return true;
            }
        }
        return false;
    }

    private static void createNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        final String name = buf.readString(256);
        final int color = buf.readInt();
        final SecurityLevel security = SecurityLevel.values()[buf.readVarInt()];
        final String password = buf.readString(256);
        if (FluxUtils.isLegalPassword(password)) {
            if (FluxNetworkData.get().createNetwork(player, name, color, security, password) != null) {
                feedback(FeedbackInfo.SUCCESS, player);
            } else {
                feedback(FeedbackInfo.NO_SPACE, player);
            }
        } else {
            feedback(FeedbackInfo.ILLEGAL_PASSWORD, player);
        }
    }

    private static void deleteNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (network.isValid()) {
            if (network.getPlayerAccess(player).canDelete()) {
                FluxNetworkData.get().deleteNetwork(network);
                feedback(FeedbackInfo.SUCCESS, player);
            } else {
                feedback(FeedbackInfo.NO_OWNER, player);
            }
        }
    }

    private static void responseAccessUpdate(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        AccessLevel access = network.getPlayerAccess(player);
        updateAccess(access, player);
    }

    private static void editConnections(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        final IFluxNetwork network = FluxNetworkData.getNetwork(buf.readVarInt());
        if (!network.isValid()) {
            return;
        }
        if (!network.getPlayerAccess(player).canEdit()) {
            feedback(FeedbackInfo.NO_ADMIN, player);
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
            updateNetwork(network, FluxConstants.TYPE_NET_CONNECTIONS).sendToPlayer(player);
            feedback(FeedbackInfo.SUCCESS_2, player);
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
                if (!(d instanceof FluxDeviceEntity)) {
                    continue;
                }
                FluxDeviceEntity t = (FluxDeviceEntity) d;
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
                t.sendBlockUpdate();
            }
            feedback(FeedbackInfo.SUCCESS, player);
            if (sendBannedLoading) {
                feedback(FeedbackInfo.BANNED_LOADING, player);
            }
        }
    }

    private static void responseConnectionUpdate(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
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
                c.writeCustomTag(tag, FluxConstants.TYPE_CONNECTION_UPDATE);
                tags.add(tag);
            });
        }
        if (!tags.isEmpty()) {
            updateConnection(networkID, tags, player);
        }
    }

    private static void configuratorNet(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
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
            feedback(FeedbackInfo.SUCCESS, player);
        }
    }

    private static void configuratorEdit(@Nonnull FriendlyByteBuf buf, @Nonnull ServerPlayer player) {
        String customName = buf.readString(256);
        CompoundNBT tag = buf.readCompoundTag();
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() == RegistryItems.FLUX_CONFIGURATOR) {
            if (tag != null && !tag.isEmpty()) {
                stack.setTagInfo(FluxConstants.TAG_FLUX_CONFIG, tag);
            }
            stack.setDisplayName(new StringTextComponent(customName));
        }
    }*/
}
