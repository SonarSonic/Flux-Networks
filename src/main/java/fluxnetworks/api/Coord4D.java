package fluxnetworks.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class Coord4D {

    private int x, y, z, dimension;

    public Coord4D(NBTTagCompound tag) {
        read(tag);
    }

    public Coord4D(TileEntity tile) {
        x = tile.getPos().getX();
        y = tile.getPos().getY();
        z = tile.getPos().getZ();
        dimension = tile.getWorld().provider.getDimension();
    }

    public Coord4D(ByteBuf buf) {
        read(buf);
    }

    public NBTTagCompound write(NBTTagCompound tag) {
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        tag.setInteger("dimension", dimension);
        return tag;
    }

    public void read(NBTTagCompound tag) {
        x = tag.getInteger("x");
        y = tag.getInteger("y");
        z = tag.getInteger("z");
        dimension = tag.getInteger("dimension");
    }

    public void write(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(dimension);
    }

    public void read(ByteBuf buf) {
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
