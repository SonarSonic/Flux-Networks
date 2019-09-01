package fluxnetworks.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class Coord4D {

    int x, y, z, dimension;

    public Coord4D(TileEntity tile) {
        x = tile.getPos().getX();
        y = tile.getPos().getY();
        z = tile.getPos().getZ();
        dimension = tile.getWorld().provider.getDimension();
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
}
