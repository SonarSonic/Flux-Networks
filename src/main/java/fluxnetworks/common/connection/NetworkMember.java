package fluxnetworks.common.connection;

import com.mojang.authlib.GameProfile;
import fluxnetworks.api.AccessPermission;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class NetworkMember{

    private UUID playerUUID;
    private UUID offlineUUID;
    private String cachedName;
    private AccessPermission accessPermission;

    NetworkMember() {}

    public NetworkMember(NBTTagCompound nbt) {
        readNetworkNBT(nbt);
    }

    public static NetworkMember createNetworkMember(EntityPlayer player, AccessPermission permissionLevel) {
        NetworkMember t = new NetworkMember();
        GameProfile profile = player.getGameProfile();

        t.playerUUID = EntityPlayer.getUUID(profile);
        t.cachedName = profile.getName();
        t.accessPermission = permissionLevel;

        return t;
    }

    public static NetworkMember createMemberByUsername(String username) {
        NetworkMember t = new NetworkMember();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
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
            t.playerUUID = EntityPlayer.getOfflineUUID(username);
        }
        t.cachedName = username;
        t.accessPermission = AccessPermission.USER;
        return t;
    }

    public String getCachedName() {
        return cachedName;
    }

    public AccessPermission getAccessPermission() {
        return accessPermission;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setAccessPermission(AccessPermission accessPermission) {
        this.accessPermission = accessPermission;
    }

    public void readNetworkNBT(NBTTagCompound nbt) {
        playerUUID = nbt.getUniqueId("playerUUID");
        cachedName = nbt.getString("cachedName");
        accessPermission = AccessPermission.values()[nbt.getByte("playerAccess")];
    }

    public NBTTagCompound writeNetworkNBT(NBTTagCompound nbt) {
        nbt.setUniqueId("playerUUID", playerUUID);
        nbt.setString("cachedName", cachedName);
        nbt.setByte("playerAccess", (byte) accessPermission.ordinal());
        return nbt;
    }
}
