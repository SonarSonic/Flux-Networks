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
                .add(RegistryBlocks.FLUX_BLOCK.get())
                .add(RegistryBlocks.FLUX_PLUG.get())
                .add(RegistryBlocks.FLUX_POINT.get())
                .add(RegistryBlocks.FLUX_CONTROLLER.get())
                .add(RegistryBlocks.BASIC_FLUX_STORAGE.get())
                .add(RegistryBlocks.HERCULEAN_FLUX_STORAGE.get())
                .add(RegistryBlocks.GARGANTUAN_FLUX_STORAGE.get());
    }
}
