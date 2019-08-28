package fluxnetworks.common.connection;

import com.mojang.authlib.GameProfile;
import fluxnetworks.api.MemberPermission;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class NetworkMember{

    private UUID playerUUID;
    private String cachedName;
    private MemberPermission permission;

    NetworkMember() {}

    public NetworkMember(NBTTagCompound nbt) {
        readNetworkNBT(nbt);
    }

    public static NetworkMember createNetworkMember(EntityPlayer player, MemberPermission permissionLevel) {
        NetworkMember t = new NetworkMember();
        GameProfile profile = player.getGameProfile();

        t.playerUUID = EntityPlayer.getUUID(profile);
        t.cachedName = profile.getName();
        t.permission = permissionLevel;

        return t;
    }

    public String getCachedName() {
        return cachedName;
    }

    public MemberPermission getPermission() {
        return permission;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPermission(MemberPermission permission) {
        this.permission = permission;
    }

    public void readNetworkNBT(NBTTagCompound nbt) {
        playerUUID = nbt.getUniqueId("playerUUID");
        cachedName = nbt.getString("tempName");
        permission = MemberPermission.values()[nbt.getInteger("permission")];
    }

    public NBTTagCompound writeNetworkNBT(NBTTagCompound nbt) {
        nbt.setUniqueId("playerUUID", playerUUID);
        nbt.setString("tempName", cachedName);
        nbt.setInteger("permission", permission.ordinal());
        return nbt;
    }
}
