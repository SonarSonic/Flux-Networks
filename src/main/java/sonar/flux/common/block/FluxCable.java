package sonar.flux.common.block;

import cofh.redstoneflux.api.IEnergyConnection;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.integration.SonarLoader;
import sonar.flux.FluxNetworks;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.common.tileentity.TileCable;

import javax.annotation.Nonnull;

public class FluxCable extends SonarMachineBlock {

	private AxisAlignedBB bounds = FULL_BLOCK_AABB;

	public FluxCable() {
		super(SonarMaterials.machine, false, true);
        this.setBlockBounds(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625);
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

	@Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		return false;
	}

	public boolean hasSpecialRenderer() {
		return true;
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

	/*public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}*/

	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	/*public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return state.withProperty(NORTH, checkBlockInDirection(w, pos, EnumFacing.NORTH)).withProperty(SOUTH, checkBlockInDirection(w, pos, EnumFacing.SOUTH)).withProperty(WEST, checkBlockInDirection(w, pos, EnumFacing.WEST)).withProperty(EAST, checkBlockInDirection(w, pos, EnumFacing.EAST)).withProperty(UP, checkBlockInDirection(w, pos, EnumFacing.UP)).withProperty(DOWN, checkBlockInDirection(w, pos, EnumFacing.DOWN));
	}*/

	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, DOWN, UP);
	}

	@Override
	public boolean dropStandard(IBlockAccess world, BlockPos pos) {
		return true;
	}
}
