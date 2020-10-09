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
public abstract class TileMessage implements IMessage {

    public static final byte C2S_CUSTOM_NAME = 1;
    public static final byte C2S_PRIORITY = 2;
    public static final byte C2S_LIMIT = 3;
    public static final byte C2S_SURGE_MODE = 4;
    public static final byte C2S_DISABLE_LIMIT = 5;
    public static final byte C2S_CHUNK_LOADING = 6;

    public static final byte S2C_GUI_SYNC = -1;
    public static final byte S2C_STORAGE_ENERGY = -2; // update model data to players who can see it

    protected TileFluxDevice tile; // origination side

    public TileMessage() {
    }

    public TileMessage(@Nonnull TileFluxDevice tile) {
        this.tile = tile;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeBlockPos(tile.getPos());
    }

    @Override
    public final void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        BlockPos pos = buffer.readBlockPos();
        TileEntity tile = player.world.getTileEntity(pos);
        if (!(tile instanceof TileFluxDevice)) {
            return;
        }
        TileFluxDevice flux = (TileFluxDevice) tile;
        // security check on server
        if (!player.world.isRemote && !flux.canPlayerAccess(player)) {
            return;
        }
        handle(buffer, flux);
    }

    protected abstract void handle(@Nonnull PacketBuffer buffer, @Nonnull TileFluxDevice flux);

    public static class CCustomName extends TileMessage {

        public CCustomName() {
        }

        public CCustomName(@Nonnull TileFluxDevice tile) {
            super(tile);
        }

        @Override
        public void encode(@Nonnull PacketBuffer buffer) {
            super.encode(buffer);
            buffer.writeString(tile.getCustomName(), 256);
        }

        @Override
        protected void handle(@Nonnull PacketBuffer buffer, @Nonnull TileFluxDevice flux) {
            String name = buffer.readString(256);
            flux.setCustomName(name);
        }
    }

    public static class SGuiSync extends TileMessage {

        public SGuiSync() {
        }

        public SGuiSync(@Nonnull TileFluxDevice tile) {
            super(tile);
        }

        @Override
        public void encode(@Nonnull PacketBuffer buffer) {
            super.encode(buffer);
            buffer.writeBoolean(tile.settings_changed);
            if (tile.settings_changed) {
                buffer.writeString(tile.customName, 256);
                buffer.writeInt(tile.priority);
                buffer.writeLong(tile.limit);
                buffer.writeBoolean(tile.surgeMode);
                buffer.writeBoolean(tile.disableLimit);
                buffer.writeBoolean(tile.chunkLoading);
            }
            buffer.writeCompoundTag(tile.getTransferHandler().writeNetworkedNBT(new CompoundNBT()));
        }

        @Override
        protected void handle(@Nonnull PacketBuffer buffer, @Nonnull TileFluxDevice flux) {
            if (buffer.readBoolean()) {
                flux.customName = buffer.readString(256);
                flux.priority = buffer.readInt();
                flux.limit = buffer.readLong();
                flux.surgeMode = buffer.readBoolean();
                flux.disableLimit = buffer.readBoolean();
                flux.chunkLoading = buffer.readBoolean();
            }
            flux.getTransferHandler().readNetworkedNBT(buffer.readCompoundTag());
        }
    }
}
