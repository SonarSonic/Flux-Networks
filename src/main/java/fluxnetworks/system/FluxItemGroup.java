package fluxnetworks.system;

import fluxnetworks.system.registry.RegistryItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class FluxItemGroup extends ItemGroup {

    public static final FluxItemGroup GROUP = new FluxItemGroup();

    public FluxItemGroup() {
        super(FluxNetworks.MODID);
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(RegistryItems.FLUX);
    }
}
