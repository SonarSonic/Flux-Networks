package sonar.flux.common.block;

import cofh.redstoneflux.api.IEnergyConnection;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.common.block.SonarBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.integration.SonarLoader;
import sonar.core.utils.ISpecialTooltip;
import sonar.flux.FluxNetworks;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.common.tileentity.TileCable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluxCable extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	public FluxCable() {
		super(SonarMaterials.machine, false);
		this.bounding_box = new AxisAlignedBB(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625);
	}

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool UP = PropertyBool.create("up");

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int i) {
		return new TileCable();
	}

	public boolean checkBlockInDirection(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		TileEntity tile = world.getTileEntity(pos.offset(dir));
		if (tile != null) {
            if (tile instanceof IFlux || tile instanceof TileCable || (SonarLoader.rfLoaded && tile instanceof IEnergyConnection)) {
				return true;
			}
            return FluxNetworks.TRANSFER_HANDLER.getTileHandler(tile, dir) != null;
		}
		return false;
	}

	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, DOWN, UP);
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, World world, List<String> list, @Nullable NBTTagCompound tag) {

	}
}
