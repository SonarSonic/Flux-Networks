package sonar.flux.common.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import sonar.core.listener.ListenerList;
import sonar.core.listener.PlayerListener;
import sonar.flux.api.network.IFluxNetwork;

public class FluxItemListenerEvent extends Event {

    public final ItemStack stack;
    public final int unique_id;
    public final IFluxNetwork network;
    public final ListenerList<PlayerListener> listeners;

    public FluxItemListenerEvent(ItemStack stack, int unique_id, IFluxNetwork network, ListenerList<PlayerListener> listeners) {
        super();
        this.stack = stack;
        this.unique_id = unique_id;
        this.network = network;
        this.listeners = listeners;
    }

    public static class AddConnectionListener extends FluxItemListenerEvent {

        public AddConnectionListener(ItemStack stack, int unique_id, IFluxNetwork network, ListenerList<PlayerListener> listeners) {
            super(stack, unique_id, network, listeners);
        }
    }

    public static class RemoveConnectionListener extends FluxItemListenerEvent {

        public RemoveConnectionListener(ItemStack stack, int unique_id, IFluxNetwork network, ListenerList<PlayerListener> listeners) {
            super(stack, unique_id, network, listeners);
        }
    }

}