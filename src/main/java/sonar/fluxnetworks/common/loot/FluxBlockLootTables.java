package sonar.fluxnetworks.common.loot;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyNbt;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

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
    protected final void registerLootTable(Block blockIn, @Nonnull LootTable.Builder table) {
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

    @Nonnull
    protected static LootTable.Builder fluxDropping(Block block) {
        //TODO Add ops, match TILE_DROP and SAVE_ALL NBT key name in TileEntity#write and #writeCustomNBT
        // so we can read NBT from the itemStack
        return LootTable.builder().addLootPool(withSurvivesExplosion(block,
                LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(block)
                                .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY))
                        )
        ));
    }
}
