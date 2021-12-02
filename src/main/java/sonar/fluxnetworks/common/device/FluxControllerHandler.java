package sonar.fluxnetworks.common.device;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.IItemEnergyBridge;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.common.integration.CuriosIntegration;
import sonar.fluxnetworks.common.util.EnergyUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

//TODO
public class FluxControllerHandler extends TransferHandler {

    // a set of players that have at least one network for wireless charging
    //TODO use capability
    private static final Set<ServerPlayer> CHARGING_PLAYERS = new ObjectOpenHashSet<>();

    private static final Predicate<ItemStack> NOT_EMPTY = s -> !s.isEmpty();

    private final Map<ServerPlayer, Iterable<WirelessHandler>> players = new HashMap<>();
    private int timer;

    private long mDesired;

    final TileFluxController mDevice;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(FluxConfig.defaultLimit);
        mDevice = fluxController;
    }

    @Override
    public void onCycleStart() {
       /* if (!mDevice.isActive() || !WirelessType.ENABLE_WIRELESS.isActivated(mDevice.getNetwork())) {
            demand = 0;
            clearPlayers();
            return;
        }
        if (timer == 0) updatePlayers();
        if ((timer & 0x3) == 2) {
            // keep demand
            demand = chargeAllItems(mDevice.getLogicLimit(), true);
        }*/
    }

    @Override
    public void onCycleEnd() {
        mBuffer += mChange = -sendToConsumers(Math.min(mBuffer, getLimit()), false);
        timer = ++timer & 0x1f;
    }

    @Override
    public void insert(long energy) {
        mBuffer += energy;
    }

    @Override
    public long getRequest() {
        return Math.max(mDesired - mBuffer, 0);
    }

    @Override
    public void clearLocalStates() {
        super.clearLocalStates();
        clearPlayers();
    }

    private long sendToConsumers(long energy, boolean simulate) {
        /*if (!mDevice.isActive()) return 0;
        if ((timer & 0x3) > 0) return 0;
        if (!WirelessType.ENABLE_WIRELESS.isActivated(mDevice.getNetwork())) return 0;*/
        return chargeAllItems(energy, simulate);
    }

    private long chargeAllItems(long energy, boolean simulate) {
        long leftover = energy;
        for (Map.Entry<ServerPlayer, Iterable<WirelessHandler>> player : players.entrySet()) {
            // dead, or quit game
            if (!player.getKey().isAlive()) {
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
            for (ServerPlayer toRemove : players.keySet()) {
                CHARGING_PLAYERS.remove(toRemove);
            }
            players.clear();
        }
    }

    private void updatePlayers() {
        clearPlayers();
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        int wireless = 0;
        for (NetworkMember p : mDevice.getNetwork().getAllMembers()) {
            ServerPlayer player = playerList.getPlayer(p.getPlayerUUID());
            if (player == null || CHARGING_PLAYERS.contains(player)) {
                continue;
            }
            final Inventory inv = player.getInventory();
            List<WirelessHandler> handlers = new ArrayList<>();
            if (WirelessType.MAIN_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(() -> new Iterator<>() {
                    private byte count;

                    @Override
                    public boolean hasNext() {
                        return count < 1;
                    }

                    @Override
                    public ItemStack next() {
                        count++;
                        return inv.getSelected();
                    }
                }, NOT_EMPTY));
            }
            if (WirelessType.OFF_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inv.offhand::iterator, NOT_EMPTY));
            }
            if (WirelessType.HOT_BAR.isActivated(wireless)) {
                handlers.add(new WirelessHandler(() -> inv.items.subList(0, 9).iterator(),
                        stack -> {
                            ItemStack heldItem = inv.getSelected();
                            return !stack.isEmpty() && (heldItem.isEmpty() || heldItem != stack);
                        }));
            }
            if (WirelessType.ARMOR.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inv.armor::iterator, NOT_EMPTY));
            }
            if (WirelessType.CURIOS.isActivated(wireless) && FluxNetworks.isCuriosLoaded()) {
                final LazyOptional<IItemHandlerModifiable> curios = CuriosIntegration.getEquippedCurios(player);
                handlers.add(new WirelessHandler(() -> {
                    // the lazy optional is not cached by Curios
                    if (curios.isPresent()) {
                        return new Iterator<>() {
                            private final IItemHandler handler = curios.orElseThrow(IllegalStateException::new);
                            private int count;

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
                        };
                    }
                    return null;
                }, NOT_EMPTY));
            }
            players.put(player, handlers);
            CHARGING_PLAYERS.add(player);
        }
    }

    private record WirelessHandler(
            Supplier<Iterator<ItemStack>> supplier,
            Predicate<ItemStack> validator) {

        private long chargeItems(long leftover, boolean simulate) {
            for (Iterator<ItemStack> it = supplier.get(); it != null && it.hasNext(); ) {
                ItemStack stack = it.next();
                IItemEnergyBridge handler;
                if (!validator.test(stack) || (handler = EnergyUtils.getBridge(stack)) == null) {
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