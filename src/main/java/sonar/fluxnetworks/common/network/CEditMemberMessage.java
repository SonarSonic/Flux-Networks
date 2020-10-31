package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class CEditMemberMessage implements IMessage {

    private int networkID;
    private UUID playerChanged;
    private int type;

    public CEditMemberMessage() {
    }

    public CEditMemberMessage(int networkID, UUID playerChanged, int type) {
        this.networkID = networkID;
        this.playerChanged = playerChanged;
        this.type = type;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(networkID);
        buffer.writeUniqueId(playerChanged);
        buffer.writeVarInt(type);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null)
            return;

        int networkID = buffer.readVarInt();
        UUID playerChanged = buffer.readUniqueId();
        int type = buffer.readVarInt();
        if (PlayerEntity.getUUID(player.getGameProfile()).equals(playerChanged))
            return;

        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (!network.isValid())
            return;

        if (!network.getPlayerAccess(player).canEdit()) {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_ADMIN), context);
            return;
        }
        // Create new member
        if (type == FluxConstants.TYPE_NEW_MEMBER) {
            PlayerEntity target = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerChanged);
            if (target != null) {
                NetworkMember m = NetworkMember.create(target, AccessLevel.USER);
                network.getMembersMap().put(m.getPlayerUUID(), m);
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
                NetworkHandler.INSTANCE.reply(new SNetworkUpdateMessage(network, FluxConstants.TYPE_NET_MEMBERS), context);
            } else {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.INVALID_USER), context);
            }
        } else {
            Optional<NetworkMember> member = network.getMemberByUUID(playerChanged);
            if (member.isPresent()) {
                NetworkMember p = member.get();
                if (type == FluxConstants.TYPE_SET_ADMIN) {
                    if (!network.getPlayerAccess(player).canDelete()) {
                        NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_OWNER), context);
                        return;
                    }
                    p.setAccessLevel(AccessLevel.ADMIN);
                } else if (type == FluxConstants.TYPE_SET_USER) {
                    p.setAccessLevel(AccessLevel.USER);
                } else if (type == FluxConstants.TYPE_CANCEL_MEMBERSHIP) {
                    network.getMembersMap().remove(playerChanged);
                } else if (type == FluxConstants.TYPE_TRANSFER_OWNERSHIP) {
                    if (!network.getPlayerAccess(player).canDelete()) {
                        NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.NO_OWNER), context);
                        return;
                    }
                                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*/
                    network.getAllMembers().removeIf(f -> f.getAccessLevel().canDelete());
                    network.setOwnerUUID(playerChanged);
                    p.setAccessLevel(AccessLevel.OWNER);
                }
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
                NetworkHandler.INSTANCE.reply(new SNetworkUpdateMessage(network, FluxConstants.TYPE_NET_MEMBERS), context);
            } else if (type == FluxConstants.TYPE_TRANSFER_OWNERSHIP) {
                PlayerEntity target = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerChanged);
                if (target != null) {
                                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*/
                    network.getAllMembers().removeIf(f -> f.getAccessLevel().canDelete());
                    NetworkMember m = NetworkMember.create(target, AccessLevel.OWNER);
                    network.getMembersMap().put(m.getPlayerUUID(), m);
                    network.setOwnerUUID(playerChanged);
                    NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.SUCCESS), context);
                    NetworkHandler.INSTANCE.reply(new SNetworkUpdateMessage(network, FluxConstants.TYPE_NET_MEMBERS), context);
                } else {
                    NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.INVALID_USER), context);
                }
            } else {
                NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.INVALID_USER), context);
            }
        }
    }
}
