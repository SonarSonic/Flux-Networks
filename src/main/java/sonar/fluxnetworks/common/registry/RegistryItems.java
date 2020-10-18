package sonar.fluxnetworks.common.registry;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxDust;

@SuppressWarnings("unused")
@ObjectHolder(FluxNetworks.MODID)
public class RegistryItems {

    @ObjectHolder("flux_dust")
    public static ItemFluxDust FLUX_DUST;

    @ObjectHolder("flux_core")
    public static Item FLUX_CORE;

    @ObjectHolder("flux_configurator")
    public static ItemFluxConfigurator FLUX_CONFIGURATOR;

    @ObjectHolder("admin_configurator")
    public static ItemAdminConfigurator ADMIN_CONFIGURATOR;

    //@ObjectHolder("fireitem")
    //public static EntityType<FireItemEntity> FIRE_ITEM_ENTITY;
}
