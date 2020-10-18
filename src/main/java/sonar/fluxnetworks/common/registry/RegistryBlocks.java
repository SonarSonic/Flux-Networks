package sonar.fluxnetworks.common.registry;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.misc.ContainerConnector;
import sonar.fluxnetworks.common.tileentity.TileFluxController;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

@ObjectHolder(FluxNetworks.MODID)
public class RegistryBlocks {

    @ObjectHolder("flux_block")
    public static Block FLUX_BLOCK;


    @ObjectHolder("connector")
    public static ContainerType<ContainerConnector<?>> CONTAINER_CONNECTOR;

    @ObjectHolder("flux_plug")
    public static FluxPlugBlock FLUX_PLUG;

    @ObjectHolder("flux_plug")
    public static TileEntityType<TileFluxPlug> FLUX_PLUG_TILE;


    @ObjectHolder("flux_point")
    public static FluxPointBlock FLUX_POINT;

    @ObjectHolder("flux_point")
    public static TileEntityType<TileFluxPoint> FLUX_POINT_TILE;


    @ObjectHolder("flux_controller")
    public static FluxControllerBlock FLUX_CONTROLLER;

    @ObjectHolder("flux_controller")
    public static TileEntityType<TileFluxController> FLUX_CONTROLLER_TILE;


    @ObjectHolder("basic_flux_storage")
    public static FluxStorageBlock BASIC_FLUX_STORAGE;

    @ObjectHolder("basic_flux_storage")
    public static TileEntityType<TileFluxStorage.Basic> BASIC_FLUX_STORAGE_TILE;


    @ObjectHolder("herculean_flux_storage")
    public static FluxStorageBlock.Herculean HERCULEAN_FLUX_STORAGE;

    @ObjectHolder("herculean_flux_storage")
    public static TileEntityType<TileFluxStorage.Herculean> HERCULEAN_FLUX_STORAGE_TILE;


    @ObjectHolder("gargantuan_flux_storage")
    public static FluxStorageBlock.Gargantuan GARGANTUAN_FLUX_STORAGE;

    @ObjectHolder("gargantuan_flux_storage")
    public static TileEntityType<TileFluxStorage.Gargantuan> GARGANTUAN_FLUX_STORAGE_TILE;
}
