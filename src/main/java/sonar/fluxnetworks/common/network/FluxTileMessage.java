package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;

/**
 * Two-way tile entity message, the player should be in the same world as the tile entity.
 * The client-to-server way should do security check.
 */
public class FluxTileMessage implements IMessage {

    private TileFluxDevice tile; // origination side
    private byte id;

    public FluxTileMessage() {
    }

    public FluxTileMessage(@Nonnull TileFluxDevice tile, byte id) {
        this.tile = tile;
        this.id = id;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBlockPos(tile.getPos());
        buffer.writeByte(id);
        tile.writePacket(buffer, id);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            if (context.getDirection().getOriginationSide().isServer()) {
                buffer.release();
            }
            return;
        }
        BlockPos pos = buffer.readBlockPos();
        TileEntity tile = player.world.getTileEntity(pos);
        if (!(tile instanceof TileFluxDevice)) {
            if (player.world.isRemote) {
                buffer.release();
            }
            return;
        }
        TileFluxDevice flux = (TileFluxDevice) tile;
        // security check on server
        if (!player.world.isRemote && !flux.canPlayerAccess(player)) {
            return;
        }
        byte type = buffer.readByte();
        flux.readPacket(buffer, type);
        if (type == FluxConstants.C2S_CHUNK_LOADING && !FluxConfig.enableChunkLoading) {
            NetworkHandler.INSTANCE.reply(new SFeedbackMessage(FeedbackInfo.BANNED_LOADING), context);
        }
        if (player.world.isRemote) {
            buffer.release();
        }
    }
}
