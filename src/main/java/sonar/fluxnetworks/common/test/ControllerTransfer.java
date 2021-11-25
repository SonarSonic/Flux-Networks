package sonar.fluxnetworks.common.test;

/*import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.fluxnetworks.common.blockentity.FluxControllerEntity;

import java.util.*;

@Deprecated
public class ControllerTransfer implements IFluxTransfer {

    public final FluxControllerEntity tile;
    private final List<ServerPlayerEntity> players = new ArrayList<>();
    private int timer;

    public ControllerTransfer(FluxControllerEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onCycleStart() {
        *//*if (timer == 0) {
            updatePlayers();
        }
        timer = ++timer & 0x1f;*//*
    }

    @Override
    public void onCycleEnd() {
    }

    *//*@Override
    public long removeEnergy(long amount, boolean simulate) {
        return 0; // we don't extract energy from items
    }*//*

    @Override
    public long sendToTile(long amount, boolean simulate) {
        *//*if ((timer & 3) > 0) {
            return 0;
        }
        *//**//*if((tile.getNetwork().getSetting(NetworkSettings.NETWORK_WIRELESS) & 1) == 0) {
            return 0;
        }*//**//*
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
                    long receive = handler.addEnergy(amount - received, stack, simulate);
                    received += receive;
                    if (amount - received <= 0) {
                        break CYCLE;
                    }
                }
            }
        }
        return received;*//*
        return 0;
    }

    *//*private void updatePlayers() {
        players.clear();
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        for (NetworkMember p : tile.getNetwork().getMemberList()) {
            ServerPlayerEntity player = playerList.getPlayerByUUID(p.getPlayerUUID());
            if (player != null) {
                players.add(player);
            }
        }
    }*//*

    *//*@Override
    public void onEnergyAdded(long amount) {

    }*//*

    @Override
    public void onEnergyReceived(long amount) {

    }

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Override
    public ItemStack getDisplayStack() {
        return ItemStack.EMPTY;
    }

    *//*@Override
    public boolean isInvalid() {
        return tile.isRemoved();
    }*//*
}*/
