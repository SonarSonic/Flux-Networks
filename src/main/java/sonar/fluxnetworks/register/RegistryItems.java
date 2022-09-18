package sonar.fluxnetworks.register;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.item.*;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RegistryItems {
    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FluxNetworks.MODID);

    private static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab(FluxNetworks.MODID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(FLUX_CORE.get());
        }
    };

    private static final Properties NORMAL_PROPS = new Properties().tab(CREATIVE_MODE_TAB).fireResistant();
    private static final Properties DEVICE_PROPS = new Properties().tab(CREATIVE_MODE_TAB).fireResistant().stacksTo(1);


    public static final RegistryObject<BlockItem> FLUX_BLOCK = registerBlockItem("flux_block", RegistryBlocks.FLUX_BLOCK, BlockItem::new);
    public static final RegistryObject<FluxDeviceItem> FLUX_PLUG = registerBlockItem("flux_plug", RegistryBlocks.FLUX_PLUG, FluxDeviceItem::new);
    public static final RegistryObject<FluxDeviceItem> FLUX_POINT = registerBlockItem("flux_point", RegistryBlocks.FLUX_POINT, FluxDeviceItem::new);
    public static final RegistryObject<FluxDeviceItem> FLUX_CONTROLLER = registerBlockItem("flux_controller", RegistryBlocks.FLUX_CONTROLLER, FluxDeviceItem::new);
    public static final RegistryObject<FluxStorageItem> BASIC_FLUX_STORAGE = registerBlockItem("basic_flux_storage", RegistryBlocks.BASIC_FLUX_STORAGE, FluxStorageItem::new);
    public static final RegistryObject<FluxStorageItem> HERCULEAN_FLUX_STORAGE = registerBlockItem("herculean_flux_storage", RegistryBlocks.HERCULEAN_FLUX_STORAGE, FluxStorageItem::new);
    public static final RegistryObject<FluxStorageItem> GARGANTUAN_FLUX_STORAGE = registerBlockItem("gargantuan_flux_storage", RegistryBlocks.GARGANTUAN_FLUX_STORAGE, FluxStorageItem::new);

    public static final RegistryObject<FluxDustItem> FLUX_DUST = registerItem("flux_dust", NORMAL_PROPS, FluxDustItem::new);
    public static final RegistryObject<Item> FLUX_CORE = registerItem("flux_core", NORMAL_PROPS, Item::new);

    public static final RegistryObject<ItemFluxConfigurator> FLUX_CONFIGURATOR = registerItem("flux_configurator", DEVICE_PROPS, ItemFluxConfigurator::new);
    public static final RegistryObject<ItemAdminConfigurator> ADMIN_CONFIGURATOR = registerItem("admin_configurator", DEVICE_PROPS, ItemAdminConfigurator::new);

    private static <I extends Item, B extends Block> RegistryObject<I> registerBlockItem(String name, RegistryObject<B> blockRegistryObject, BiFunction<? super B, ? super Properties, I> factory) {
        return registerItem(name, NORMAL_PROPS, (props) -> factory.apply(blockRegistryObject.get(), props));
    }

    private static <I extends Item> RegistryObject<I> registerItem(String name, Properties itemProperties, Function<? super Properties, I> factory) {
        return REGISTRY.register(name, () -> factory.apply(itemProperties));
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    private RegistryItems() {}
}
