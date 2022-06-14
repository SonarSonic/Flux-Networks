package sonar.fluxnetworks.register;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.*;

@ObjectHolder(FluxNetworks.MODID)
public class RegistryBlocks {

    @ObjectHolder("flux_block")
    public static Block FLUX_BLOCK;


    @ObjectHolder("flux_menu")
    public static MenuType<FluxMenu> FLUX_MENU;


    @ObjectHolder("flux_plug")
    public static FluxPlugBlock FLUX_PLUG;

    @ObjectHolder("flux_plug")
    public static BlockEntityType<TileFluxPlug> FLUX_PLUG_ENTITY;


    @ObjectHolder("flux_point")
    public static FluxPointBlock FLUX_POINT;

    @ObjectHolder("flux_point")
    public static BlockEntityType<TileFluxPoint> FLUX_POINT_ENTITY;


    @ObjectHolder("flux_controller")
    public static FluxControllerBlock FLUX_CONTROLLER;

    @ObjectHolder("flux_controller")
    public static BlockEntityType<TileFluxController> FLUX_CONTROLLER_ENTITY;


    @ObjectHolder("basic_flux_storage")
    public static FluxStorageBlock BASIC_FLUX_STORAGE;

    @ObjectHolder("basic_flux_storage")
    public static BlockEntityType<TileFluxStorage.Basic> BASIC_FLUX_STORAGE_ENTITY;


    @ObjectHolder("herculean_flux_storage")
    public static FluxStorageBlock.Herculean HERCULEAN_FLUX_STORAGE;

    @ObjectHolder("herculean_flux_storage")
    public static BlockEntityType<TileFluxStorage.Herculean> HERCULEAN_FLUX_STORAGE_ENTITY;


    @ObjectHolder("gargantuan_flux_storage")
    public static FluxStorageBlock.Gargantuan GARGANTUAN_FLUX_STORAGE;

    @ObjectHolder("gargantuan_flux_storage")
    public static BlockEntityType<TileFluxStorage.Gargantuan> GARGANTUAN_FLUX_STORAGE_ENTITY;
}
