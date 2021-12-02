package sonar.fluxnetworks.data.loot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluxLootTableProvider extends LootTableProvider {

    public FluxLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Nonnull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(Pair.of(FluxBlockLoot::new, LootContextParamSets.BLOCK));
    }

    @Nonnull
    @Override
    public String getName() {
        return super.getName() + ":" + FluxNetworks.NAME_CPT;
    }

    @Override
    protected void validate(@Nonnull Map<ResourceLocation, LootTable> map,
                            @Nonnull ValidationContext validationContext) {
        // NO-OP
    }
}
