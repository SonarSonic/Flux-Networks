package sonar.flux.api;

import net.minecraft.item.ItemStack;
import sonar.core.api.IFlexibleGui;
import sonar.flux.client.gui.EnumGuiTab;

import javax.annotation.Nonnull;
import java.util.List;

public interface IFluxItemGui extends IFlexibleGui<ItemStack> {

    @Nonnull
    Object getIndexScreen(ItemStack stack, List<EnumGuiTab> tabs);

    int getViewingNetworkID(ItemStack stack);

    void setViewingNetworkID(ItemStack stack, int networkID);

}
