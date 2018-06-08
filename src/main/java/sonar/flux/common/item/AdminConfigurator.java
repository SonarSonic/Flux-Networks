package sonar.flux.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.FluxListener;
import sonar.flux.client.gui.GuiAdminConfigurator;
import sonar.flux.common.containers.ContainerAdminConfigurator;
import sonar.flux.network.FluxNetworkCache;

import javax.annotation.Nonnull;

public class AdminConfigurator extends SonarItem implements IFlexibleGui<ItemStack> {

	@Nonnull
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.SUCCESS;
	}

	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isCreative()) {
			if (!world.isRemote)
				SonarCore.instance.guiHandler.openBasicItemStack(false, stack, player, world, player.getPosition(), 0);
		} else {
			FontHelper.sendMessage("Hey you can't do that! You're not an admin!", world, player);
		}
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onGuiOpened(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		if (!world.isRemote) {
			FluxNetworkCache.instance().getListenerList().addListener(player, FluxListener.ADMIN);
		}
	}

	@Override
	public Object getServerElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new ContainerAdminConfigurator(player) : null;
	}

	@Override
	public Object getClientElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new GuiAdminConfigurator(player) : null;
	}
}