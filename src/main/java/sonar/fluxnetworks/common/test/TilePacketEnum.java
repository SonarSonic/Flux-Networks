package sonar.fluxnetworks.common.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

@Deprecated
public enum TilePacketEnum {
    /*SET_NETWORK(TilePacketHandler::handleSetNetworkPacket),
    CHUNK_LOADING(TilePacketHandler::handleChunkLoadPacket)*/;

    public IPacketTileHandler handler;

    TilePacketEnum(TilePacketEnum.IPacketTileHandler handler) {
        this.handler = handler;
    }

    public interface IPacketTileHandler {
        Object handlePacket(TileFluxDevice tile, PlayerEntity player, CompoundNBT nbtTag);
    }
}
