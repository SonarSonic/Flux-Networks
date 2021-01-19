package sonar.fluxnetworks.common.block;

import com.google.common.collect.Lists;
import sonar.fluxnetworks.common.tileentity.TileFluxConnector;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class BlockSidedConnection extends BlockFluxCore {

    public static final PropertyBoolFacing NORTH = PropertyBoolFacing.create("north", EnumFacing.NORTH);
    public static final PropertyBoolFacing EAST = PropertyBoolFacing.create("east", EnumFacing.EAST);
    public static final PropertyBoolFacing SOUTH = PropertyBoolFacing.create("south", EnumFacing.SOUTH);
    public static final PropertyBoolFacing WEST = PropertyBoolFacing.create("west", EnumFacing.WEST);
    public static final PropertyBoolFacing DOWN = PropertyBoolFacing.create("down", EnumFacing.DOWN);
    public static final PropertyBoolFacing UP = PropertyBoolFacing.create("up", EnumFacing.UP);
    public static final ArrayList<PropertyBoolFacing> faces = Lists.newArrayList(DOWN, UP, NORTH, SOUTH, WEST, EAST);

    public BlockSidedConnection(String name) {
        super(name);
    }

    public static class PropertyBoolFacing extends PropertyBool {

        public EnumFacing facing;

        protected PropertyBoolFacing(String name, EnumFacing facing) {
            super(name);
            this.facing = facing;
        }

        public static PropertyBoolFacing create(String name, EnumFacing facing) {
            return new PropertyBoolFacing(name, facing);
        }
    }

    @Override
    public void observedNeighborChange(IBlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
        super.observedNeighborChange(observerState, world, observerPos, changedBlock, changedBlockPos);
        TileFluxConnector tile = (TileFluxConnector) world.getTileEntity(observerPos);
        if(!tile.getWorld().isRemote) {
            tile.updateTransfers(FluxUtils.getBlockDirection(observerPos, changedBlockPos));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        TileFluxCore tile = (TileFluxCore) worldIn.getTileEntity(pos);
        for(PropertyBoolFacing face : faces) {
            state = state.withProperty(face, tile.connections[face.facing.getIndex()] == 1);
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECTED, NORTH, SOUTH, WEST, EAST, DOWN, UP);
    }
}
