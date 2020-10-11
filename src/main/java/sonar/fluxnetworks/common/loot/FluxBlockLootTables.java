package sonar.fluxnetworks.common.loot;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.common.block.FluxDeviceBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.item.ItemFluxDevice;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.Set;

public class FluxBlockLootTables extends BlockLootTables {

    private final Set<Block> knownBlocks = new ObjectArraySet<>();

    @Nonnull
    @Override
    public final Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

    @Override
    protected final void registerLootTable(@Nonnull Block blockIn, @Nonnull LootTable.Builder table) {
        super.registerLootTable(blockIn, table);
        knownBlocks.add(blockIn);
    }

    @Override
    protected void addTables() {
        registerLootTable(RegistryBlocks.FLUX_PLUG, FluxBlockLootTables::fluxDropping);
        registerLootTable(RegistryBlocks.FLUX_POINT, FluxBlockLootTables::fluxDropping);
        registerLootTable(RegistryBlocks.FLUX_CONTROLLER, FluxBlockLootTables::fluxDropping);
        registerLootTable(RegistryBlocks.BASIC_FLUX_STORAGE, FluxBlockLootTables::fluxDropping);
        registerLootTable(RegistryBlocks.HERCULEAN_FLUX_STORAGE, FluxBlockLootTables::fluxDropping);
        registerLootTable(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, FluxBlockLootTables::fluxDropping);
    }

    /**
     * Pick out needed NBT from {@link TileFluxDevice#write(CompoundNBT)}
     * Convert them to be readable by {@link FluxDeviceBlock#onBlockPlacedBy(World, BlockPos, BlockState, LivingEntity, ItemStack)}
     *
     * @param block flux device block
     * @return loot table builder
     */
    @Nonnull
    protected static LootTable.Builder fluxDropping(Block block) {
        if (!(block instanceof FluxDeviceBlock)) {
            throw new IllegalArgumentException();
        }
        CopyNbt.Builder copyNbt = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
        // replace to a sub NBT compound tag to avoid conflicts with vanilla or other mods
        copyNbt.replaceOperation("0", FluxUtils.FLUX_DATA + "." + ItemFluxDevice.PRIORITY);
        copyNbt.replaceOperation("1", FluxUtils.FLUX_DATA + "." + ItemFluxDevice.LIMIT);
        copyNbt.replaceOperation("2", FluxUtils.FLUX_DATA + "." + ItemFluxDevice.DISABLE_LIMIT);
        copyNbt.replaceOperation("3", FluxUtils.FLUX_DATA + "." + ItemFluxDevice.SURGE_MODE);
        copyNbt.replaceOperation("4", FluxUtils.FLUX_DATA + "." + FluxNetworkData.NETWORK_ID);
        copyNbt.replaceOperation("6", FluxUtils.FLUX_DATA + "." + ItemFluxDevice.CUSTOM_NAME);
        copyNbt.replaceOperation("b", FluxUtils.FLUX_DATA + "." + "buffer");
        if (block instanceof FluxStorageBlock) {
            copyNbt.replaceOperation("energy", FluxUtils.FLUX_DATA + "." + "energy");
        }
        return LootTable.builder().addLootPool(withSurvivesExplosion(block,
                LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(block)
                                .acceptFunction(copyNbt)
                        )
        ));
    }
}
