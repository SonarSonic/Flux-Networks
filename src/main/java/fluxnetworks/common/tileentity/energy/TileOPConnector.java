package fluxnetworks.common.tileentity.energy;

import mcjty.lib.api.power.IBigPower;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mcjty.lib.api.power.IBigPower", modid = "theoneprobe")
public abstract class TileOPConnector extends TileGTEnergy implements IBigPower {

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getStoredPower(){
        return getTransferHandler().getBuffer();
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getCapacity(){
        return getTransferHandler().getBuffer();
    }
}
