package sonar.fluxnetworks.common.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import sonar.fluxnetworks.client.render.FluxStorageItemRenderer;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FluxStorageItem extends FluxDeviceItem {

    public FluxStorageItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new FluxStorageItemRenderer();
            }
        });
    }
}
