package sonar.fluxnetworks.register;

import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.device.TileFluxController;
import sonar.fluxnetworks.common.device.TileFluxPlug;
import sonar.fluxnetworks.common.device.TileFluxPoint;
import sonar.fluxnetworks.common.device.TileFluxStorage;

import java.util.Set;
import java.util.function.Supplier;

public class RegistryBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FluxNetworks.MODID);

    public static final Supplier<BlockEntityType<TileFluxPlug>> FLUX_PLUG = BLOCK_ENTITY_TYPES.register(
            "flux_plug",
            () -> new BlockEntityType<>(TileFluxPlug::new, Set.of(RegistryBlocks.FLUX_PLUG.get()), DSL.remainderType())
    );
    public static final Supplier<BlockEntityType<TileFluxPoint>> FLUX_POINT = BLOCK_ENTITY_TYPES.register(
            "flux_point",
            () -> new BlockEntityType<>(TileFluxPoint::new, Set.of(RegistryBlocks.FLUX_POINT.get()), DSL.remainderType())
    );
    public static final Supplier<BlockEntityType<TileFluxController>> FLUX_CONTROLLER = BLOCK_ENTITY_TYPES.register(
            "flux_controller",
            () -> new BlockEntityType<>(TileFluxController::new, Set.of(RegistryBlocks.FLUX_CONTROLLER.get()), DSL.remainderType())
    );
    public static final Supplier<BlockEntityType<TileFluxStorage.Basic>> BASIC_FLUX_STORAGE = BLOCK_ENTITY_TYPES.register(
            "basic_flux_storage",
            () -> new BlockEntityType<>(TileFluxStorage.Basic::new, Set.of(RegistryBlocks.BASIC_FLUX_STORAGE.get()), DSL.remainderType())
    );
    public static final Supplier<BlockEntityType<TileFluxStorage.Herculean>> HERCULEAN_FLUX_STORAGE = BLOCK_ENTITY_TYPES.register(
            "herculean_flux_storage",
            () -> new BlockEntityType<>(TileFluxStorage.Herculean::new, Set.of(RegistryBlocks.HERCULEAN_FLUX_STORAGE.get()), DSL.remainderType())
    );
    public static final Supplier<BlockEntityType<TileFluxStorage.Gargantuan>> GARGANTUAN_FLUX_STORAGE = BLOCK_ENTITY_TYPES.register(
            "gargantuan_flux_storage",
            () -> new BlockEntityType<>(TileFluxStorage.Gargantuan::new, Set.of(RegistryBlocks.FLUX_PLUG.get()), DSL.remainderType())
    );

//    public static final RegistryObject<BlockEntityType<TileFluxPlug>> FLUX_PLUG = RegistryObject.create(RegistryBlocks.FLUX_PLUG_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
//    public static final RegistryObject<BlockEntityType<TileFluxPoint>> FLUX_POINT = RegistryObject.create(RegistryBlocks.FLUX_POINT_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
//    public static final RegistryObject<BlockEntityType<TileFluxController>> FLUX_CONTROLLER = RegistryObject.create(RegistryBlocks.FLUX_CONTROLLER_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
//    public static final RegistryObject<BlockEntityType<TileFluxStorage.Basic>> BASIC_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.BASIC_FLUX_STORAGE_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
//    public static final RegistryObject<BlockEntityType<TileFluxStorage.Herculean>> HERCULEAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.HERCULEAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
//    public static final RegistryObject<BlockEntityType<TileFluxStorage.Gargantuan>> GARGANTUAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES)

    private RegistryBlockEntityTypes() {}
}
