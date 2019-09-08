package fluxnetworks.common.connection;

import baubles.api.cap.BaublesCapabilities;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.energy.IItemEnergyHandler;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.common.handler.ItemEnergyHandler;
import fluxnetworks.common.tileentity.TileFluxController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.function.Predicate;

public class ControllerTransfer implements IFluxTransfer {

    public final TileFluxController tile;
    public List<EntityPlayer> players = new ArrayList<>();
    private int timer;

    public ControllerTransfer(TileFluxController tile) {
        this.tile = tile;
    }

    @Override
    public void onServerStartTick() {
        if(timer == 0) {
            updatePlayers();
        }
        timer++;
        timer %= 20;
    }

    @Override
    public long addToNetwork(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long removeFromNetwork(long amount, boolean simulate) {
        if(!tile.getNetwork().getSetting(NetworkSettings.NETWORK_WIRELESS)) {
            return 0;
        }
        long received = 0;
        CYCLE:
        for(EntityPlayer player : players) {
            Map<Iterable<ItemStack>, Predicate<ItemStack>> inventories = getSubInventories(new HashMap<>(), player);
            for(Map.Entry<Iterable<ItemStack>, Predicate<ItemStack>> inventory : inventories.entrySet()){
                for(ItemStack stack : inventory.getKey()){
                    IItemEnergyHandler handler;
                    if(!inventory.getValue().test(stack) || (handler = ItemEnergyHandler.getEnergyHandler(stack)) == null) {
                        continue;
                    }
                    long receive = handler.addEnergy(amount - received, stack, simulate);
                    received += receive;
                    if (amount - received <= 0) {
                        break CYCLE;
                    }
                }
            }
        }
        return received;
    }

    public void updatePlayers() {
        List<NetworkMember> m = tile.getNetwork().getSetting(NetworkSettings.NETWORK_PLAYERS);
        List<EntityPlayer> players = new ArrayList<>();
        for(NetworkMember p : m) {
            Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(p.getPlayerUUID());
            if (entity instanceof EntityPlayer) {
                players.add((EntityPlayer) entity);
            }
        }
        this.players = players;
    }

    public static final Predicate<ItemStack> NOT_EMPTY = STACK -> !STACK.isEmpty();

    public Map<Iterable<ItemStack>, Predicate<ItemStack>> getSubInventories(Map<Iterable<ItemStack>, Predicate<ItemStack>> subInventories, EntityPlayer player) {
        InventoryPlayer inv = player.inventory;

        subInventories.put(inv.mainInventory.subList(0, 9), stack -> !stack.isEmpty());
        subInventories.put(inv.offHandInventory, NOT_EMPTY);
        subInventories.put(inv.armorInventory, NOT_EMPTY);

        if(FluxNetworks.proxy.baublesLoaded) {
            if(player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)){
                IItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
                subInventories.put(() -> new ItemHandlerIterator(handler), NOT_EMPTY);
            }
        }
        return subInventories;
    }

    private static class ItemHandlerIterator implements Iterator<ItemStack> {

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
    public void addedToNetwork(long amount) {

    }

    @Override
    public void removedFromNetwork(long amount) {

    }

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Override
    public ItemStack getDisplayStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isInvalid() {
        return tile.isInvalid();
    }
}
