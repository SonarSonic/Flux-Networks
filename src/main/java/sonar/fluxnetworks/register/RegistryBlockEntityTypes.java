package sonar.fluxnetworks.register;

import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.device.TileFluxController;
import sonar.fluxnetworks.common.device.TileFluxPlug;
import sonar.fluxnetworks.common.device.TileFluxPoint;
import sonar.fluxnetworks.common.device.TileFluxStorage;

import java.util.Set;

public class RegistryBlockEntityTypes {
    private static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FluxNetworks.MODID);

    public static final RegistryObject<BlockEntityType<TileFluxPlug>> FLUX_PLUG = registerBlockEntity("flux_block", TileFluxPlug::new, RegistryBlocks.FLUX_PLUG);
    public static final RegistryObject<BlockEntityType<TileFluxPoint>> FLUX_POINT = registerBlockEntity("flux_point", TileFluxPoint::new, RegistryBlocks.FLUX_POINT);
    public static final RegistryObject<BlockEntityType<TileFluxController>> FLUX_CONTROLLER = registerBlockEntity("flux_controller", TileFluxController::new, RegistryBlocks.FLUX_CONTROLLER);
    public static final RegistryObject<BlockEntityType<TileFluxStorage.Basic>> BASIC_FLUX_STORAGE = registerBlockEntity("basic_flux_storage", TileFluxStorage.Basic::new, RegistryBlocks.BASIC_FLUX_STORAGE);
    public static final RegistryObject<BlockEntityType<TileFluxStorage.Herculean>> HERCULEAN_FLUX_STORAGE = registerBlockEntity("herculean_flux_storage", TileFluxStorage.Herculean::new, RegistryBlocks.HERCULEAN_FLUX_STORAGE);
    public static final RegistryObject<BlockEntityType<TileFluxStorage.Gargantuan>> GARGANTUAN_FLUX_STORAGE = registerBlockEntity("gargantuan_flux_storage", TileFluxStorage.Gargantuan::new, RegistryBlocks.GARGANTUAN_FLUX_STORAGE);

    private static <BE extends BlockEntity> RegistryObject<BlockEntityType<BE>> registerBlockEntity(String name, BlockEntitySupplier<? extends BE> factory, RegistryObject<? extends Block> blockRegistryObject) {
        return REGISTRY.register(name, () -> new BlockEntityType<>(factory, Set.of(blockRegistryObject.get()), DSL.remainderType()));
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    private RegistryBlockEntityTypes() {}
}
