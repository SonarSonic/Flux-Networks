package sonar.fluxnetworks.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.item.FluxDustItem;
import sonar.fluxnetworks.common.item.FluxStorageItem;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;

@ObjectHolder(FluxNetworks.MODID)
public class RegistryItems {

    @ObjectHolder("flux_dust")
    public static FluxDustItem FLUX_DUST;

    @ObjectHolder("flux_core")
    public static Item FLUX_CORE;

    @ObjectHolder("flux_configurator")
    public static ItemFluxConfigurator FLUX_CONFIGURATOR;

    @ObjectHolder("admin_configurator")
    public static ItemAdminConfigurator ADMIN_CONFIGURATOR;

    //@ObjectHolder("fireitem")
    //public static EntityType<FireItemEntity> FIRE_ITEM_ENTITY;

    @ObjectHolder("basic_flux_storage")
    public static FluxStorageItem BASIC_FLUX_STORAGE;

    @ObjectHolder("herculean_flux_storage")
    public static FluxStorageItem HERCULEAN_FLUX_STORAGE;

    @ObjectHolder("gargantuan_flux_storage")
    public static FluxStorageItem GARGANTUAN_FLUX_STORAGE;
}
