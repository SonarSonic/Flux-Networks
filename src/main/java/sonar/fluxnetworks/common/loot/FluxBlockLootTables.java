package sonar.fluxnetworks.common.loot;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootTable;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;
import java.util.Set;

public class FluxBlockLootTables extends BlockLootTables {

    private final Set<Block> knownBlocks = new ObjectArraySet<>();

    @Nonnull
    @Override
    public Iterable<net.minecraft.block.Block> getKnownBlocks() {
        return knownBlocks;
    }

    @Override
    protected void registerLootTable(Block blockIn, @Nonnull LootTable.Builder table) {
        super.registerLootTable(blockIn, table);
        knownBlocks.add(blockIn);
    }

    @Override
    protected void addTables() {
        registerLootTable(RegistryBlocks.FLUX_PLUG, BlockLootTables::droppingWithName);
    }
}
