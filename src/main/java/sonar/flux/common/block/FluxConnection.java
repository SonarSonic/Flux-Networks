package sonar.flux.common.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxListener;
import sonar.flux.common.item.FluxConfigurator;
import sonar.flux.common.tileentity.TileEntityFlux;

import java.util.List;

public abstract class FluxConnection extends SonarMachineBlock {

	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	public FluxConnection() {
		super(SonarMaterials.machine, false, true);
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean dropStandard(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List<String> list) {
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, World world, List<String> list) {
	}

	@Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		ItemStack heldItem = hand == null ? null : player.getHeldItem(hand);
		if (heldItem.isEmpty() || !(heldItem.getItem() instanceof FluxConfigurator)) {
			if (!world.isRemote) {
				TileEntity target = world.getTileEntity(pos);
				if (target != null && target instanceof TileEntityFlux) {
					TileEntityFlux flux = (TileEntityFlux) target;
					if (flux.canAccess(player)) {
						flux.listeners.addListener(player, FluxListener.FULL_NETWORK);
						player.openGui(FluxNetworks.instance, IGuiTile.ID, world, pos.getX(), pos.getY(), pos.getZ());
					} else {
						FontHelper.sendMessage(SonarHelper.getProfileByUUID(flux.playerUUID.getUUID()).getName() + " : " + "You don't have permission to access this network", world, player);
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemstack) {
		super.onBlockPlacedBy(world, pos, state, player, itemstack);
		TileEntity target = world.getTileEntity(pos);
		if (target != null && target instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) target;
			if (player != null && player instanceof EntityPlayer) {
				flux.setPlayerUUID(((EntityPlayer) player).getGameProfile().getId());
				flux.updateNeighbours(true);
			}
		}
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(CONNECTED, meta == 1);
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(CONNECTED) ? 1 : 0;
	}

	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state) {
		return this.getDefaultState().withProperty(CONNECTED, true);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CONNECTED);
	}
}
