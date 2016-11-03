package sonar.flux.common.multiparts;
/*
import mcmultipart.raytrace.PartMOP;
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
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxNetworks;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.FluxNetworkCache.ViewingType;

public abstract class FluxConnectionMultipart extends FluxMultipart {

	public FluxConnectionMultipart() {
		super();
	}
	public FluxConnectionMultipart(EntityPlayer player, ConnectionType type) {
		super(type);
		setPlayerUUID(((EntityPlayer) player).getGameProfile().getId());
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (player != null && !getWorld().isRemote) {
			TileEntity target = getWorld().getTileEntity(getPos());
			if (target != null && target instanceof TileEntityFlux) {
				TileEntityFlux flux = (TileEntityFlux) target;
				if (flux.playerUUID.getUUID().equals(player.getGameProfile().getId()) || !flux.getNetwork().isFakeNetwork() && flux.getNetwork().getPlayerAccess(player).canEdit()) {
					FluxNetworks.getServerCache().addViewer(player, ViewingType.NETWORK, flux.getNetwork().getNetworkID());
					player.openGui(FluxNetworks.instance, IGuiTile.ID, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
				} else {
					FontHelper.sendMessage(SonarHelper.getProfileByUUID(flux.playerUUID.getUUID()) + " : " + "You don't have permission to access this network", getWorld(), player);
				}
			}
		}
		return true;
	}

	public void onNeighborTileChange(EnumFacing facing) {
		TileEntity tile = getWorld().getTileEntity(getPos());
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
	public void onLoaded() {
		super.onLoaded();
		updateConnections();
	}
}
*/