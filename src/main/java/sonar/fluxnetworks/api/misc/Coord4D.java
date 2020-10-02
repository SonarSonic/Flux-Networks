package sonar.fluxnetworks.api.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

//TODO make deprecated
@Deprecated
public class Coord4D {

    private int x, y, z, dimension; //TODO should we change to DimensionType? or perhaps GlobalPos

    public Coord4D(CompoundNBT tag) {
        read(tag);
    }

    public Coord4D(@Nonnull TileEntity tile) {
        x = tile.getPos().getX();
        y = tile.getPos().getY();
        z = tile.getPos().getZ();
        //dimension = tile.getWorld().getDimension().getType().getId();
    }

    public Coord4D(ByteBuf buf) {
        read(buf);
    }

    public void write(@Nonnull CompoundNBT tag) {
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("z", z);
        tag.putInt("dimension", dimension);
    }

    public void read(@Nonnull CompoundNBT tag) {
        x = tag.getInt("x");
        y = tag.getInt("y");
        z = tag.getInt("z");
        dimension = tag.getInt("dimension");
    }

    public void write(@Nonnull ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(dimension);
    }

    public void read(@Nonnull ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        dimension = buf.readInt();
    }

    public String getStringInfo() {
        return "X: " + x + " Y: " + y + " Z: " + z + " Dim: " + dimension;
    }

    public BlockPos getPos(){
        return new BlockPos(x,y,z);
    }

    public int getDimension(){
        return dimension;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Coord4D)) {
            return false;
        }
        Coord4D c = (Coord4D) obj;
        return x == c.x && y == c.y && z == c.z && dimension == c.dimension;
    }
}
