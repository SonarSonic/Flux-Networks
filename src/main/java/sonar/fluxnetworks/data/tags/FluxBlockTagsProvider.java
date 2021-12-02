package sonar.fluxnetworks.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nullable;

public class FluxBlockTagsProvider extends BlockTagsProvider {

    public FluxBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, FluxNetworks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(RegistryBlocks.FLUX_BLOCK)
                .add(RegistryBlocks.FLUX_PLUG)
                .add(RegistryBlocks.FLUX_POINT)
                .add(RegistryBlocks.FLUX_CONTROLLER)
                .add(RegistryBlocks.BASIC_FLUX_STORAGE)
                .add(RegistryBlocks.HERCULEAN_FLUX_STORAGE)
                .add(RegistryBlocks.GARGANTUAN_FLUX_STORAGE);
    }
}
