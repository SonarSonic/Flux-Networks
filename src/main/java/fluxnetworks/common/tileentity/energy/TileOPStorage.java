package fluxnetworks.common.tileentity.energy;

import fluxnetworks.api.tileentity.IFluxStorage;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.common.tileentity.TileFluxStorage;
import mcjty.lib.api.power.IBigPower;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mcjty.lib.api.power.IBigPower", modid = "theoneprobe")
public abstract class TileOPStorage extends TileFluxCore implements IFluxStorage, IBigPower {

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getStoredPower(){
        return ((TileFluxStorage)this).energyStored;
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getCapacity(){
        return ((TileFluxStorage)this).maxEnergyStorage;
    }
}
