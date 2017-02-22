package sonar.flux.common.block;

import java.util.List;

import net.minecraft.block.properties.IProperty;
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
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.FluxNetworkCache.ViewingType;

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
	public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List list) {}

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
	
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(CONNECTED, meta == 1 ? true : false);
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(CONNECTED) ? 1 : 0;
	}

	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state) {
		return this.getDefaultState().withProperty(CONNECTED, true);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { CONNECTED });
	}
}
