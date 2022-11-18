package sonar.fluxnetworks.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

public class RegistryBlocks {
    public static final ResourceLocation FLUX_BLOCK_KEY = FluxNetworks.location("flux_block");
    public static final ResourceLocation FLUX_PLUG_KEY = FluxNetworks.location("flux_plug");
    public static final ResourceLocation FLUX_POINT_KEY = FluxNetworks.location("flux_point");
    public static final ResourceLocation FLUX_CONTROLLER_KEY = FluxNetworks.location("flux_controller");
    public static final ResourceLocation BASIC_FLUX_STORAGE_KEY = FluxNetworks.location("basic_flux_storage");
    public static final ResourceLocation HERCULEAN_FLUX_STORAGE_KEY = FluxNetworks.location("herculean_flux_storage");
    public static final ResourceLocation GARGANTUAN_FLUX_STORAGE_KEY = FluxNetworks.location("gargantuan_flux_storage");

    public static final RegistryObject<Block> FLUX_BLOCK = RegistryObject.create(FLUX_BLOCK_KEY, ForgeRegistries.BLOCKS);
    public static final RegistryObject<FluxPlugBlock> FLUX_PLUG = RegistryObject.create(FLUX_PLUG_KEY, ForgeRegistries.BLOCKS);
    public static final RegistryObject<FluxPointBlock> FLUX_POINT = RegistryObject.create(FLUX_POINT_KEY, ForgeRegistries.BLOCKS);
    public static final RegistryObject<FluxControllerBlock> FLUX_CONTROLLER = RegistryObject.create(FLUX_CONTROLLER_KEY, ForgeRegistries.BLOCKS);
    public static final RegistryObject<FluxStorageBlock.Basic> BASIC_FLUX_STORAGE = RegistryObject.create(BASIC_FLUX_STORAGE_KEY, ForgeRegistries.BLOCKS);
    public static final RegistryObject<FluxStorageBlock.Herculean> HERCULEAN_FLUX_STORAGE = RegistryObject.create(HERCULEAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCKS);
    public static final RegistryObject<FluxStorageBlock.Gargantuan> GARGANTUAN_FLUX_STORAGE = RegistryObject.create(GARGANTUAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCKS);

    static void register(RegisterEvent.RegisterHelper<Block> helper) {
        BlockBehaviour.Properties normalProps = BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL)
                .strength(1.0F, 1000F);
        BlockBehaviour.Properties deviceProps = BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL)
                .strength(1.0F, 1000F).noOcclusion();

        helper.register(FLUX_BLOCK_KEY, new Block(normalProps));
        helper.register(FLUX_PLUG_KEY, new FluxPlugBlock(deviceProps));
        helper.register(FLUX_POINT_KEY, new FluxPointBlock(deviceProps));
        helper.register(FLUX_CONTROLLER_KEY, new FluxControllerBlock(deviceProps));
        helper.register(BASIC_FLUX_STORAGE_KEY, new FluxStorageBlock.Basic(deviceProps));
        helper.register(HERCULEAN_FLUX_STORAGE_KEY, new FluxStorageBlock.Herculean(deviceProps));
        helper.register(GARGANTUAN_FLUX_STORAGE_KEY, new FluxStorageBlock.Gargantuan(deviceProps));
    }

    private RegistryBlocks() {}
}
