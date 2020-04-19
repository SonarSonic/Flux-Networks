package sonar.fluxnetworks.common.network;

import net.minecraft.world.server.ServerWorld;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.FluxCacheTypes;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;

public class TilePacketHandler {

    public static CompoundNBT getSetNetworkPacket(int id, String password) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, id);
        tag.putString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static Object handleSetNetworkPacket(TileFluxCore tile, PlayerEntity player, CompoundNBT tag) {
        int id = tag.getInt(FluxNetworkData.NETWORK_ID);
        String pass = tag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if(tile.getNetworkID() == id) {
            return null;
        }
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(id);
        if(!network.isInvalid()) {
            if(tile.getConnectionType().isController() && network.getConnections(FluxCacheTypes.controller).size() > 0) {
                return new FeedbackPacket(EnumFeedbackInfo.HAS_CONTROLLER);
            }
            if(!network.getMemberPermission(player).canAccess()) {
                if(pass.isEmpty()) {
                    return new FeedbackPacket(EnumFeedbackInfo.PASSWORD_REQUIRE);
                }
                if (!pass.equals(network.getSetting(NetworkSettings.NETWORK_PASSWORD))) {
                    return new FeedbackPacket(EnumFeedbackInfo.REJECT);
                }
            }
            if(tile.getNetwork() != null && !tile.getNetwork().isInvalid()) {
                tile.getNetwork().queueConnectionRemoval(tile, false);
            }
            tile.playerUUID = PlayerEntity.getUUID(player.getGameProfile());
            network.queueConnectionAddition(tile);
            return new FeedbackPacket(EnumFeedbackInfo.SUCCESS);
        }
        return null;
    }

    public static CompoundNBT getChunkLoadPacket(boolean chunkLoading) {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("c", chunkLoading);
        return tag;
    }

    public static Object handleChunkLoadPacket(TileFluxCore tile, PlayerEntity player, CompoundNBT tag) {
        boolean load = tag.getBoolean("c");
        if(FluxConfig.enableChunkLoading) {
            if (load) {
                boolean p = FluxChunkManager.addChunkLoader((ServerWorld)tile.getWorld(), new ChunkPos(tile.getPos()));
                tile.chunkLoading = p;
                tile.settings_changed = true;
                if(!p) {
                    return new FeedbackPacket(EnumFeedbackInfo.HAS_LOADER);
                }
                return null;
            } else {
                FluxChunkManager.removeChunkLoader((ServerWorld) tile.getWorld(), new ChunkPos(tile.getPos()));
                tile.chunkLoading = false;
                tile.settings_changed = true;
                return null;
            }
        } else {
            tile.chunkLoading = false;
            tile.settings_changed = true;
        }
        return new FeedbackPacket(EnumFeedbackInfo.BANNED_LOADING);
    }
}
