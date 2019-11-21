package icyllis.fluxnetworks.system.handler;

import icyllis.fluxnetworks.api.util.ITileEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.concurrent.atomic.AtomicInteger;

public class ForgeEnergyHandler implements ITileEnergyHandler {

    static final ForgeEnergyHandler INSTANCE = new ForgeEnergyHandler();

    @Override
    public boolean match(TileEntity tile, Direction side) {
        return tile.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    public long addEnergy(long amount, TileEntity tile, Direction side, boolean simulate) {
        AtomicInteger r = new AtomicInteger();
        tile.getCapability(CapabilityEnergy.ENERGY, side).ifPresent(s -> {
            if(s.canReceive()) {
                r.set(s.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), simulate));
            }
        });
        return r.get();
    }

    @Override
    public long removeEnergy(long amount, TileEntity tile, Direction side) {
        // no use now
        return 0;
    }
}
