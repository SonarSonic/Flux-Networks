package icyllis.fluxnetworks.common.block.state;

import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;

public class PropertyBoolFacing extends BooleanProperty {

    private Direction facing;

    private PropertyBoolFacing(String name, Direction facing) {
        super(name);
        this.facing = facing;
    }

    public static PropertyBoolFacing create(String name, Direction facing) {
        return new PropertyBoolFacing(name, facing);
    }
}
