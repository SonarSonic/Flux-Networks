package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.List;

public abstract class FluxStorageBlock extends FluxNetworkBlock {

    public FluxStorageBlock(Properties props) {
        super(props);
    }

    public abstract int getMaxStorage();

    public static class Basic extends FluxStorageBlock {

        public Basic(Properties props) {
            super(props);
        }

        @Override
        public int getMaxStorage() {
            return FluxConfig.basicCapacity;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return new TileFluxStorage.Basic();
        }

    }
    public static class Herculean extends FluxStorageBlock {

        public Herculean(Properties props) {
            super(props);
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return new TileFluxStorage.Herculean();
        }

        @Override
        public int getMaxStorage() {
            return FluxConfig.herculeanCapacity;
        }
    }

    public static class Gargantuan extends FluxStorageBlock {

        public Gargantuan(Properties props) {
            super(props);
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return new TileFluxStorage.Gargantuan();
        }

        @Override
        public int getMaxStorage() {
            return FluxConfig.gargantuanCapacity;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent(FluxTranslate.FLUX_STORAGE_TOOLTIP.k()));
    }
}
