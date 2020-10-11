package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;

/**
 * Two-way tile entity message, the player should be in the same world as the tile entity.
 * The client-to-server way should do security check.
 */
public class TileMessage implements IMessage {

    public static final byte C2S_CUSTOM_NAME = 1;
    public static final byte C2S_PRIORITY = 2;
    public static final byte C2S_LIMIT = 3;
    public static final byte C2S_SURGE_MODE = 4;
    public static final byte C2S_DISABLE_LIMIT = 5;
    public static final byte C2S_CHUNK_LOADING = 6;

    public static final byte S2C_GUI_SYNC = -1;
    public static final byte S2C_STORAGE_ENERGY = -2; // update model data to players who can see it

    private TileFluxDevice tile; // origination side
    private byte type;

    public TileMessage() {
    }

    public TileMessage(@Nonnull TileFluxDevice tile, byte type) {
        this.tile = tile;
        this.type = type;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBlockPos(tile.getPos());
        buffer.writeByte(type);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            buffer.release();
            return;
        }
        BlockPos pos = buffer.readBlockPos();
        TileEntity tile = player.world.getTileEntity(pos);
        if (!(tile instanceof TileFluxDevice)) {
            buffer.release();
            return;
        }
        TileFluxDevice flux = (TileFluxDevice) tile;
        // security check on server
        if (!player.world.isRemote && !flux.canPlayerAccess(player)) {
            buffer.release();
            return;
        }
        flux.readPacket(buffer, buffer.readByte());
        buffer.release();
    }
}
