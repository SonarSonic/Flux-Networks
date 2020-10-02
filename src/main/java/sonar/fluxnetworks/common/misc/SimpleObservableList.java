package sonar.fluxnetworks.common.misc;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * List update detective. From SonarCore
 */
@Deprecated
public class SimpleObservableList<T> extends ArrayList<T> {

    public List<IListWatcher<T>> watchers = new ArrayList<>();

    public SimpleObservableList(){}

    public void addWatcher(IListWatcher<T> watcher){
        if(!watchers.contains(watcher))
            watchers.add(watcher);
    }

    public void removeWatcher(IListWatcher<T> watcher){
        watchers.remove(watcher);
    }

    public void updateList(List<T> updatedList){
        List<T> newElements = Lists.newArrayList(updatedList);
        List<T> currentElements = Lists.newArrayList(this);
        currentElements.remove(newElements);

        Iterator<T> it = newElements.iterator();
        while(it.hasNext()){

        }
    }

    @Override
    public boolean add(T t) {
        boolean add = super.add(t);
        if(add){
            watchers.forEach(w -> w.onElementAdded(t));
        }
        return add;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o);
        if(remove){
            watchers.forEach(w -> w.onElementRemoved((T)o));
        }
        return remove;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean addAll = super.addAll(c);
        if(addAll){
            watchers.forEach(IListWatcher::onListChanged);
        }
        return addAll;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean addAll = super.addAll(index, c);
        if(addAll){
            watchers.forEach(IListWatcher::onListChanged);
        }
        return addAll;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removeAll = super.removeAll(c);
        if(removeAll){
            watchers.forEach(IListWatcher::onListChanged);
        }
        return removeAll;
    }

    @Override
    public void clear() {
        forEach(e -> watchers.forEach(w -> w.onElementRemoved(e)));
        super.clear();
    }

    @Override
    public T set(int index, T element) {
        T set = super.set(index, element);
        watchers.forEach(w -> w.onElementRemoved(set));
        watchers.forEach(w -> w.onElementAdded(element));
        return set;
    }

    @Override
    public void add(int index, T element) {
        int original_size = size();
        super.add(index, element);
        if(size() != original_size){
            watchers.forEach(w -> w.onElementAdded(element));
        }
    }

    public interface IListWatcher<T>{

        /**when one specific element has been added*/
        void onElementAdded(@Nullable T added);

        /**when one specific element has been removed*/
        void onElementRemoved(@Nullable T added);

        /**when large amount of elements may have changed*/
        void onListChanged();
    }
}
