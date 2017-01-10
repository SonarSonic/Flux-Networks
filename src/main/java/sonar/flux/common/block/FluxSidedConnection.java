package sonar.flux.common.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.ConnectedTile;
import sonar.core.common.block.ConnectedTile.PropertySonarFacing;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxNetworks;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.FluxNetworkCache.ViewingType;

public abstract class FluxSidedConnection extends FluxConnection {

	protected FluxSidedConnection() {
		super();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) tile;
			flux.updateConnections();
		}
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		IBlockState currentState = state;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		TileEntity tile = w.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) tile;
			for (PropertySonarFacing face : ConnectedTile.faces) {
				currentState = currentState.withProperty(face, flux.connections[face.facing.getIndex()]);
			}
		}
		return currentState;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { CONNECTED, ConnectedTile.NORTH, ConnectedTile.EAST, ConnectedTile.SOUTH, ConnectedTile.WEST, ConnectedTile.DOWN, ConnectedTile.UP });
	}

}
