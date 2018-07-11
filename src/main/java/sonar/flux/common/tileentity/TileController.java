package sonar.flux.common.tileentity;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import sonar.core.api.IFlexibleGui;
import sonar.core.sync.ISyncValue;
import sonar.core.sync.SyncRegistry;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.client.gui.tabs.GuiTabControllerIndex;
import sonar.flux.connection.transfer.ControllerTransfer;
import sonar.flux.connection.transfer.handlers.SingleTransferHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class TileController extends TileFlux implements IFlexibleGui, IFluxController {

	public ISyncValue<Boolean> wireless_charging = SyncRegistry.createValue(Boolean.class, value_watcher, "12", false);
	public ISyncValue<Boolean> main_inventory = SyncRegistry.createValue(Boolean.class, value_watcher, "14", true);
	public ISyncValue<Boolean> hot_bar = SyncRegistry.createValue(Boolean.class, value_watcher, "15", true);
	public ISyncValue<Boolean> armour_slot = SyncRegistry.createValue(Boolean.class, value_watcher, "16", true);
	public ISyncValue<Boolean> baubles_slot = SyncRegistry.createValue(Boolean.class, value_watcher, "17", true);
	public ISyncValue<Boolean> left_hand = SyncRegistry.createValue(Boolean.class, value_watcher, "18", false);
	public ISyncValue<Boolean> right_hand = SyncRegistry.createValue(Boolean.class, value_watcher, "19", false);

	public final SingleTransferHandler handler = new SingleTransferHandler(this, new ControllerTransfer(this));

	public TileController() {
		super(ConnectionType.CONTROLLER);
		customName.setValueInternal("Flux Controller");
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		switch (id) {
		case 13:
			wireless_charging.save(buf);
			break;
		case 14:
			customName.save(buf);
			break;
		case 15:
			main_inventory.save(buf);
			hot_bar.save(buf);
			armour_slot.save(buf);
			baubles_slot.save(buf);
			left_hand.save(buf);
			right_hand.save(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		switch (id) {
		case 13:
			wireless_charging.load(buf);
			break;
		case 14:
			customName.load(buf);
			break;
		case 15:
			main_inventory.load(buf);
			hot_bar.load(buf);
			armour_slot.load(buf);
			baubles_slot.load(buf);
			left_hand.load(buf);
			right_hand.load(buf);
			break;
		}
	}

	@Override
	public ItemStack getDisplayStack() {
		return new ItemStack(FluxNetworks.fluxController, 1);
	}

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}

	@Override
	public List<GuiTab> getTabs(){
		return Lists.newArrayList(GuiTab.INDEX, GuiTab.WIRELESS_CHARGING, GuiTab.NETWORK_SELECTION, GuiTab.CONNECTIONS, GuiTab.NETWORK_STATISTICS, GuiTab.PLAYERS, GuiTab.DEBUG, GuiTab.NETWORK_EDIT, GuiTab.NETWORK_CREATE);
	}

	@Override
	@Nonnull
	public Object getIndexScreen(List<GuiTab> tabs){
		return new GuiTabControllerIndex(this, tabs);
	}
}