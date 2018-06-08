package sonar.flux.common.tileentity;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import sonar.core.api.IFlexibleGui;
import sonar.core.network.sync.SyncTagType;
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

	public SyncTagType.BOOLEAN wireless_charging = new SyncTagType.BOOLEAN(12);
	public SyncTagType.BOOLEAN main_inventory = (SyncTagType.BOOLEAN) new SyncTagType.BOOLEAN(14).setDefault(true);
	public SyncTagType.BOOLEAN hot_bar = (SyncTagType.BOOLEAN) new SyncTagType.BOOLEAN(15).setDefault(true);
	public SyncTagType.BOOLEAN armour_slot = (SyncTagType.BOOLEAN) new SyncTagType.BOOLEAN(16).setDefault(true);
	public SyncTagType.BOOLEAN baubles_slot = (SyncTagType.BOOLEAN) new SyncTagType.BOOLEAN(17).setDefault(true);
	public SyncTagType.BOOLEAN left_hand = (SyncTagType.BOOLEAN) new SyncTagType.BOOLEAN(18).setDefault(false);
	public SyncTagType.BOOLEAN right_hand = (SyncTagType.BOOLEAN) new SyncTagType.BOOLEAN(19).setDefault(false);

	public final SingleTransferHandler handler = new SingleTransferHandler(this, new ControllerTransfer(this));

	public TileController() {
		super(ConnectionType.CONTROLLER);
		syncList.addParts(wireless_charging,  main_inventory, hot_bar, armour_slot, baubles_slot, left_hand, right_hand);
		customName.setDefault("Flux Controller");
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		switch (id) {
		case 13:
			wireless_charging.writeToBuf(buf);
			break;
		case 14:
			customName.writeToBuf(buf);
			break;
		case 15:
			main_inventory.writeToBuf(buf);
			hot_bar.writeToBuf(buf);
			armour_slot.writeToBuf(buf);
			baubles_slot.writeToBuf(buf);
			left_hand.writeToBuf(buf);
			right_hand.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		switch (id) {
		case 13:
			wireless_charging.readFromBuf(buf);
			break;
		case 14:
			customName.readFromBuf(buf);
			break;
		case 15:
			main_inventory.readFromBuf(buf);
			hot_bar.readFromBuf(buf);
			armour_slot.readFromBuf(buf);
			baubles_slot.readFromBuf(buf);
			left_hand.readFromBuf(buf);
			right_hand.readFromBuf(buf);
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