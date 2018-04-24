package sonar.flux.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import sonar.core.helpers.SonarHelper;
import sonar.flux.api.configurator.FluxConfigurationType;
import sonar.flux.api.configurator.IFluxConfigurable;
import sonar.flux.client.GuiConfigurator;
import sonar.flux.common.containers.ContainerConfigurator;

import javax.annotation.Nonnull;

public class FluxConfigurator extends SonarItem implements IFlexibleGui<ItemStack> {

	public static final String CONFIGS_TAG = "configs";
	public static final String DISABLED_TAG = "disabled";

	@Nonnull
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof IFluxConfigurable) {
				IFluxConfigurable configurable = (IFluxConfigurable) tile;
				if (configurable.canAccess(player).canEdit()) {
					ItemStack stack = hand == null ? ItemStack.EMPTY : player.getHeldItem(hand);
					if (player.isSneaking()) {
						NBTTagCompound configs = stack.getOrCreateSubCompound(CONFIGS_TAG);
						stack.setTagInfo(CONFIGS_TAG, configurable.addConfigs(new NBTTagCompound(), player));
						FontHelper.sendMessage("Copied Configuration", world, player);
					} else {
						NBTTagCompound configs = stack.getOrCreateSubCompound(CONFIGS_TAG);
						if (!configs.hasNoTags()) {
							NBTTagCompound disabled = stack.getOrCreateSubCompound(DISABLED_TAG);
							if (disabled.hasNoTags()) {
								configurable.readConfigs(configs, player);
							} else {
								NBTTagCompound allowed = configs.copy();
								for (FluxConfigurationType type : FluxConfigurationType.values()) {
									if (disabled.getBoolean(type.getNBTName())) {
										allowed.removeTag(type.getNBTName());
									}
								}
								configurable.readConfigs(allowed, player);
							}
						}
						FontHelper.sendMessage("Pasted Configuration", world, player);
					}
					return EnumActionResult.SUCCESS;
				} else {
					FontHelper.sendMessage(SonarHelper.getProfileByUUID(configurable.getConnectionOwner()).getName() + " : " + "You don't have permission to access this network", world, player);
				}
			} else {
				FontHelper.sendMessage("INVALID BLOCK", world, player);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
			SonarCore.instance.guiHandler.openBasicItemStack(false, stack, player, world, player.getPosition(), 0);
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onGuiOpened(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {

	}

	@Override
	public Object getServerElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new ContainerConfigurator(player) : null;
	}

	@Override
	public Object getClientElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new GuiConfigurator(player, obj) : null;
	}
}