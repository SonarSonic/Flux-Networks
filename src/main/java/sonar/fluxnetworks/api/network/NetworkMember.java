package sonar.fluxnetworks.api.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class NetworkMember{

    private UUID playerUUID;
    private String cachedName;
    private EnumAccessType accessPermission;

    NetworkMember() {}

    public NetworkMember(CompoundNBT nbt) {
        readNetworkNBT(nbt);
    }

    public static NetworkMember createNetworkMember(PlayerEntity player, EnumAccessType permissionLevel) {
        NetworkMember t = new NetworkMember();
        GameProfile profile = player.getGameProfile();

        t.playerUUID = PlayerEntity.getUUID(profile);
        t.cachedName = profile.getName();
        t.accessPermission = permissionLevel;

        return t;
    }

    public static NetworkMember createMemberByUsername(String username) {
        NetworkMember t = new NetworkMember();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        boolean isOffline = !server.isServerInOnlineMode();

        if(!isOffline) {
            PlayerProfileCache cache = server.getPlayerProfileCache();
            GameProfile profile = cache.getGameProfileForUsername(username);
            if(profile != null) {
                t.playerUUID = profile.getId();
            } else {
                isOffline = true;
            }
        }
        if(isOffline) {
            t.playerUUID = PlayerEntity.getOfflineUUID(username);
        }
        t.cachedName = username;
        t.accessPermission = EnumAccessType.USER;
        return t;
    }

    public String getCachedName() {
        return cachedName;
    }

    public EnumAccessType getAccessPermission() {
        return accessPermission;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setAccessPermission(EnumAccessType accessPermission) {
        this.accessPermission = accessPermission;
    }

    public void readNetworkNBT(CompoundNBT nbt) {
        playerUUID = nbt.getUniqueId("playerUUID");
        cachedName = nbt.getString("cachedName");
        accessPermission = EnumAccessType.values()[nbt.getByte("playerAccess")];
    }

    public CompoundNBT writeNetworkNBT(CompoundNBT nbt) {
        nbt.putUniqueId("playerUUID", playerUUID);
        nbt.putString("cachedName", cachedName);
        nbt.putByte("playerAccess", (byte) accessPermission.ordinal());
        return nbt;
    }
}
