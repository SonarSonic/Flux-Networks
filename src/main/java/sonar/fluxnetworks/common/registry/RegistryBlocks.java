package sonar.fluxnetworks.common.registry;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.misc.ContainerConnector;
import sonar.fluxnetworks.common.tileentity.TileFluxController;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

@ObjectHolder("fluxnetworks")
public class RegistryBlocks {

    @ObjectHolder("fluxblock")
    public static Block FLUX_BLOCK;


    @ObjectHolder("connector")
    public static ContainerType<ContainerConnector<?>> CONTAINER_CONNECTOR;

    @ObjectHolder("fluxplug")
    public static FluxPlugBlock FLUX_PLUG;

    @ObjectHolder("fluxplug")
    public static TileEntityType<TileFluxPlug> FLUX_PLUG_TILE;


    @ObjectHolder("fluxpoint")
    public static FluxPointBlock FLUX_POINT;

    @ObjectHolder("fluxpoint")
    public static TileEntityType<TileFluxPoint> FLUX_POINT_TILE;


    @ObjectHolder("fluxcontroller")
    public static FluxControllerBlock FLUX_CONTROLLER;

    @ObjectHolder("fluxcontroller")
    public static TileEntityType<TileFluxController> FLUX_CONTROLLER_TILE;


    @ObjectHolder("basicfluxstorage")
    public static FluxStorageBlock BASIC_FLUX_STORAGE;

    @ObjectHolder("basicfluxstorage")
    public static TileEntityType<TileFluxStorage.Basic> BASIC_FLUX_STORAGE_TILE;


    @ObjectHolder("herculeanfluxstorage")
    public static FluxStorageBlock.Herculean HERCULEAN_FLUX_STORAGE;

    @ObjectHolder("herculeanfluxstorage")
    public static TileEntityType<TileFluxStorage.Herculean> HERCULEAN_FLUX_STORAGE_TILE;


    @ObjectHolder("gargantuanfluxstorage")
    public static FluxStorageBlock.Gargantuan GARGANTUAN_FLUX_STORAGE;

    @ObjectHolder("gargantuanfluxstorage")
    public static TileEntityType<TileFluxStorage.Gargantuan> GARGANTUAN_FLUX_STORAGE_TILE;
}
