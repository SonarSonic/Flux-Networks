package sonar.flux.common.item;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.SonarCore;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.api.configurator.FluxConfigurationType;
import sonar.flux.api.configurator.IFluxConfigurable;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.FluxColourHandler;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.tabs.GuiTabIndexConfigurator;
import sonar.flux.common.containers.ContainerFluxItem;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.ListenerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemConfigurator extends SonarItem implements IFluxItemGui {

	public static final String CONFIGS_TAG = "configs";
	public static final String DISABLED_TAG = "disabled";

	@Nonnull
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(world.isRemote){
			return EnumActionResult.SUCCESS;
		}
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof IFluxConfigurable)) {
			FontHelper.sendMessage("INVALID BLOCK", world, player);
			return EnumActionResult.FAIL;
		}
		IFluxConfigurable configurable = (IFluxConfigurable) tile;
		if (!configurable.canAccess(player).canEdit()) {
			FontHelper.sendMessage(SonarHelper.getProfileByUUID(configurable.getConnectionOwner()).getName() + " : " + "You don't have permission to access this network", world, player);
			return EnumActionResult.FAIL;
		}

		//// COPY OR PASTE SETTINGS \\\\
		ItemStack stack = player.getHeldItem(hand);

		if (player.isSneaking()) {
			NBTTagCompound disabled = stack.getOrCreateSubCompound(DISABLED_TAG);
			stack.setTagInfo(CONFIGS_TAG, configurable.copyConfiguration(new NBTTagCompound(), disabled, player));
			FontHelper.sendMessage("Copied Configuration", world, player);
		} else {
			NBTTagCompound configs = stack.getOrCreateSubCompound(CONFIGS_TAG);
			if (!configs.hasNoTags()) {
				NBTTagCompound disabled = stack.getOrCreateSubCompound(DISABLED_TAG);
				configurable.pasteConfiguration(configs, disabled, player);
				FontHelper.sendMessage("Pasted Configuration", world, player);
			}else{
				FontHelper.sendMessage("NO CONFIGURATION TO PASTE", world, player);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	public static NBTTagCompound copyConfiguration(TileFlux flux, NBTTagCompound config, NBTTagCompound disabled, EntityPlayer player) {
		for(FluxConfigurationType type : FluxConfigurationType.VALUES){
			type.copy.copyFromTile(config, type.getNBTName(), flux);
		}
		return config;
	}

	public static void pasteConfiguration(TileFlux flux, NBTTagCompound config, NBTTagCompound disabled, EntityPlayer player) {
		for(FluxConfigurationType type : FluxConfigurationType.VALUES){
			if(config.hasKey(type.getNBTName()) && !disabled.getBoolean(type.getNBTName())) {
				type.paste.pasteToTile(config, type.getNBTName(), flux);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
		NBTTagCompound tag = stack.getSubCompound(CONFIGS_TAG);
		if(tag != null) {
			list.add(FluxTranslate.NETWORK_NAME.t() + ": " + FluxColourHandler.getOrRequestNetworkName(tag.getInteger(FluxConfigurationType.NETWORK.getNBTName())));
		}else{
			super.addInformation(stack, world, list, flag);
		}
	}

	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
			SonarCore.instance.guiHandler.openBasicItemStack(false, stack, player, world, player.getPosition(), 0);
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	//// GUI \\\\

	@Override
	public int getViewingNetworkID(ItemStack stack) {
		NBTTagCompound tag = stack.getSubCompound(CONFIGS_TAG);
		if(tag == null || !tag.hasKey(FluxConfigurationType.NETWORK.getNBTName())){
			return -1;
		}
		return tag.getInteger(FluxConfigurationType.NETWORK.getNBTName());
	}

	@Override
	public void setViewingNetworkID(ItemStack stack, int networkID) {
		int oldID = getViewingNetworkID(stack);
		stack.getOrCreateSubCompound(CONFIGS_TAG).setInteger(FluxConfigurationType.NETWORK.getNBTName(), networkID);
		IFluxNetwork old = FluxNetworkCache.instance().getNetwork(oldID);
		IFluxNetwork network = FluxNetworkCache.instance().getNetwork(networkID);
		ListenerHelper.onViewingNetworkChanged(stack, old, network);
	}

	@Override
	public void onGuiOpened(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		ListenerHelper.onPlayerOpenItemGui(obj, player);
		ListenerHelper.onPlayerOpenItemTab(obj, player, EnumGuiTab.INDEX);
	}

	@Override
	public Object getServerElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new ContainerFluxItem(player, obj);
	}

	@Override
	public Object getClientElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		FluxNetworks.proxy.setFluxStack(obj);
		return EnumGuiTab.INDEX.getGuiScreen(Lists.newArrayList(getTabs()));
	}

	@Nonnull
	@Override
	public Object getIndexScreen(ItemStack stack, List<EnumGuiTab> tabs) {
		return new GuiTabIndexConfigurator(tabs);
	}

	public List<EnumGuiTab> getTabs(){
		return Lists.newArrayList(EnumGuiTab.INDEX, EnumGuiTab.NETWORK_SELECTION, EnumGuiTab.CONNECTIONS, EnumGuiTab.NETWORK_STATISTICS, EnumGuiTab.PLAYERS, EnumGuiTab.DEBUG, EnumGuiTab.NETWORK_EDIT, EnumGuiTab.NETWORK_CREATE);
	}
}