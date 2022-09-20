package sonar.fluxnetworks.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.item.*;

import javax.annotation.Nonnull;

public class RegistryItems {
    private static final ResourceLocation FLUX_DUST_KEY = FluxNetworks.rl("flux_dust");
    private static final ResourceLocation FLUX_CORE_KEY = FluxNetworks.rl("flux_core");
    private static final ResourceLocation FLUX_CONFIGURATOR_KEY = FluxNetworks.rl("flux_configurator");
    private static final ResourceLocation ADMIN_CONFIGURATOR_KEY = FluxNetworks.rl("admin_configurator");

    public static final RegistryObject<BlockItem> FLUX_BLOCK = RegistryObject.create(RegistryBlocks.FLUX_BLOCK_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxDeviceItem> FLUX_PLUG = RegistryObject.create(RegistryBlocks.FLUX_PLUG_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxDeviceItem> FLUX_POINT = RegistryObject.create(RegistryBlocks.FLUX_POINT_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxDeviceItem> FLUX_CONTROLLER = RegistryObject.create(RegistryBlocks.FLUX_CONTROLLER_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxStorageItem> BASIC_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.BASIC_FLUX_STORAGE_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxStorageItem> HERCULEAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.HERCULEAN_FLUX_STORAGE_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxStorageItem> GARGANTUAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<FluxDustItem> FLUX_DUST = RegistryObject.create(FLUX_DUST_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<Item> FLUX_CORE = RegistryObject.create(FLUX_CORE_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<ItemFluxConfigurator> FLUX_CONFIGURATOR = RegistryObject.create(FLUX_CONFIGURATOR_KEY, ForgeRegistries.ITEMS);
    public static final RegistryObject<ItemAdminConfigurator> ADMIN_CONFIGURATOR = RegistryObject.create(ADMIN_CONFIGURATOR_KEY, ForgeRegistries.ITEMS);

    private static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab(FluxNetworks.MODID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(FLUX_CORE.get());
        }
    };

    static void register(RegisterEvent.RegisterHelper<Item> helper) {
        Item.Properties normalProps = new Item.Properties().tab(CREATIVE_MODE_TAB).fireResistant();
        Item.Properties deviceProps = new Item.Properties().tab(CREATIVE_MODE_TAB).fireResistant().stacksTo(1);

        helper.register(RegistryBlocks.FLUX_BLOCK_KEY, new BlockItem(RegistryBlocks.FLUX_BLOCK.get(), normalProps));
        helper.register(RegistryBlocks.FLUX_PLUG_KEY, new FluxDeviceItem(RegistryBlocks.FLUX_PLUG.get(), deviceProps));
        helper.register(RegistryBlocks.FLUX_POINT_KEY, new FluxDeviceItem(RegistryBlocks.FLUX_POINT.get(), deviceProps));
        helper.register(RegistryBlocks.FLUX_CONTROLLER_KEY, new FluxDeviceItem(RegistryBlocks.FLUX_CONTROLLER.get(), deviceProps));
        helper.register(RegistryBlocks.BASIC_FLUX_STORAGE_KEY, new FluxStorageItem(RegistryBlocks.BASIC_FLUX_STORAGE.get(), deviceProps));
        helper.register(RegistryBlocks.HERCULEAN_FLUX_STORAGE_KEY, new FluxStorageItem(RegistryBlocks.HERCULEAN_FLUX_STORAGE.get(), deviceProps));
        helper.register(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_KEY, new FluxStorageItem(RegistryBlocks.GARGANTUAN_FLUX_STORAGE.get(), deviceProps));

        helper.register(FLUX_DUST_KEY, new FluxDustItem(normalProps));
        helper.register(FLUX_CORE_KEY, new Item(normalProps));
        helper.register(FLUX_CONFIGURATOR_KEY, new ItemFluxConfigurator(normalProps));
        helper.register(ADMIN_CONFIGURATOR_KEY, new ItemAdminConfigurator(normalProps));
    }

    private RegistryItems() {}
}
