package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

public enum TilePacketEnum {
    SET_NETWORK(TilePacketHandler::handleSetNetworkPacket),
    CHUNK_LOADING(TilePacketHandler::handleChunkLoadPacket);

    public IPacketTileHandler handler;

    TilePacketEnum(TilePacketEnum.IPacketTileHandler handler) {
        this.handler = handler;
    }

    public interface IPacketTileHandler {
        Object handlePacket(TileFluxDevice tile, PlayerEntity player, CompoundNBT nbtTag);
    }
}
