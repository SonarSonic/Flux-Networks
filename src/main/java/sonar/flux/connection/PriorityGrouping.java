package sonar.flux.connection;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.transfer.handlers.BaseTransferHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PriorityGrouping<T extends IFlux> {

    public final int priority;
    public final List<T> entries;
    private long current_addition;
    private long current_removal;

    public PriorityGrouping(int priority){
        this.priority = priority;
        this.entries = new ArrayList<>();
    }

    public int getPriority(){
        return priority;
    }

    public List<T> getEntries(){
        return entries;
    }

    public boolean valid(IFlux flux){
        return flux.getCurrentPriority()==priority;
    }

    public int hashCode(){
        return priority;
    }

    //// THESE METHODS ARE TO SIMPLIFY THE TRANSFER PROCESS BY STORING SIMULATED TRANSFERS FOR EVEN DISTRIBUTION AMONGST TILES \\\\

    /**updates the current addition of the plugs, returns the max addition for the group*/
    public <T extends IFlux> long updateTotalAddition(EnergyType type, long add){
        current_addition = 0;
        for(IFlux flux : getEntries()){
            BaseTransferHandler handler = (BaseTransferHandler) flux.getTransferHandler();
            current_addition += handler.current_addition = handler.addToNetwork(add, type, ActionType.SIMULATE);
        }
        return current_addition;
    }

    /**updates the current addition of the plugs, returns the max addition for the group*/
    public <T extends IFlux> long updateTotalRemoval(EnergyType type, long remove){
        current_removal = 0;
        for(IFlux flux : getEntries()){
            BaseTransferHandler handler = (BaseTransferHandler) flux.getTransferHandler();
            current_removal += handler.current_removal = handler.removeFromNetwork(remove, type, ActionType.SIMULATE);
        }
        return current_removal;
    }

    public long getAllowedRemoval(T flux, long to_remove){
        BaseTransferHandler handler = (BaseTransferHandler) flux.getTransferHandler();
        return Math.min((int) Math.ceil(handler.current_removal * ((double) handler.current_removal / current_removal)), to_remove);
    }

    public long getAllowedAddition(T flux, long to_add){
        BaseTransferHandler handler = (BaseTransferHandler) flux.getTransferHandler();
        return Math.min((int) Math.ceil(handler.current_addition * ((double) handler.current_addition / current_addition)), to_add);
    }

    public static <T extends IFlux> PriorityGrouping<T> getOrCreateGrouping(int priority, List<PriorityGrouping<T>> groupings){
        Optional<PriorityGrouping<T>> group = groupings.stream().filter(g -> g.priority == priority).findFirst();
        if(!group.isPresent()){
            PriorityGrouping<T> newGroup = new PriorityGrouping<>(priority);
            groupings.add(newGroup);
            return newGroup;
        }
        return group.get();
    }
}
