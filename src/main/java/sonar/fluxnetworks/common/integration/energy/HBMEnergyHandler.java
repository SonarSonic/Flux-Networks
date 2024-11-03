package sonar.fluxnetworks.common.integration.energy;

import api.hbm.energy.IBatteryItem;
import com.hbm.capability.HbmCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

public class HBMEnergyHandler implements ITileEnergyHandler, IItemEnergyHandler {

    public static final HBMEnergyHandler INSTANCE = new HBMEnergyHandler();

    private HBMEnergyHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing side) {
        return false;
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side, boolean simulate) {
        return 0;
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side) {
        return 0;
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return IBatteryItem.class.isAssignableFrom(stack.getItem().getClass());
    }

    @Override
    public boolean canAddEnergy(@Nonnull ItemStack stack) {
        if(IBatteryItem.class.isAssignableFrom(stack.getItem().getClass())) {
            IBatteryItem batteryItem = (IBatteryItem) stack.getItem();
            return batteryItem.getCharge(stack) < batteryItem.getMaxCharge();
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull ItemStack stack) {
        return false;
    }

    //note that this should return the amount of energy transfered to the item
    @Override
    public long addEnergy(long amount, @Nonnull ItemStack stack, boolean simulate) {
        if(IBatteryItem.class.isAssignableFrom(stack.getItem().getClass())) {
            //System.out.println("Attempting to charge: " + stack + " simulate: " + simulate);
            IBatteryItem batteryItem = (IBatteryItem) stack.getItem();
            if (batteryItem.getMaxCharge() <= batteryItem.getCharge(stack)) {
                //System.out.println("max charge for: " + stack);
            } else {
                long missingCharge = batteryItem.getMaxCharge() - batteryItem.getCharge(stack);
                if (missingCharge <= amount) {
                    if (!simulate) {
                        batteryItem.chargeBattery(stack, missingCharge);
                    }
                    //System.out.println("1: " + amount + " : " + batteryItem.getCharge(stack));
                    return missingCharge;
                } else {//missingCharge > amount
                    if (!simulate) {
                        batteryItem.chargeBattery(stack, amount);
                    }
                    //System.out.println("2: " + amount + " : " + batteryItem.getCharge(stack));
                    return amount;
                }
            }
        }
        return 0;
    }

    @Override
    public long removeEnergy(long amount, @Nonnull ItemStack stack) {
        return 0;
    }
}
