package sonar.flux.api.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.UUID;

public class FluxPlayer implements INBTSyncable {


    private UUID player_uuid_online;
    private UUID player_uuid_offline;

    private String player_name;
    private PlayerAccess player_access;

    FluxPlayer() {
    }

    public static FluxPlayer createFluxPlayer(String username, PlayerAccess access){
        FluxPlayer player = new FluxPlayer();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        boolean isOffline = !server.isServerInOnlineMode();

        if(!isOffline){
            PlayerProfileCache cache = server.getPlayerProfileCache();
            GameProfile profile = cache.getGameProfileForUsername(username);
            if(profile != null){
                player.player_uuid_online = profile.getId();
            }else{
                isOffline = true;
            }
        }
        if(isOffline){
            player.player_uuid_online = new UUID(0,0);
            player.player_uuid_offline = EntityPlayer.getOfflineUUID(username);
        }
        player.player_name = username;
        player.player_access = access;
        return player;
    }

    public static FluxPlayer createFluxPlayer(EntityPlayer ePlayer, PlayerAccess access){
        FluxPlayer player = new FluxPlayer();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        GameProfile profile = ePlayer.getGameProfile();
        if(server.isServerInOnlineMode()){
            player.player_uuid_online = EntityPlayer.getUUID(profile);
        }else{
            player.player_uuid_online = new UUID(0,0);
            player.player_uuid_offline = EntityPlayer.getOfflineUUID(profile.getName());
        }
        player.player_name = profile.getName();
        player.player_access = access;
        return player;
    }

    public FluxPlayer(NBTTagCompound tag) {
        readData(tag, SyncType.SAVE);
    }

    public UUID getOnlineUUID() {
        if(valid(player_uuid_online)){
            return player_uuid_online;
        }
        return getOfflineUUID();
    }

    public UUID getOfflineUUID() {
        if(player_uuid_offline == null){
            player_uuid_offline = EntityPlayer.getOfflineUUID(player_name);
        }
        return player_uuid_offline;
    }

    public String getCachedName() {
        return player_name;
    }

    public PlayerAccess getAccess() {
        return player_access;
    }

    public void setAccess(PlayerAccess access) {
        this.player_access = access;
    }

    @Override
    public void readData(NBTTagCompound nbt, SyncType type) {
        player_uuid_online = nbt.getUniqueId("playerUUID");
        player_name = nbt.getString("cachedName");
        player_access = PlayerAccess.values()[nbt.getByte("playerAccess")];
    }

    @Override
    public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
        nbt.setUniqueId("playerUUID", player_uuid_online);
        nbt.setString("cachedName", player_name);
        nbt.setByte("playerAccess", (byte) player_access.ordinal());
        return nbt;
    }

    public boolean matches(EntityPlayer player, UUID profileUUID){
        if(profileUUID.equals(getOnlineUUID()) || profileUUID.equals(getOfflineUUID())){
            return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj instanceof FluxPlayer) {
            FluxPlayer player = (FluxPlayer) obj;
            return player.player_uuid_online.equals(this.player_uuid_online) && player.player_access.equals(player_access);
        }
        return false;
    }

    public static UUID INVALID = new UUID(0,0);

    public static boolean valid(UUID uuid){
        return uuid != null && !uuid.equals(INVALID);
    }
}
