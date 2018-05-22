package sonar.flux.connection.transfer;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.ItemStackHelper;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.internal.IEnergyTransfer;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.connection.FluxHelper;

import java.util.*;
import java.util.function.Predicate;

public class ControllerTransfer extends BaseFluxTransfer implements IEnergyTransfer {

	public final TileController controller;

	public ControllerTransfer(TileController tile) {
		super(tile.getNetwork().getDefaultEnergyType());
		this.controller = tile;
	}

	@Override
	public long addToNetwork(long maxTransferRF, ActionType actionType) {
		return 0;
	}


	@Override
	public long removeFromNetwork(long maxTransferRF, ActionType actionType) {
		if (!controller.transmitter.getObject()) {
			return 0;
		}
		long received = 0;
		players: for (EntityPlayer player : getChargeablePlayers()) {
			Map<Iterable<ItemStack>, Predicate<ItemStack>> inventories = getSubInventories(new HashMap<>(), player);
			for(Map.Entry<Iterable<ItemStack>, Predicate<ItemStack>> inventory : inventories.entrySet()){
				for(ItemStack stack : inventory.getKey()){
					IItemEnergyHandler handler;
					if(!inventory.getValue().test(stack) || (handler = FluxHelper.getValidAdditionHandler(stack)) == null) {
						continue;
					}
					long receive = handler.addEnergy(maxTransferRF - received, stack, actionType);
					if(receive > 0) {
						received += receive;
						if (!actionType.shouldSimulate()) {
							removedFromNetwork(receive, getEnergyType());
						}
					}
					if (maxTransferRF - received <= 0) {
						break players;
					}
				}
			}
		}
		return received;
	}

	public List<EntityPlayer> getChargeablePlayers(){
		List<FluxPlayer> playerNames = controller.getNetwork().getPlayers();
		List<EntityPlayer> players = new ArrayList<>();
		for (FluxPlayer player : playerNames) {
			Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player.id);
			if (entity instanceof EntityPlayer) {
				players.add((EntityPlayer) entity);
			}
		}
		return players;
	}

	public static final Predicate<ItemStack> NOT_EMPTY = STACK -> !STACK.isEmpty();

	public Map<Iterable<ItemStack>, Predicate<ItemStack>> getSubInventories(Map<Iterable<ItemStack>, Predicate<ItemStack>> subInventories, EntityPlayer player){
		InventoryPlayer inv = player.inventory;
		ItemStack heldItem = inv.getCurrentItem();

		if(controller.right_hand.getObject() && !heldItem.isEmpty()){
			subInventories.put(Lists.newArrayList(heldItem), NOT_EMPTY);
		}
		if(controller.left_hand.getObject()){
			subInventories.put(inv.offHandInventory, NOT_EMPTY);
		}
		if(controller.armour_slot.getObject()){
			subInventories.put(inv.armorInventory, NOT_EMPTY);
		}
		if(controller.baubles_slot.getObject() && Loader.isModLoaded("baubles")){
			if(player.hasCapability(baubles.api.cap.BaublesCapabilities.CAPABILITY_BAUBLES, null)){
				IItemHandler handler = player.getCapability(baubles.api.cap.BaublesCapabilities.CAPABILITY_BAUBLES, null);
				subInventories.put(() -> new ItemHandlerIterator(handler), NOT_EMPTY);
			}
		}
		if(controller.hot_bar.getObject()){
			subInventories.put(inv.mainInventory.subList(0, 9), stack -> !stack.isEmpty() && (heldItem.isEmpty() || heldItem != stack));
		}
		if(controller.main_inventory.getObject()){
			subInventories.put(inv.mainInventory.subList(9, inv.mainInventory.size()), NOT_EMPTY);
		}
		return subInventories;
	}

	private static class ItemHandlerIterator implements Iterator<ItemStack>{

		private final IItemHandler handler;
		private int count = 0;

		ItemHandlerIterator(IItemHandler handler){
			this.handler = handler;
		}

		@Override
		public boolean hasNext() {
			return count < handler.getSlots();
		}

		@Override
		public ItemStack next() {
			ItemStack next = handler.getStackInSlot(count);
			count++;
			return next;
		}
	}


	@Override
	public ItemStack getDisplayStack() {
		return ItemStackHelper.getBlockItem(controller.getWorld(), controller.getPos());
	}

	@Override
	public EnergyType getEnergyType() {
		return controller.getNetwork().getDefaultEnergyType();
	}

}
