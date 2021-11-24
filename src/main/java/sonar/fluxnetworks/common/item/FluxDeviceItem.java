package sonar.fluxnetworks.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.render.FluxStorageItemRenderer;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class FluxDeviceItem extends BlockItem {

    public FluxDeviceItem(Block block, Properties props) {
        super(block, props);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_DATA);
        if (tag != null && tag.contains(FluxConstants.CUSTOM_NAME)) {
            return new TextComponent(tag.getString(FluxConstants.CUSTOM_NAME));
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip,
                                @Nonnull TooltipFlag flag) {
        CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_DATA);
        if (tag != null) {
            if (tag.contains(FluxConstants.NETWORK_ID)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.NETWORK_FULL_NAME.t() + ": " +
                        ChatFormatting.RESET + FluxClientCache.getDisplayName(tag)));
            }

            if (tag.contains(FluxConstants.LIMIT)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.TRANSFER_LIMIT.t() + ": " +
                        ChatFormatting.RESET + EnergyType.storage(tag.getLong(FluxConstants.LIMIT))));
            }

            if (tag.contains(FluxConstants.PRIORITY)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.PRIORITY.t() + ": " +
                        ChatFormatting.RESET + tag.getInt(FluxConstants.PRIORITY)));
            }

            if (tag.contains(FluxConstants.BUFFER)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.INTERNAL_BUFFER.t() + ": " +
                        ChatFormatting.RESET + EnergyType.storage(tag.getLong(FluxConstants.BUFFER))));
            } else if (tag.contains(FluxConstants.ENERGY)) {
                long energy = tag.getLong(FluxConstants.ENERGY);
                Block block = getBlock();
                double percentage;
                if (block instanceof FluxStorageBlock)
                    percentage = Math.min((double) energy / ((FluxStorageBlock) block).getEnergyCapacity(), 1.0);
                else
                    percentage = 0;
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.ENERGY_STORED.t() + ": " +
                        ChatFormatting.RESET + EnergyType.storage(energy) + String.format(" (%.1f%%)",
                        percentage * 100)));
            }

        } else {
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
        if (getBlock() instanceof FluxStorageBlock) {
            consumer.accept(new IItemRenderProperties() {
                @Override
                public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                    return new FluxStorageItemRenderer();
                }
            });
        }
    }
}
