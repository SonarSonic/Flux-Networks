package sonar.fluxnetworks.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;

public class RegistryCreativeModeTabs {
    public static final ResourceLocation CREATIVE_MODE_TAB_KEY = FluxNetworks.location("tab");

    public static final RegistryObject<CreativeModeTab> CREATIVE_MODE_TAB = RegistryObject.create(
            CREATIVE_MODE_TAB_KEY, Registries.CREATIVE_MODE_TAB, FluxNetworks.MODID
    );

    static void register(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        helper.register(CREATIVE_MODE_TAB_KEY, CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + FluxNetworks.MODID))
                .icon(() -> new ItemStack(RegistryItems.FLUX_CORE.get()))
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
                .build());
    }

    private RegistryCreativeModeTabs() {}
}
