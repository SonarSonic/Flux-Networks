package sonar.fluxnetworks.common.connection.transfer;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.common.misc.EnergyUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

import java.util.*;
import java.util.function.Predicate;

public class FluxControllerHandler extends BasicPointHandler<TileFluxController> {

    private final List<ServerPlayerEntity> players = new ArrayList<>();
    private int timer;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(fluxController);
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        if (timer == 0) {
            updatePlayers();
        }
        timer = ++timer & 0x1f;
    }

    @Override
    public long sendToConsumers(long energy, boolean simulate) {
        if ((timer & 3) > 0) { //TODO THIS CAUSES FLICKERING INTO GUI, WE COULD SMOOTH THIS OUT (on client side)
            return 0;
        }
        //TODO each player
        /*if((tile.getNetwork().getSetting(NetworkSettings.NETWORK_WIRELESS) & 1) == 0) {
            return 0;
        }*/
        long received = 0;
        CYCLE:
        for (ServerPlayerEntity player : players) {
            // dead, or quit game
            if (!player.isAlive()) {
                continue;
            }
            Map<Iterable<ItemStack>, Predicate<ItemStack>> inventories = getSubInventories(new HashMap<>(), player);
            for (Map.Entry<Iterable<ItemStack>, Predicate<ItemStack>> inventory : inventories.entrySet()) {
                for (ItemStack stack : inventory.getKey()) {
                    IItemEnergyHandler handler;
                    if (!inventory.getValue().test(stack) || (handler = EnergyUtils.getEnergyHandler(stack)) == null) {
                        continue;
                    }
                    long receive = handler.addEnergy(energy - received, stack, simulate);
                    received += receive;
                    if (energy - received <= 0) {
                        break CYCLE;
                    }
                }
            }
        }
        return received;
    }

    private void updatePlayers() {
        players.clear();
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        for (NetworkMember p : device.getNetwork().getMemberList()) {
            ServerPlayerEntity player = playerList.getPlayerByUUID(p.getPlayerUUID());
            if (player != null) {
                players.add(player);
            }
        }
    }

    private static final Predicate<ItemStack> NOT_EMPTY = STACK -> !STACK.isEmpty();

    private Map<Iterable<ItemStack>, Predicate<ItemStack>> getSubInventories(Map<Iterable<ItemStack>, Predicate<ItemStack>> subInventories, PlayerEntity player) {
        PlayerInventory inv = player.inventory;
        ItemStack heldItem = inv.getCurrentItem();

        //TODO
        int wireless = 0/*tile.getNetwork().getSetting(NetworkSettings.NETWORK_WIRELESS)*/;
        if ((wireless >> 1 & 1) == 1) {
            subInventories.put(Lists.newArrayList(heldItem), NOT_EMPTY);
        }
        if ((wireless >> 2 & 1) == 1) {
            subInventories.put(inv.offHandInventory, NOT_EMPTY);
        }
        if ((wireless >> 3 & 1) == 1) {
            subInventories.put(inv.mainInventory.subList(0, 9), stack -> !stack.isEmpty() && (heldItem.isEmpty() || heldItem != stack));
        }
        if ((wireless >> 4 & 1) == 1) {
            subInventories.put(inv.armorInventory, NOT_EMPTY);
        }
        if ((wireless >> 5 & 1) == 1) {
            /* TODO BAUBLES!
            if(FluxNetworks.proxy.baublesLoaded) {
                if(player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)){
                    IItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
                    subInventories.put(() -> new ItemHandlerIterator(handler), NOT_EMPTY);
                }
            }
            */
        }

        return subInventories;
    }

    private static class ItemHandlerIterator implements Iterator<ItemStack> {

        private final IItemHandler handler;
        private int count = 0;

        ItemHandlerIterator(IItemHandler handler) {
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
}