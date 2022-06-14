package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class NetworkMember {

    private UUID mPlayerUUID;
    private String mCachedName;
    private AccessLevel mAccessLevel;

    private NetworkMember() {
    }

    public NetworkMember(@Nonnull CompoundTag tag) {
        readNBT(tag);
    }

    @Nonnull
    public static NetworkMember create(@Nonnull Player player, @Nonnull AccessLevel accessLevel) {
        final NetworkMember member = new NetworkMember();
        member.mPlayerUUID = player.getUUID();
        member.mCachedName = player.getGameProfile().getName();
        if (member.mCachedName == null) {
            member.mCachedName = "[Anonymous]";
        }
        member.mAccessLevel = accessLevel;
        return member;
    }

    /*public static NetworkMember createMemberByUsername(String username) {
        NetworkMember t = new NetworkMember();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        boolean isOffline = !server.isServerInOnlineMode();

        if (!isOffline) {
            PlayerProfileCache cache = server.getPlayerProfileCache();
            GameProfile profile = cache.getGameProfileForUsername(username);
            if (profile != null) {
                t.playerUUID = profile.getId();
            } else {
                isOffline = true;
            }
        }
        if (isOffline) {
            t.playerUUID = PlayerEntity.getOfflineUUID(username);
        }
        t.cachedName = username;
        t.accessLevel = AccessLevel.USER;
        return t;
    }*/

    public UUID getPlayerUUID() {
        return mPlayerUUID;
    }

    public String getCachedName() {
        return mCachedName;
    }

    public AccessLevel getAccessLevel() {
        return mAccessLevel;
    }

    public void setAccessLevel(@Nonnull AccessLevel accessLevel) {
        mAccessLevel = accessLevel;
    }

    public void readNBT(@Nonnull CompoundTag tag) {
        mPlayerUUID = tag.getUUID("playerUUID");
        mCachedName = tag.getString("cachedName");
        mAccessLevel = AccessLevel.fromKey(tag.getByte("accessLevel"));
    }

    public void writeNBT(@Nonnull CompoundTag tag) {
        tag.putUUID("playerUUID", mPlayerUUID);
        tag.putString("cachedName", mCachedName);
        tag.putByte("accessLevel", mAccessLevel.getKey());
    }
}
