package fluxnetworks.common.block;

import fluxnetworks.common.tileentity.TileFluxPlug;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockFluxPlug extends BlockSidedConnection {

    public BlockFluxPlug() {
        super("FluxPlug");
        bounding = new AxisAlignedBB(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluxPlug();
    }
}
