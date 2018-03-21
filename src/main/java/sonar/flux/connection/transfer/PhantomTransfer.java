package sonar.flux.connection.transfer;

import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.ItemStackHelper;

public class PhantomTransfer extends BaseFluxTransfer {

	public PhantomTransfer(EnergyType type) {
		super(type);
	}

	@Override
	public ItemStack getDisplayStack() {
		return ItemStack.EMPTY;
	}

}
