package sonar.fluxnetworks.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import sonar.fluxnetworks.FluxNetworks;

import java.util.function.Supplier;

public class RegistryCreativeModeTabs {
    public static final ResourceLocation CREATIVE_MODE_TAB_KEY = FluxNetworks.location("tab");

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, FluxNetworks.MODID);

    public static final Supplier<CreativeModeTab> CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.register(
            "tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + FluxNetworks.MODID))
                    .icon(RegistryItems.FLUX_CORE::toStack)
                    .displayItems((parameters, output) -> {
                        output.accept(RegistryItems.FLUX_BLOCK.get());
                        output.accept(RegistryItems.FLUX_PLUG.get());
                        output.accept(RegistryItems.FLUX_POINT.get());
                        output.accept(RegistryItems.FLUX_CONTROLLER.get());
                        output.accept(RegistryItems.BASIC_FLUX_STORAGE.get());
                        output.accept(RegistryItems.HERCULEAN_FLUX_STORAGE.get());
                        output.accept(RegistryItems.GARGANTUAN_FLUX_STORAGE.get());
                        output.accept(RegistryItems.FLUX_DUST.get());
                        output.accept(RegistryItems.FLUX_CORE.get());
                        output.accept(RegistryItems.FLUX_CONFIGURATOR.get());
                        output.accept(RegistryItems.ADMIN_CONFIGURATOR.get());
                    })
                    .build()
            );

    private RegistryCreativeModeTabs() {}
}
