package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.common.handler.NetworkHandler;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Two-way tile entity message, the player should be in the same world as the tile entity.
 * The client-to-server way should do security check.
 */
public class TTileMessage implements IMessage {

    public static final byte C2S_CUSTOM_NAME = 1;
    public static final byte C2S_PRIORITY = 2;
    public static final byte C2S_LIMIT = 3;
    public static final byte C2S_SURGE_MODE = 4;
    public static final byte C2S_DISABLE_LIMIT = 5;
    public static final byte C2S_CHUNK_LOADING = 6;

    public static final byte S2C_GUI_SYNC = -1;
    public static final byte S2C_STORAGE_ENERGY = -2; // update model data to players who can see it

    private IFluxDevice tile; // origination
    private BlockPos pos;
    private byte id;

    private PacketBuffer buffer; // reception

    public TTileMessage() {
    }

    public TTileMessage(IFluxDevice tile, BlockPos pos, byte id) {
        this.tile = tile;
        this.pos = pos;
        this.id = id;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(id);
        switch (id) {
            case C2S_CUSTOM_NAME:
                buffer.writeString(tile.getCustomName(), 256);
                break;
            case C2S_PRIORITY:
        }
    }

    @Override
    public void decode(@Nonnull PacketBuffer buffer) {
        pos = buffer.readBlockPos();
        id = buffer.readByte();
        buffer.retain(); // for handling
        this.buffer = buffer;
    }

    @Override
    public void handle(@Nonnull Supplier<NetworkEvent.Context> context) {
        PlayerEntity player = NetworkHandler.getPlayer(context.get());
        if (player == null) {
            return;
        }
        TileEntity tile = player.world.getTileEntity(pos);
        if (!(tile instanceof IFluxDevice)) {
            return;
        }
        IFluxDevice flux = (IFluxDevice) tile;
        // security check on server
        if (id > 0 && !flux.canPlayerAccess(player)) {
            return;
        }
        switch (id) {
            case C2S_CUSTOM_NAME:
                flux.setCustomName(buffer.readString(256));
                break;
            case C2S_PRIORITY:
        }
    }
}
