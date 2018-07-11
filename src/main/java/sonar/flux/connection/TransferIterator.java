package sonar.flux.connection;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.tiles.IFlux;

import java.util.Iterator;
import java.util.List;

public class TransferIterator<T extends IFlux> {

    private Iterator<PriorityGrouping<T>> groupIterator;
    private PriorityGrouping<T> current_group;

    private Iterator<T> fluxIterator;
    private T current_flux;

    private EnergyType energyType;
    private int transferType;
    public boolean completed = false;

    public void update(List<PriorityGrouping<T>> list, EnergyType type, int transferType){
        this.groupIterator = list.iterator();
        this.current_group = null;

        this.fluxIterator = null;
        this.current_flux = null;

        this.energyType = type;
        this.transferType = transferType;
        this.completed = false;

        incrementGroup();
    }

    public boolean incrementGroup(){
        if(groupIterator.hasNext()){
            current_group = groupIterator.next();
            fluxIterator = current_group.getEntries().iterator();
            return incrementFlux();
        }
        completed = true;
        return false;
    }

    public boolean incrementFlux(){
        if(fluxIterator.hasNext()){
            current_flux = fluxIterator.next();
            return canTransfer() || incrementFlux();
        }
        return incrementGroup();
    }

    public boolean canTransfer(){
        if(!current_flux.isActive()){
            return false;
        }
        switch(transferType){
            case 0:
                return current_flux.getTransferHandler().addToNetwork(current_flux.getTransferLimit(), energyType, ActionType.SIMULATE) > 0;
            case 1:
                return current_flux.getTransferHandler().removeFromNetwork(current_flux.getTransferLimit(), energyType, ActionType.SIMULATE) > 0;
            default:
                return false;
        }
    }

    public boolean hasNext() {
        if(completed){
            return false;
        }
        return canTransfer() || incrementFlux();
    }

    public PriorityGrouping<T> getCurrentGroup() {
        return current_group;
    }

    public T getCurrentFlux() {
        return current_flux;
    }

}
