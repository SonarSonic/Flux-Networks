package sonar.fluxnetworks.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import javax.annotation.Nonnull;
import java.util.*;

public class FluxLootTableProvider extends LootTableProvider {

    public FluxLootTableProvider(PackOutput packOutput) {
        super(packOutput, Collections.emptySet(), List.of(
                new SubProviderEntry(FluxBlockLoot::new, LootContextParamSets.BLOCK)));
    }

    @Override
    protected void validate(@Nonnull Map<ResourceLocation, LootTable> map,
                            @Nonnull ValidationContext validationContext) {
        // NO-OP
    }
}
