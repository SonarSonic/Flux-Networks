package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.api.tiles.IFluxConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A single group that points or plugs with same priority
 *
 * @param <T> Flux Point or Plug
 */
public class PriorityGroup<T extends IFluxConnector> {

    private final int priority;
    private final List<T> connectors;

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

    public static <T extends IFluxConnector> PriorityGroup<T> getOrCreateGroup(int priority, List<PriorityGroup<T>> groups) {
        Optional<PriorityGroup<T>> group = groups.stream().filter(g -> g.priority == priority).findFirst();
        if (!group.isPresent()) {
            PriorityGroup<T> newGroup = new PriorityGroup<>(priority);
            groups.add(newGroup);
            return newGroup;
        }
        return group.get();
    }
}
