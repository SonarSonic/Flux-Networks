package sonar.flux.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.ConnectedTile;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxNetworks;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.FluxNetworkCache.ViewingType;

public abstract class FluxConnection extends ConnectedTile {

	protected FluxConnection(int target) {
		super(target);
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
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		if (player != null && !world.isRemote) {
			TileEntity target = world.getTileEntity(pos);
			if (target != null && target instanceof TileEntityFlux) {
				TileEntityFlux flux = (TileEntityFlux) target;
				if (flux.playerUUID.getUUID().equals(player.getGameProfile().getId()) || !flux.getNetwork().isFakeNetwork() && flux.getNetwork().getPlayerAccess(player).canEdit()) {
					FluxNetworks.getServerCache().addViewer(player, ViewingType.NETWORK, flux.getNetwork().getNetworkID());
					player.openGui(FluxNetworks.instance, IGuiTile.ID, world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					FontHelper.sendMessage(SonarHelper.getProfileByUUID(flux.playerUUID.getUUID()) + " : " + "You don't have permission to access this network", world, player);
				}
			}
		}
		return true;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) tile;
			flux.updateConnections();
		}
	}

	public boolean checkBlockInDirection(IBlockAccess world, int x, int y, int z, EnumFacing side) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if (tile != null && tile instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) tile;
			return flux.connections[side.getIndex()];
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
				flux.updateConnections();
			}
		}
	}
}
