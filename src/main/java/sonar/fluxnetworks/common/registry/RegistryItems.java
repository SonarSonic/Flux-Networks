package sonar.fluxnetworks.common.registry;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.item.ItemFlux;

@ObjectHolder(FluxNetworks.MODID)
public class RegistryItems {

    @ObjectHolder("flux")
    public static ItemFlux FLUX;

    @ObjectHolder("fluxcore")
    public static Item FLUX_CORE;

    @ObjectHolder("fluxconfigurator")
    public static ItemFluxConfigurator FLUX_CONFIGURATOR;

    @ObjectHolder("adminconfigurator")
    public static ItemAdminConfigurator ADMIN_CONFIGURATOR;

    //@ObjectHolder("fireitem")
    //public static EntityType<FireItemEntity> FIRE_ITEM_ENTITY;
}
