package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.network.ChargingType;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.common.integration.CuriosIntegration;
import sonar.fluxnetworks.common.misc.EnergyUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FluxControllerHandler extends BasicPointHandler<TileFluxController> {

    private final Map<ServerPlayerEntity, List<WirelessHandler>> players = new HashMap<>();
    private int timer;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(fluxController);
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        timer = ++timer & 0x1f;
    }

    @Override
    public long sendToConsumers(long energy, boolean simulate) {
        if (!device.isActive()) {
            return 0;
        }
        if ((timer & 0x3) > 0) { //TODO THIS CAUSES FLICKERING INTO GUI, WE COULD SMOOTH THIS OUT (on client side)
            return 0;
        }
        if (!ChargingType.ENABLE_WIRELESS.isActivated(device.getNetwork())) {
            players.clear();
            return 0;
        }
        if (simulate && timer == 0) {
            updatePlayers();
        }
        long leftover = energy;
        for (Map.Entry<ServerPlayerEntity, List<WirelessHandler>> player : players.entrySet()) {
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

    private void updatePlayers() {
        players.clear();
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        int wireless = device.getNetwork().getWirelessMode();
        for (NetworkMember p : device.getNetwork().getAllMembers()) {
            ServerPlayerEntity player = playerList.getPlayerByUUID(p.getPlayerUUID());
            if (player == null) {
                continue;
            }
            PlayerInventory inv = player.inventory;
            List<WirelessHandler> handlers = new ArrayList<>();
            if (ChargingType.MAIN_HAND.isActivated(wireless)) {
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
            if (ChargingType.OFF_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inv.offHandInventory::iterator, NOT_EMPTY));
            }
            if (ChargingType.HOT_BAR.isActivated(wireless)) {
                handlers.add(new WirelessHandler(() -> inv.mainInventory.subList(0, 9).iterator(),
                        stack -> {
                            ItemStack heldItem = inv.getCurrentItem();
                            return !stack.isEmpty() && (heldItem.isEmpty() || heldItem != stack);
                        }));
            }
            if (ChargingType.ARMOR.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inv.armorInventory::iterator, NOT_EMPTY));
            }
            if (ChargingType.CURIOS.isActivated(wireless) && FluxNetworks.curiosLoaded) {
                LazyOptional<IItemHandlerModifiable> curios = CuriosIntegration.getEquippedCurios(player);
                handlers.add(new WirelessHandler(() -> {
                    if (curios.isPresent()) {
                        return new Iterator<ItemStack>() {
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
                    return new Iterator<ItemStack>() {
                        @Override
                        public boolean hasNext() {
                            return false;
                        }

                        @Override
                        public ItemStack next() {
                            return null;
                        }
                    };
                }, NOT_EMPTY));
            }
            players.put(player, handlers);
        }
    }

    private static final Predicate<ItemStack> NOT_EMPTY = s -> !s.isEmpty();

    private static class WirelessHandler {

        private final Supplier<Iterator<ItemStack>> supplier;
        private final Predicate<ItemStack> validator;

        WirelessHandler(Supplier<Iterator<ItemStack>> supplier, Predicate<ItemStack> validator) {
            this.supplier = supplier;
            this.validator = validator;
        }

        public long chargeItems(long leftover, boolean simulate) {
            for (Iterator<ItemStack> it = supplier.get(); it.hasNext(); ) {
                ItemStack stack = it.next();
                IItemEnergyHandler handler;
                if (!validator.test(stack) || (handler = EnergyUtils.getEnergyHandler(stack)) == null) {
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