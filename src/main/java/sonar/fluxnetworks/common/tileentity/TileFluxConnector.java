package sonar.fluxnetworks.common.tileentity;

import mcjty.lib.api.power.IBigPower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import sonar.fluxnetworks.common.tileentity.energy.TileGTEnergy;

@Optional.Interface(iface = "mcjty.lib.api.power.IBigPower", modid = "theoneprobe")
public abstract class TileFluxConnector extends TileGTEnergy implements IBigPower {

    @Override
    public void updateTransfers(EnumFacing... facings) {
        super.updateTransfers(facings);
        boolean sendUpdate = false;
        for (EnumFacing facing : facings) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            boolean b = connections[facing.getIndex()] != 0;
            boolean c = TileEntityHandler.canRenderConnection(tile, facing.getOpposite());
            if (b != c) {
                connections[facing.getIndex()] = (byte) (c ? 1 : 0);
                sendUpdate = true;
            }
        }
        if (sendUpdate) {
            sendPackets();
        }
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getStoredPower() {
        return getTransferBuffer();
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getCapacity() {
        return getMaxTransferLimit();
    }
}
