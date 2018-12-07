package sonar.flux.common.item;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.tabs.GuiTabIndexAdmin;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemAdminConfigurator extends ItemAbstractGui {

	@Nonnull
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		onItemRightClick(world, player, hand);
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

	@Nonnull
	@Override
	public Object getIndexScreen(ItemStack stack, List<EnumGuiTab> tabs) {
		return new GuiTabIndexAdmin(tabs);
	}

	@Override
	public int getViewingNetworkID(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag != null && !tag.hasNoTags()){
			return tag.getInteger("v_id");
		}
		return -1;
	}

	@Override
	public void setViewingNetworkID(ItemStack stack, int networkID) {
		stack.setTagInfo("v_id", new NBTTagInt(networkID));
	}

	public List<EnumGuiTab> getTabs(){
		return Lists.newArrayList(EnumGuiTab.INDEX, EnumGuiTab.ADMIN_NETWORK_SELECTION, EnumGuiTab.CONNECTIONS, EnumGuiTab.NETWORK_STATISTICS, EnumGuiTab.PLAYERS, EnumGuiTab.DEBUG, EnumGuiTab.NETWORK_EDIT, EnumGuiTab.NETWORK_CREATE);
	}
}