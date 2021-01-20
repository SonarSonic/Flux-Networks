package sonar.fluxnetworks.common.connection.transfer;

import baubles.api.cap.BaublesCapabilities;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.common.handler.ItemEnergyHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FluxControllerHandler extends BasicPointHandler<TileFluxController> {

    // a set of players that have at least one network for wireless charging
    private static final Set<EntityPlayerMP> CHARGING_PLAYERS = new ObjectOpenHashSet<>();

    private static final Predicate<ItemStack> NOT_EMPTY = s -> !s.isEmpty();

    private final Map<EntityPlayerMP, Iterable<WirelessHandler>> players = new HashMap<>();
    private int timer;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(fluxController);
    }

    @Override
    public void onCycleStart() {
        if (!device.isActive() || !WirelessType.ENABLE_WIRELESS.isActivated(device.getNetwork())) {
            demand = 0;
            clearPlayers();
            return;
        }
        if (timer == 0) updatePlayers();
        if ((timer & 0x3) == 2) {
            // keep demand
            demand = chargeAllItems(device.getLogicLimit(), true);
        }
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        timer = ++timer & 0x1f;
    }

    @Override
    public void updateTransfers(@Nonnull EnumFacing... faces) {

    }

    @Override
    public void reset() {
        super.reset();
        clearPlayers();
    }

    @Override
    public long sendToConsumers(long energy, boolean simulate) {
        if (!device.isActive()) return 0;
        if ((timer & 0x3) > 0) return 0;
        if (!WirelessType.ENABLE_WIRELESS.isActivated(device.getNetwork())) return 0;
        return chargeAllItems(energy, simulate);
    }

    private long chargeAllItems(long energy, boolean simulate) {
        long leftover = energy;
        for (Map.Entry<EntityPlayerMP, Iterable<WirelessHandler>> player : players.entrySet()) {
            // dead, or quit game
            if (!player.getKey().isEntityAlive()) {
                continue;
            }
            for (WirelessHandler handler : player.getValue()) {
                leftover = handler.chargeItems(leftover, simulate);
                if (leftover <= 0) {
                    return energy;
                }
            }
        }
        return energy - leftover;
    }

    private void clearPlayers() {
        if (!players.isEmpty()) {
            for (EntityPlayerMP toRemove : players.keySet()) {
                CHARGING_PLAYERS.remove(toRemove);
            }
            players.clear();
        }
    }

    private void updatePlayers() {
        clearPlayers();
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        int wireless = device.getNetwork().getSetting(NetworkSettings.NETWORK_WIRELESS);
        for (NetworkMember p : device.getNetwork().getSetting(NetworkSettings.NETWORK_PLAYERS)) {
            EntityPlayerMP player = playerList.getPlayerByUUID(p.getPlayerUUID());
            //noinspection ConstantConditions
            if (player == null || CHARGING_PLAYERS.contains(player)) {
                continue;
            }
            final InventoryPlayer inv = player.inventory;
            List<WirelessHandler> handlers = new ArrayList<>();
            if (WirelessType.RIGHT_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(() -> new Iterator<ItemStack>() {
                    private byte count;

                    @Override
                    public boolean hasNext() {
                        return count < 1;
                    }

                    @Override
                    public ItemStack next() {
                        count++;
                        return inv.getCurrentItem();
                    }
                }, NOT_EMPTY));
            }
            if (WirelessType.LEFT_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inv.offHandInventory::iterator, NOT_EMPTY));
            }
            if (WirelessType.HOT_BAR.isActivated(wireless)) {
                handlers.add(new WirelessHandler(() -> inv.mainInventory.subList(0, 9).iterator(),
                        stack -> {
                            ItemStack heldItem = inv.getCurrentItem();
                            return !stack.isEmpty() && (heldItem.isEmpty() || heldItem != stack);
                        }));
            }
            if (WirelessType.ARMOR_SLOT.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inv.armorInventory::iterator, NOT_EMPTY));
            }
            if (WirelessType.BAUBLES.isActivated(wireless) && FluxNetworks.proxy.baublesLoaded) {
                if (player.hasCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null)) {
                    IItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
                    handlers.add(new WirelessHandler(() -> new Iterator<ItemStack>() {
                        private int count;

                        @Override
                        public boolean hasNext() {
                            return handler != null && count < handler.getSlots();
                        }

                        @Override
                        public ItemStack next() {
                            if (handler == null) return null;
                            ItemStack next = handler.getStackInSlot(count);
                            count++;
                            return next;
                        }
                    }, NOT_EMPTY));
                }
            }
            players.put(player, handlers);
            CHARGING_PLAYERS.add(player);
        }
    }

    private static class WirelessHandler {

        private final Supplier<Iterator<ItemStack>> supplier;
        private final Predicate<ItemStack> validator;

        WirelessHandler(Supplier<Iterator<ItemStack>> supplier, Predicate<ItemStack> validator) {
            this.supplier = supplier;
            this.validator = validator;
        }

        private long chargeItems(long leftover, boolean simulate) {
            for (Iterator<ItemStack> it = supplier.get(); it != null && it.hasNext(); ) {
                ItemStack stack = it.next();
                IItemEnergyHandler handler;
                if (!validator.test(stack) || (handler = ItemEnergyHandler.getEnergyHandler(stack)) == null) {
                    continue;
                }
                if (handler.canAddEnergy(stack)) {
                    leftover -= handler.addEnergy(leftover, stack, simulate);
                    if (leftover <= 0) {
                        return 0;
                    }
                }
            }
            return leftover;
        }
    }
}
