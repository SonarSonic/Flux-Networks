package sonar.flux.common.block;

import cofh.api.energy.IEnergyConnection;
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
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.integration.SonarLoader;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.common.tileentity.TileEntityCable;

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
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityCable();
	}

	@Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		if (!world.isRemote) {
		}
		return false;
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

/*
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {

		Vec3d vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());		
		
		
		RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
		
		
		this.bounds = ;
		
		return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		return bounds.offset(pos);
	}
	@Deprecated
	public void addCollisionBoxToList(IBlockState blockState, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, blockState.getCollisionBoundingBox(world, pos));
		IBlockState state = blockState.getActualState(world, pos);
		if (state.getValue(NORTH)) {
			AxisAlignedBB sideBox = new AxisAlignedBB(0, 4 * 0.0625, 4 * 0.0625, 1 - (8 * 0.0625), 1 - (4 * 0.0625), 1 - (4 * 0.0625));
			collidingBoxes.add(sideBox);
		}
	}
*/
	public boolean checkBlockInDirection(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		TileEntity tile = world.getTileEntity(pos.offset(dir));
		if (tile != null) {
            if (tile instanceof IFlux || tile instanceof TileEntityCable || (SonarLoader.rfLoaded && tile instanceof IEnergyConnection)) {
				return true;
			}
			if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, dir) != null) {
				return true;
			}
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
