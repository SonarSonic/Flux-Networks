package sonar.fluxnetworks.register;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import java.util.function.Function;

public class RegistryBlocks {
    private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FluxNetworks.MODID);

    private static final Properties NORMAL_PROPS = Properties.of(Material.METAL).sound(SoundType.METAL)
            .strength(1.0F, 1000F);
    private static final Properties DEVICE_PROPS = Properties.of(Material.METAL).sound(SoundType.METAL)
            .strength(1.0F, 1000F).noOcclusion();


    public static final RegistryObject<Block> FLUX_BLOCK = registerBlock("flux_block", NORMAL_PROPS, Block::new);
    public static final RegistryObject<FluxPlugBlock> FLUX_PLUG = registerBlock("flux_plug", DEVICE_PROPS, FluxPlugBlock::new);
    public static final RegistryObject<FluxPointBlock> FLUX_POINT = registerBlock("flux_point", DEVICE_PROPS, FluxPointBlock::new);
    public static final RegistryObject<FluxControllerBlock> FLUX_CONTROLLER = registerBlock("flux_controller", DEVICE_PROPS, FluxControllerBlock::new);
    public static final RegistryObject<FluxStorageBlock.Basic> BASIC_FLUX_STORAGE = registerBlock("basic_flux_storage", DEVICE_PROPS, FluxStorageBlock.Basic::new);
    public static final RegistryObject<FluxStorageBlock.Herculean> HERCULEAN_FLUX_STORAGE = registerBlock("herculean_flux_storage", DEVICE_PROPS, FluxStorageBlock.Herculean::new);
    public static final RegistryObject<FluxStorageBlock.Gargantuan> GARGANTUAN_FLUX_STORAGE = registerBlock("gargantuan_flux_storage", DEVICE_PROPS, FluxStorageBlock.Gargantuan::new);

    private static <B extends Block> RegistryObject<B> registerBlock(String name, Properties blockProperties, Function<? super Properties, B> factory) {
        return REGISTRY.register(name, () -> factory.apply(blockProperties));
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    private RegistryBlocks() {}
}
