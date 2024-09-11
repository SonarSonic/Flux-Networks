package sonar.fluxnetworks.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import java.util.function.Supplier;

public class RegistryBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FluxNetworks.MODID);

    private static final BlockBehaviour.Properties normalProps = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
            .strength(1.0F, 1000F);
    private static final BlockBehaviour.Properties deviceProps = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
            .strength(1.0F, 1000F).noOcclusion();

    public static final ResourceLocation FLUX_BLOCK_KEY = FluxNetworks.location("flux_block");
    public static final ResourceLocation FLUX_PLUG_KEY = FluxNetworks.location("flux_plug");
    public static final ResourceLocation FLUX_POINT_KEY = FluxNetworks.location("flux_point");
    public static final ResourceLocation FLUX_CONTROLLER_KEY = FluxNetworks.location("flux_controller");
    public static final ResourceLocation BASIC_FLUX_STORAGE_KEY = FluxNetworks.location("basic_flux_storage");
    public static final ResourceLocation HERCULEAN_FLUX_STORAGE_KEY = FluxNetworks.location("herculean_flux_storage");
    public static final ResourceLocation GARGANTUAN_FLUX_STORAGE_KEY = FluxNetworks.location("gargantuan_flux_storage");

    public static final Supplier<Block> FLUX_BLOCK = BLOCKS.register("flux_block", () -> new Block(normalProps));
    public static final Supplier<FluxPlugBlock> FLUX_PLUG = BLOCKS.register("flux_plug", () -> new FluxPlugBlock(deviceProps));
    public static final Supplier<FluxPointBlock> FLUX_POINT = BLOCKS.register("flux_point", () -> new FluxPointBlock(deviceProps));
    public static final Supplier<FluxControllerBlock> FLUX_CONTROLLER = BLOCKS.register("flux_controller", () -> new FluxControllerBlock(deviceProps));
    public static final Supplier<FluxStorageBlock.Basic> BASIC_FLUX_STORAGE = BLOCKS.register("basic_flux_storage", () -> new FluxStorageBlock.Basic(deviceProps));
    public static final Supplier<FluxStorageBlock.Herculean> HERCULEAN_FLUX_STORAGE = BLOCKS.register("herculean_flux_storage", () -> new FluxStorageBlock.Herculean(deviceProps));
    public static final Supplier<FluxStorageBlock.Gargantuan> GARGANTUAN_FLUX_STORAGE = BLOCKS.register("gargantuan_flux_storage", () -> new FluxStorageBlock.Gargantuan(deviceProps));

//    public static final RegistryObject<Block> FLUX_BLOCK = RegistryObject.create(FLUX_BLOCK_KEY, ForgeRegistries.BLOCKS);
//    public static final RegistryObject<FluxPlugBlock> FLUX_PLUG = RegistryObject.create(FLUX_PLUG_KEY, ForgeRegistries.BLOCKS);
//    public static final RegistryObject<FluxPointBlock> FLUX_POINT = RegistryObject.create(FLUX_POINT_KEY, ForgeRegistries.BLOCKS);
//    public static final RegistryObject<FluxControllerBlock> FLUX_CONTROLLER = RegistryObject.create(FLUX_CONTROLLER_KEY, ForgeRegistries.BLOCKS);
//    public static final RegistryObject<FluxStorageBlock.Basic> BASIC_FLUX_STORAGE = RegistryObject.create(BASIC_FLUX_STORAGE_KEY, ForgeRegistries.BLOCKS);
//    public static final RegistryObject<FluxStorageBlock.Herculean> HERCULEAN_FLUX_STORAGE = RegistryObject.create(HERCULEAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCKS);
//    public static final RegistryObject<FluxStorageBlock.Gargantuan> GARGANTUAN_FLUX_STORAGE = RegistryObject.create(GARGANTUAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCKS);

    private RegistryBlocks() {}
}
