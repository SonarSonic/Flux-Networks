package sonar.flux.connection;

import sonar.flux.api.tiles.IFlux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PriorityGrouping<T extends IFlux> {

    public final int priority;
    public final List<T> entries;

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
