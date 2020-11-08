package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;

public class CConfiguratorSettingMessage implements IMessage {

    private String customName;
    private CompoundNBT tag;

    public CConfiguratorSettingMessage() {
    }

    public CConfiguratorSettingMessage(String customName, CompoundNBT tag) {
        this.customName = customName;
        this.tag = tag;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeString(customName, 256);
        buffer.writeCompoundTag(tag);
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        PlayerEntity player = NetworkHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        String customName = buffer.readString(256);
        CompoundNBT tag = buffer.readCompoundTag();
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() == RegistryItems.FLUX_CONFIGURATOR) {
            if (tag != null && !tag.isEmpty()) {
                stack.setTagInfo(FluxConstants.TAG_FLUX_CONFIG, tag);
            }
            stack.setDisplayName(new StringTextComponent(customName));
        }
    }
}
