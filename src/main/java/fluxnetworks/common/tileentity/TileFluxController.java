package fluxnetworks.common.tileentity;

import fluxnetworks.api.ConnectionType;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.api.tileentity.IFluxController;
import fluxnetworks.api.tileentity.IFluxEnergy;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.connection.NetworkStatistics;
import fluxnetworks.common.connection.transfer.ControllerTransfer;
import fluxnetworks.common.connection.handler.SingleTransferHandler;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.network.ManagedPeripheral;

import java.util.HashMap;
import java.util.Map;

public class TileFluxController extends TileFluxCore implements IFluxController, IFluxEnergy {

    public final SingleTransferHandler handler = new SingleTransferHandler(this, new ControllerTransfer(this));

    public TileFluxController() {
        customName = "Flux Controller";
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.CONTROLLER;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public long addEnergy(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long removeEnergy(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergy() {
        return 0;
    }

    @Override
    public String getPeripheralName() {
        return "flux_controller";
    }
}
