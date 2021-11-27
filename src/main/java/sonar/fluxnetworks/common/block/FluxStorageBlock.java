package sonar.fluxnetworks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.common.device.FluxDeviceEntity;
import sonar.fluxnetworks.common.device.FluxStorageEntity;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public abstract class FluxStorageBlock extends FluxDeviceBlock {

    protected FluxStorageBlock(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(FluxTranslate.FLUX_STORAGE_TOOLTIP.getTextComponent());
        tooltip.add(FluxTranslate.FLUX_STORAGE_TOOLTIP_2.getTextComponent(EnergyType.storage(getEnergyCapacity())));
    }

    public abstract long getEnergyCapacity();

    public static class Basic extends FluxStorageBlock {

        public Basic(Properties props) {
            super(props);
        }

        @Override
        public long getEnergyCapacity() {
            return FluxConfig.basicCapacity;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new FluxStorageEntity.Basic(pos, state);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                      BlockEntityType<T> type) {
            if (type == RegistryBlocks.BASIC_FLUX_STORAGE_ENTITY) {
                return FluxDeviceEntity.getTicker(level);
            }
            return null;
        }
    }

    public static class Herculean extends FluxStorageBlock {

        public Herculean(Properties props) {
            super(props);
        }

        @Override
        public long getEnergyCapacity() {
            return FluxConfig.herculeanCapacity;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new FluxStorageEntity.Herculean(pos, state);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                      BlockEntityType<T> type) {
            if (type == RegistryBlocks.HERCULEAN_FLUX_STORAGE_ENTITY) {
                return FluxDeviceEntity.getTicker(level);
            }
            return null;
        }
    }

    public static class Gargantuan extends FluxStorageBlock {

        public Gargantuan(Properties props) {
            super(props);
        }

        @Override
        public long getEnergyCapacity() {
            return FluxConfig.gargantuanCapacity;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new FluxStorageEntity.Gargantuan(pos, state);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                      BlockEntityType<T> type) {
            if (type == RegistryBlocks.GARGANTUAN_FLUX_STORAGE_ENTITY) {
                return FluxDeviceEntity.getTicker(level);
            }
            return null;
        }
    }
}
