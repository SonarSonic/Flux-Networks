package fluxnetworks.common.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

import java.util.ArrayList;

public abstract class BlockSidedConnection extends BlockFluxCore {

    public static final PropertyBoolFacing NORTH = PropertyBoolFacing.create("north", Direction.NORTH);
    public static final PropertyBoolFacing EAST = PropertyBoolFacing.create("east", Direction.EAST);
    public static final PropertyBoolFacing SOUTH = PropertyBoolFacing.create("south", Direction.SOUTH);
    public static final PropertyBoolFacing WEST = PropertyBoolFacing.create("west", Direction.WEST);
    public static final PropertyBoolFacing DOWN = PropertyBoolFacing.create("down", Direction.DOWN);
    public static final PropertyBoolFacing UP = PropertyBoolFacing.create("up", Direction.UP);
    public static final ArrayList<PropertyBoolFacing> faces = Lists.newArrayList(DOWN, UP, NORTH, SOUTH, WEST, EAST);

    public BlockSidedConnection(String name) {
        super(name);
    }

    public static class PropertyBoolFacing extends BooleanProperty {

        public Direction facing;

        protected PropertyBoolFacing(String name, Direction facing) {
            super(name);
            this.facing = facing;
        }

        public static PropertyBoolFacing create(String name, Direction facing) {
            return new PropertyBoolFacing(name, facing);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NORTH, SOUTH, WEST, EAST, DOWN, UP);
    }
}
