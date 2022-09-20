package sonar.fluxnetworks.register;

import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.common.device.TileFluxController;
import sonar.fluxnetworks.common.device.TileFluxPlug;
import sonar.fluxnetworks.common.device.TileFluxPoint;
import sonar.fluxnetworks.common.device.TileFluxStorage;

import java.util.Set;

public class RegistryBlockEntityTypes {
    public static final RegistryObject<BlockEntityType<TileFluxPlug>> FLUX_PLUG = RegistryObject.create(RegistryBlocks.FLUX_PLUG_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final RegistryObject<BlockEntityType<TileFluxPoint>> FLUX_POINT = RegistryObject.create(RegistryBlocks.FLUX_POINT_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final RegistryObject<BlockEntityType<TileFluxController>> FLUX_CONTROLLER = RegistryObject.create(RegistryBlocks.FLUX_CONTROLLER_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final RegistryObject<BlockEntityType<TileFluxStorage.Basic>> BASIC_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.BASIC_FLUX_STORAGE_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final RegistryObject<BlockEntityType<TileFluxStorage.Herculean>> HERCULEAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.HERCULEAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final RegistryObject<BlockEntityType<TileFluxStorage.Gargantuan>> GARGANTUAN_FLUX_STORAGE = RegistryObject.create(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_KEY, ForgeRegistries.BLOCK_ENTITY_TYPES);

    static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register(RegistryBlocks.FLUX_PLUG_KEY, new BlockEntityType<>(TileFluxPlug::new, Set.of(RegistryBlocks.FLUX_PLUG.get()), DSL.remainderType()));
        helper.register(RegistryBlocks.FLUX_POINT_KEY, new BlockEntityType<>(TileFluxPoint::new, Set.of(RegistryBlocks.FLUX_POINT.get()), DSL.remainderType()));
        helper.register(RegistryBlocks.FLUX_CONTROLLER_KEY, new BlockEntityType<>(TileFluxController::new, Set.of(RegistryBlocks.FLUX_CONTROLLER.get()), DSL.remainderType()));
        helper.register(RegistryBlocks.BASIC_FLUX_STORAGE_KEY, new BlockEntityType<>(TileFluxStorage.Basic::new, Set.of(RegistryBlocks.BASIC_FLUX_STORAGE.get()), DSL.remainderType()));
        helper.register(RegistryBlocks.HERCULEAN_FLUX_STORAGE_KEY, new BlockEntityType<>(TileFluxStorage.Herculean::new, Set.of(RegistryBlocks.HERCULEAN_FLUX_STORAGE.get()), DSL.remainderType()));
        helper.register(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_KEY, new BlockEntityType<>(TileFluxStorage.Gargantuan::new, Set.of(RegistryBlocks.GARGANTUAN_FLUX_STORAGE.get()), DSL.remainderType()));
    }

    private RegistryBlockEntityTypes() {}
}
