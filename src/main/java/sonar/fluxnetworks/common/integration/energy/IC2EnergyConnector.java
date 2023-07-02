package sonar.fluxnetworks.common.integration.energy;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.items.electric.*;
import ic2.api.tiles.IEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.api.energy.IItemEnergyConnector;

import javax.annotation.Nonnull;

public class IC2EnergyConnector implements IBlockEnergyConnector, IItemEnergyConnector {

    public static final IC2EnergyConnector INSTANCE = new IC2EnergyConnector();

    @Override
    public boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return target instanceof IEnergyTile || target instanceof IEnergyStorage;
    }

    @Override
    public boolean canSendTo(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return target instanceof IEnergySink || target instanceof IEnergyStorage;
    }

    @Override
    public boolean canReceiveFrom(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return false;
    }

    @Override
    public long sendTo(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        if (target instanceof IEnergyStorage sink) {
            int before = sink.getStoredEU();
            if (!simulate) {
                return (long) sink.addEnergy((int) Math.min(sink.getMaxEU() - before, amount >> 2)) << 2;
            } else {
                return Math.min(sink.getMaxEU() - before, amount >> 2) << 2;
            }
        } else if (target instanceof IEnergySink sink) {
            int voltage = EnergyNet.INSTANCE.getPowerFromTier(sink.getSinkTier());
            int a = (int) Math.min(amount >> 2, voltage);
            if (simulate) {
                return (long) Math.min(a, sink.getRequestedEnergy()) << 2;
            } else {
                return (long) Math.floor(a - sink.acceptEnergy(side, a, voltage)) << 2;
            }
        }
        return 0;
    }

    @Override
    public long receiveFrom(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        return 0;
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof IElectricItem || stack.getItem() instanceof ICustomElectricItem);
    }

    @Override
    public boolean canSendTo(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof IElectricItem || stack.getItem() instanceof ICustomElectricItem);
    }

    @Override
    public boolean canReceiveFrom(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof IElectricItem || stack.getItem() instanceof ICustomElectricItem);
    }

    @Override
    public long sendTo(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IElectricItemManager manager = getManager(stack);
        return (long) manager.charge(stack, (int) (amount >> 2), 4, false, simulate) << 2;
    }

    @Override
    public long receiveFrom(long amount, @Nonnull ItemStack stack, boolean simulate) {
        IElectricItemManager manager = getManager(stack);
        return (long) manager.discharge(stack, (int) (amount >> 2), 4, false, true, false) << 2;
    }

    public static IElectricItemManager getManager(ItemStack stack) {
        if (stack.getItem() instanceof ICustomElectricItem customElectricItem) {
            IElectricItemManager manager = customElectricItem.getManager(stack);
            if (manager == null) {
                manager = ElectricItem.getBackupManager(stack.getItem());
            }
            return manager;
        }
        return ElectricItem.MANAGER;
    }
}
