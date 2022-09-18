package sonar.fluxnetworks.register;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;

/**
 * ContainerType has the function to create container on client side<br>
 * Register the create container function that will be opened on client side from the packet that from the server
 */
public class RegistryMenuTypes {
    private static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FluxNetworks.MODID);

    public static final RegistryObject<MenuType<FluxMenu>> FLUX_MENU = REGISTRY.register("flux_menu", () ->
            IForgeMenuType.create((containerId, inventory, buffer) -> {
                // check if it's tile entity
                if (buffer.readBoolean()) {
                    BlockPos pos = buffer.readBlockPos();
                    if (inventory.player.getLevel().getBlockEntity(pos) instanceof TileFluxDevice device) {
                        CompoundTag tag = buffer.readNbt();
                        if (tag != null) {
                            device.readCustomTag(tag, FluxConstants.NBT_TILE_UPDATE);
                        }
                        return new FluxMenu(containerId, inventory, device);
                    }
                } else {
                    ItemStack stack = inventory.player.getMainHandItem();
                    if (stack.getItem() == RegistryItems.FLUX_CONFIGURATOR.get()) {
                        return new FluxMenu(containerId, inventory, new ItemFluxConfigurator.Provider(stack));
                    }
                }
                return new FluxMenu(containerId, inventory, new ItemAdminConfigurator.Provider());
            }));

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    private RegistryMenuTypes() {}
}
