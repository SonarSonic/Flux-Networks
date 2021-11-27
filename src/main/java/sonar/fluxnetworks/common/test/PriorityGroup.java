package sonar.fluxnetworks.common.test;

import sonar.fluxnetworks.common.device.TransferHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A single group that logic points, plugs with same priority
 *
 * @param <T> Logic Flux Point or Plug
 * @deprecated inefficient
 */
@Deprecated
public class PriorityGroup<T extends TransferHandler> {

    /*public static final Comparator<PriorityGroup<?>> DESCENDING_ORDER =
            (a, b) -> Integer.compare(b.mPriority, a.mPriority);*/

    private final int mPriority;

    private final List<T> mItems = new ArrayList<>();

    private PriorityGroup(int priority) {
        mPriority = priority;
    }

    public int getPriority() {
        return mPriority;
    }

    @Nonnull
    public List<T> getItems() {
        return mItems;
    }

    @Nonnull
    public static <T extends TransferHandler> PriorityGroup<T> getOrCreateGroup(int priority,
                                                                                @Nonnull List<PriorityGroup<T>> groups) {
        Optional<PriorityGroup<T>> group = groups.stream().filter(g -> g.mPriority == priority).findFirst();
        if (group.isEmpty()) {
            PriorityGroup<T> newGroup = new PriorityGroup<>(priority);
            groups.add(newGroup);
            return newGroup;
        }
        return group.get();
    }
}
