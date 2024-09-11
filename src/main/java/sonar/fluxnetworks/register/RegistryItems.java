package sonar.fluxnetworks.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.item.*;

import java.util.function.Supplier;

public class RegistryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FluxNetworks.MODID);

    private static final Item.Properties normalProps = new Item.Properties().fireResistant();
    private static final Item.Properties toolProps = new Item.Properties().fireResistant().stacksTo(1);

    private static final ResourceLocation FLUX_DUST_KEY = FluxNetworks.location("flux_dust");
    private static final ResourceLocation FLUX_CORE_KEY = FluxNetworks.location("flux_core");
    private static final ResourceLocation FLUX_CONFIGURATOR_KEY = FluxNetworks.location("flux_configurator");
    private static final ResourceLocation ADMIN_CONFIGURATOR_KEY = FluxNetworks.location("admin_configurator");

    public static final Supplier<BlockItem> FLUX_BLOCK = ITEMS.registerSimpleBlockItem(
            "flux_block", RegistryBlocks.FLUX_BLOCK, normalProps
    );
    public static final DeferredItem<FluxDeviceItem> FLUX_PLUG = ITEMS.register(
            "flux_plug", () -> new FluxDeviceItem(RegistryBlocks.FLUX_PLUG.get(), normalProps)
    );
    public static final DeferredItem<FluxDeviceItem> FLUX_POINT = ITEMS.register(
            "flux_point", () -> new FluxDeviceItem(RegistryBlocks.FLUX_POINT.get(), normalProps)
    );
    public static final DeferredItem<FluxDeviceItem> FLUX_CONTROLLER = ITEMS.register(
            "flux_controller", () -> new FluxDeviceItem(RegistryBlocks.FLUX_CONTROLLER.get(), normalProps)
    );
    public static final DeferredItem<FluxStorageItem> BASIC_FLUX_STORAGE = ITEMS.register(
            "basic_flux_storage", () -> new FluxStorageItem(RegistryBlocks.BASIC_FLUX_STORAGE.get(), normalProps)
    );
    public static final DeferredItem<FluxStorageItem> HERCULEAN_FLUX_STORAGE = ITEMS.register(
            "herculean_flux_storage", () -> new FluxStorageItem(RegistryBlocks.HERCULEAN_FLUX_STORAGE.get(), normalProps)
    );
    public static final DeferredItem<FluxStorageItem> GARGANTUAN_FLUX_STORAGE = ITEMS.register(
            "gargantuan_flux_storage", () -> new FluxStorageItem(RegistryBlocks.GARGANTUAN_FLUX_STORAGE.get(), normalProps)
    );
    public static final DeferredItem<FluxDustItem> FLUX_DUST = ITEMS.register(
            "flux_dust", () -> new FluxDustItem(normalProps)
    );
    public static final DeferredItem<Item> FLUX_CORE = ITEMS.registerSimpleItem(
            "flux_core", normalProps
    );
    public static final DeferredItem<ItemFluxConfigurator> FLUX_CONFIGURATOR = ITEMS.register(
            "flux_configurator", () -> new ItemFluxConfigurator(toolProps)
    );
    public static final DeferredItem<ItemAdminConfigurator> ADMIN_CONFIGURATOR = ITEMS.register(
            "admin_configurator", () -> new ItemAdminConfigurator(toolProps)
    );

//    public static final RegistryObject<BlockItem> FLUX_BLOCK = RegistryObject.create(RegistryBlocks.FLUX_BLOCK_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxDeviceItem> FLUX_PLUG = RegistryObject.create(RegistryBlocks.FLUX_PLUG_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxDeviceItem> FLUX_POINT = RegistryObject.create(RegistryBlocks.FLUX_POINT_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxDeviceItem> FLUX_CONTROLLER = RegistryObject.create(RegistryBlocks.FLUX_CONTROLLER_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxStorageItem> BASIC_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.BASIC_FLUX_STORAGE_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxStorageItem> HERCULEAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.HERCULEAN_FLUX_STORAGE_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxStorageItem> GARGANTUAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<FluxDustItem> FLUX_DUST = RegistryObject.create(FLUX_DUST_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<Item> FLUX_CORE = RegistryObject.create(FLUX_CORE_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<ItemFluxConfigurator> FLUX_CONFIGURATOR = RegistryObject.create(FLUX_CONFIGURATOR_KEY, ForgeRegistries.ITEMS);
//    public static final RegistryObject<ItemAdminConfigurator> ADMIN_CONFIGURATOR = RegistryObject.create(ADMIN_CONFIGURATOR_KEY, ForgeRegistries.ITEMS);

    private RegistryItems() {}
}
