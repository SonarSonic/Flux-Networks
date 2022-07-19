package sonar.fluxnetworks.common.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.energy.IItemEnergyBridge;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.connection.TransferHandler;
import sonar.fluxnetworks.common.integration.CuriosIntegration;
import sonar.fluxnetworks.common.util.EnergyUtils;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FluxControllerHandler extends TransferHandler {

    private static final Predicate<ItemStack> NOT_EMPTY = s -> !s.isEmpty();

    private final Map<ServerPlayer, Iterable<WirelessHandler>> mPlayers = new HashMap<>();
    private int mTimer;

    private long mDesired;

    final TileFluxController mDevice;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(FluxConfig.defaultLimit);
        mDevice = fluxController;
    }

    @Override
    public void onCycleStart() {
        /*if (!WirelessType.ENABLE_WIRELESS.isActivated(mDevice.getNetwork())) {
            demand = 0;
            clearPlayers();
            return;
        }*/
        if (mTimer == 0) {
            updatePlayers();
        }
        if ((mTimer & 0x3) == 2) {
            // keep demand
            mDesired = chargeAllItems(getLimit(), true);
        }
    }

    @Override
    public void onCycleEnd() {
        mBuffer += mChange = -sendToConsumers(Math.min(mBuffer, getLimit()));
        mTimer = ++mTimer & 0x3f;
    }

    @Override
    public void addToBuffer(long energy) {
        mBuffer += energy;
    }

    @Override
    public long getRequest() {
        return Math.max(mDesired - mBuffer, 0);
    }

    @Override
    public void onNetworkChanged() {
        super.onNetworkChanged();
        mPlayers.clear();
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        super.writeCustomTag(tag, type);
        tag.putLong(FluxConstants.BUFFER, mBuffer);
    }

    private long sendToConsumers(long energy) {
        /*if (!mDevice.isActive()) return 0;*/
        if ((mTimer & 0x3) > 0) return 0;
        //if (!WirelessType.ENABLE_WIRELESS.isActivated(mDevice.getNetwork())) return 0;
        return chargeAllItems(energy, false);
    }

    private long chargeAllItems(long energy, boolean simulate) {
        long leftover = energy;
        for (Map.Entry<ServerPlayer, Iterable<WirelessHandler>> player : mPlayers.entrySet()) {
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
        mPlayers.clear();

        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        for (NetworkMember p : mDevice.getNetwork().getAllMembers()) {
            ServerPlayer player = playerList.getPlayer(p.getPlayerUUID());
            if (player == null) {
                continue;
            }
            FluxPlayer fluxPlayer = FluxUtils.get(player, FluxPlayer.FLUX_PLAYER);
            if (fluxPlayer == null) {
                continue;
            }
            if (fluxPlayer.getWirelessNetwork() != mDevice.getNetworkID()) {
                continue;
            }
            int wireless = fluxPlayer.getWirelessMode();
            if (!WirelessType.ENABLE_WIRELESS.isActivated(wireless)) {
                continue;
            }
            if ((wireless & ~(1 << WirelessType.ENABLE_WIRELESS.ordinal())) == 0) {
                // In this case, handlers will be empty
                continue;
            }
            final Inventory inventory = player.getInventory();
            final List<WirelessHandler> handlers = new ArrayList<>();
            if (WirelessType.MAIN_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(() -> new Iterator<>() {
                    private int mCount;

                    @Override
                    public boolean hasNext() {
                        return mCount < 1;
                    }

                    @Override
                    public ItemStack next() {
                        mCount++;
                        return inventory.getSelected();
                    }
                }, NOT_EMPTY));
            }
            if (WirelessType.OFF_HAND.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inventory.offhand::iterator, NOT_EMPTY));
            }
            if (WirelessType.HOT_BAR.isActivated(wireless)) {
                final List<ItemStack> bar = inventory.items.subList(0, Inventory.getSelectionSize());
                handlers.add(new WirelessHandler(bar::iterator,
                        stack -> {
                            ItemStack heldItem = inventory.getSelected();
                            return !stack.isEmpty() && (heldItem.isEmpty() || heldItem != stack);
                        }));
            }
            if (WirelessType.ARMOR.isActivated(wireless)) {
                handlers.add(new WirelessHandler(inventory.armor::iterator, NOT_EMPTY));
            }
            if (WirelessType.CURIOS.isActivated(wireless) && FluxNetworks.isCuriosLoaded()) {
                final LazyOptional<IItemHandlerModifiable> curios = CuriosIntegration.getEquippedCurios(player);
                handlers.add(new WirelessHandler(() -> {
                    // the lazy optional is not cached by Curios
                    if (curios.isPresent()) {
                        return new Iterator<>() {
                            private final IItemHandler mItemHandler = curios.orElseThrow(IllegalStateException::new);
                            private int mCount;

                            @Override
                            public boolean hasNext() {
                                return mCount < mItemHandler.getSlots();
                            }

                            @Override
                            public ItemStack next() {
                                ItemStack next = mItemHandler.getStackInSlot(mCount);
                                mCount++;
                                return next;
                            }
                        };
                    }
                    return null;
                }, NOT_EMPTY));
            }
            if (!handlers.isEmpty()) {
                mPlayers.put(player, handlers);
            }
        }
    }

    private record WirelessHandler(
            Supplier<Iterator<ItemStack>> stacks,
            Predicate<ItemStack> validator) {

        private long chargeItems(long leftover, boolean simulate) {
            for (Iterator<ItemStack> it = stacks.get(); it != null && it.hasNext(); ) {
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