package sonar.fluxnetworks.common.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

@Deprecated
public class TilePacket extends AbstractPacket {

    private final TilePacketEnum type;

    private final CompoundNBT tag;

    private final Coord4D coord4D;

    public TilePacket(PacketBuffer buf) {
        coord4D = new Coord4D(buf);
        type = TilePacketEnum.values()[buf.readInt()];
        tag = buf.readCompoundTag();
    }

    public TilePacket(TilePacketEnum type, CompoundNBT tag, Coord4D coord4D) {
        this.type = type;
        this.tag = tag;
        this.coord4D = coord4D;
    }

    @Override
    public void encode(PacketBuffer buf) {
        coord4D.write(buf);
        buf.writeInt(type.ordinal());
        buf.writeCompoundTag(tag);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if (player != null) {
            World world = player.getEntityWorld();
            /*if (world.getDimension().getType().getId() != coord4D.getDimension()) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                world = server.getWorld(DimensionType.getById(coord4D.getDimension()));
            }*/
            TileEntity tile = world.getTileEntity(coord4D.getPos());
            if (tile instanceof TileFluxDevice) {
                TileFluxDevice flux = (TileFluxDevice) tile;
                return type.handler.handlePacket(flux, player, tag);
            }
        }
        return null;
    }
}
