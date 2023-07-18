package sonar.fluxnetworks.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class FluxBlockTagsProvider extends BlockTagsProvider {

    public FluxBlockTagsProvider(PackOutput output,
                                 CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FluxNetworks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(RegistryBlocks.FLUX_BLOCK.get())
                .add(RegistryBlocks.FLUX_PLUG.get())
                .add(RegistryBlocks.FLUX_POINT.get())
                .add(RegistryBlocks.FLUX_CONTROLLER.get())
                .add(RegistryBlocks.BASIC_FLUX_STORAGE.get())
                .add(RegistryBlocks.HERCULEAN_FLUX_STORAGE.get())
                .add(RegistryBlocks.GARGANTUAN_FLUX_STORAGE.get());
    }
}
