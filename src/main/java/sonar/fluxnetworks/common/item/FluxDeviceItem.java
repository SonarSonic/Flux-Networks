package sonar.fluxnetworks.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluxDeviceItem extends BlockItem {

    public FluxDeviceItem(Block block, Properties props) {
        super(block, props);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_DATA);
        if (tag != null) {
            String value = tag.getString(FluxConstants.CUSTOM_NAME);
            if (!value.isEmpty()) {
                return new TextComponent(value);
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip,
                                @Nonnull TooltipFlag flag) {
        CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_DATA);
        if (tag != null) {
            if (tag.contains(FluxConstants.NETWORK_ID)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.NETWORK_FULL_NAME.get() + ": " +
                        ChatFormatting.RESET + ClientCache.getNetwork(
                        tag.getInt(FluxConstants.NETWORK_ID)).getNetworkName()));
            }

            if (tag.contains(FluxConstants.LIMIT)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.TRANSFER_LIMIT.get() + ": " +
                        ChatFormatting.RESET + EnergyType.FE.getStorage(tag.getLong(FluxConstants.LIMIT))));
            }

            if (tag.contains(FluxConstants.PRIORITY)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.PRIORITY.get() + ": " +
                        ChatFormatting.RESET + tag.getInt(FluxConstants.PRIORITY)));
            }

            if (tag.contains(FluxConstants.BUFFER)) {
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.INTERNAL_BUFFER.get() + ": " +
                        ChatFormatting.RESET + EnergyType.FE.getStorage(tag.getLong(FluxConstants.BUFFER))));
            } else if (tag.contains(FluxConstants.ENERGY)) {
                long energy = tag.getLong(FluxConstants.ENERGY);
                Block block = getBlock();
                double percentage;
                if (block instanceof FluxStorageBlock)
                    percentage = Math.min((double) energy / ((FluxStorageBlock) block).getEnergyCapacity(), 1.0);
                else
                    percentage = 0;
                tooltip.add(new TextComponent(ChatFormatting.BLUE + FluxTranslate.ENERGY_STORED.get() + ": " +
                        ChatFormatting.RESET + EnergyType.FE.getStorage(energy) + String.format(" (%.1f%%)",
                        percentage * 100)));
            }

        } else {
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }
}
