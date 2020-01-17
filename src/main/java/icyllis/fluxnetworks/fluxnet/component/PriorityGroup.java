package icyllis.fluxnetworks.fluxnet.component;

import icyllis.fluxnetworks.api.tile.IFluxTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PriorityGroup<T extends IFluxTile> {

    public final int priority;
    public final List<T> connectors;

    public PriorityGroup(int priority) {
        this.priority = priority;
        connectors = new ArrayList<>();
    }

    public int getPriority() {
        return priority;
    }

    public List<T> getConnectors() {
        return connectors;
    }

    public static <T extends IFluxTile> PriorityGroup<T> getOrCreateGroup(int priority, List<PriorityGroup<T>> groups) {
        Optional<PriorityGroup<T>> group = groups.stream().filter(g -> g.priority == priority).findFirst();
        if(!group.isPresent()){
            PriorityGroup<T> newGroup = new PriorityGroup<>(priority);
            groups.add(newGroup);
            return newGroup;
        }
        return group.get();
    }
}
