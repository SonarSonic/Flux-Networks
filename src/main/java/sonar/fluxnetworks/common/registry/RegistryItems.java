package sonar.fluxnetworks.common.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.common.core.FireItemEntity;
import sonar.fluxnetworks.common.item.AdminConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import net.minecraft.item.Item;

@ObjectHolder("fluxnetworks")
public class RegistryItems {

    @ObjectHolder("flux")
    public static Item FLUX;

    @ObjectHolder("fluxcore")
    public static Item FLUX_CORE;

    @ObjectHolder("fluxconfigurator")
    public static FluxConfiguratorItem FLUX_CONFIGURATOR;

    @ObjectHolder("adminconfigurator")
    public static AdminConfiguratorItem ADMIN_CONFIGURATOR;

    @ObjectHolder("fireitementity")
    public static EntityType<FireItemEntity> FIRE_ITEM_ENTITY;

}
