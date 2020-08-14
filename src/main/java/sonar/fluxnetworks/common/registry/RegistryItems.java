package sonar.fluxnetworks.common.registry;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.item.AdminConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxItem;

@ObjectHolder(FluxNetworks.MODID)
public class RegistryItems {

    @ObjectHolder("flux")
    public static FluxItem FLUX;

    @ObjectHolder("fluxcore")
    public static Item FLUX_CORE;

    @ObjectHolder("fluxconfigurator")
    public static FluxConfiguratorItem FLUX_CONFIGURATOR;

    @ObjectHolder("adminconfigurator")
    public static AdminConfiguratorItem ADMIN_CONFIGURATOR;

    //@ObjectHolder("fireitem")
    //public static EntityType<FireItemEntity> FIRE_ITEM_ENTITY;
}
