package sonar.fluxnetworks.common.loot;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.block.FluxDeviceBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.device.FluxDeviceEntity;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nonnull;
import java.util.Set;

public class FluxBlockLoot extends BlockLoot {

    private final Set<Block> knownBlocks = new ObjectArraySet<>();

    public FluxBlockLoot() {
    }

    @Nonnull
    @Override
    public final Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

    @Override
    protected final void add(@Nonnull Block blockIn, @Nonnull LootTable.Builder table) {
        super.add(blockIn, table);
        knownBlocks.add(blockIn);
    }

    @Override
    protected void addTables() {
        add(RegistryBlocks.FLUX_PLUG, FluxBlockLoot::fluxDropping);
        add(RegistryBlocks.FLUX_POINT, FluxBlockLoot::fluxDropping);
        add(RegistryBlocks.FLUX_CONTROLLER, FluxBlockLoot::fluxDropping);
        add(RegistryBlocks.BASIC_FLUX_STORAGE, FluxBlockLoot::fluxDropping);
        add(RegistryBlocks.HERCULEAN_FLUX_STORAGE, FluxBlockLoot::fluxDropping);
        add(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, FluxBlockLoot::fluxDropping);
    }

    /**
     * Pick out needed NBT from {@link FluxDeviceEntity#save(CompoundTag)}
     * Convert them to be readable by {@link FluxDeviceBlock#setPlacedBy(Level, BlockPos, BlockState, LivingEntity,
     * ItemStack)}
     *
     * @param block flux device block
     * @return loot table builder
     */
    @Nonnull
    protected static LootTable.Builder fluxDropping(Block block) {
        if (!(block instanceof FluxDeviceBlock)) {
            throw new IllegalArgumentException();
        }
        CopyNbtFunction.Builder copyNbt = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY);
        // replace to a sub NBT compound tag to avoid conflicts with vanilla or other mods
        copyNbt.copy(FluxConstants.NETWORK_ID, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.NETWORK_ID);
        copyNbt.copy(FluxConstants.CUSTOM_NAME, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.CUSTOM_NAME);
        copyNbt.copy(FluxConstants.PRIORITY, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.PRIORITY);
        copyNbt.copy(FluxConstants.LIMIT, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.LIMIT);
        copyNbt.copy(FluxConstants.SURGE_MODE, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.SURGE_MODE);
        copyNbt.copy(FluxConstants.DISABLE_LIMIT, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.DISABLE_LIMIT);
        if (block instanceof FluxStorageBlock)
            copyNbt.copy(FluxConstants.ENERGY, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.ENERGY);
        else
            copyNbt.copy(FluxConstants.BUFFER, FluxConstants.TAG_FLUX_DATA + "." + FluxConstants.BUFFER);
        return LootTable.lootTable().withPool(applyExplosionCondition(block,
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(block)
                                .apply(copyNbt)
                        )
        ));
    }
}
