package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class NetworkMember {

    private UUID mPlayerUUID;
    private String mCachedName;
    private AccessLevel mAccessLevel;

    private NetworkMember(@Nonnull UUID uuid, @Nullable String name, @Nonnull AccessLevel access) {
        mPlayerUUID = Objects.requireNonNull(uuid);
        mCachedName = Objects.requireNonNullElse(name, "[Anonymous]");
        mAccessLevel = access;
    }

    public NetworkMember(@Nonnull CompoundTag tag) {
        readNBT(tag);
    }

    @Nonnull
    public static NetworkMember create(@Nonnull Player player, @Nonnull AccessLevel access) {
        return new NetworkMember(player.getUUID(), player.getGameProfile().getName(), access);
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

    @Nonnull
    public UUID getPlayerUUID() {
        return mPlayerUUID;
    }

    @Nonnull
    public String getCachedName() {
        return mCachedName;
    }

    @Nonnull
    public AccessLevel getAccessLevel() {
        return mAccessLevel;
    }

    public boolean setAccessLevel(@Nonnull AccessLevel accessLevel) {
        if (mAccessLevel != accessLevel) {
            mAccessLevel = accessLevel;
            return true;
        }
        return false;
    }

    public void writeNBT(@Nonnull CompoundTag tag) {
        tag.putUUID("playerUUID", mPlayerUUID);
        tag.putString("cachedName", mCachedName);
        tag.putByte("accessLevel", mAccessLevel.getKey());
    }

    public void readNBT(@Nonnull CompoundTag tag) {
        mPlayerUUID = tag.getUUID("playerUUID");
        mCachedName = tag.getString("cachedName");
        mAccessLevel = AccessLevel.fromKey(tag.getByte("accessLevel"));
    }

    @Override
    public String toString() {
        return "NetworkMember{" +
                "uuid=" + mPlayerUUID +
                ", name='" + mCachedName + '\'' +
                ", access=" + mAccessLevel +
                '}';
    }
}
