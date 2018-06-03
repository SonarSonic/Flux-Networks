package sonar.flux.common.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import sonar.core.common.block.SonarBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.ISpecialTooltip;
import sonar.flux.FluxTranslate;
import sonar.flux.common.item.FluxConfigurator;
import sonar.flux.common.tileentity.TileFlux;

import java.util.List;

public abstract class FluxConnection extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	public FluxConnection() {
		super(SonarMaterials.machine, false);
		this.hasSpecialRenderer = true;
	}

	//public boolean dropStandard(IBlockAccess world, BlockPos pos)

	@Override
	public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = hand == null ? ItemStack.EMPTY : player.getHeldItem(hand);
		if (heldItem.isEmpty() || !(heldItem.getItem() instanceof FluxConfigurator)) {
			if (!world.isRemote) {
				TileEntity target = world.getTileEntity(pos);
				if (target instanceof TileFlux) {
					TileFlux flux = (TileFlux) target;
					if (flux.canAccess(player).canView()) {
						flux.openFlexibleGui(player, 0);
					} else {
						player.sendMessage(new TextComponentString(SonarHelper.getProfileByUUID(flux.playerUUID.getUUID()).getName() + " : " + FluxTranslate.ERROR_NO_PERMISSION.t()));
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
		if (target instanceof TileFlux) {
			TileFlux flux = (TileFlux) target;
			flux.onBlockPlacedBy(world, pos, state, player, itemstack);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(CONNECTED, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(CONNECTED) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CONNECTED);
	}
}
