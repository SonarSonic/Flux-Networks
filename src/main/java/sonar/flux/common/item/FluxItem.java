package sonar.flux.common.item;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.helpers.FontHelper;

public class FluxItem extends Item {
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
		tooltip.add(FontHelper.translate("fluxItem.Info"));
	}
}
