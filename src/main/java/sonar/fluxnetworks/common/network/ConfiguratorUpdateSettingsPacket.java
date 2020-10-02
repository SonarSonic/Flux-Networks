package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import net.minecraft.item.ItemStack;
import sonar.fluxnetworks.common.misc.FluxUtils;

public class ConfiguratorUpdateSettingsPacket extends AbstractPacket{

    public String customName;
    public CompoundNBT tag;

    public ConfiguratorUpdateSettingsPacket(String customName, CompoundNBT tag) {
        this.customName = customName;
        this.tag = tag;
    }

    public ConfiguratorUpdateSettingsPacket(PacketBuffer buf) {
        customName = buf.readString(256);
        tag = buf.readCompoundTag();
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeString(customName);
        buf.writeCompoundTag(tag);
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        ItemStack stack = player.getHeldItemMainhand();
        if(stack.getItem() instanceof ItemFluxConfigurator){
            if(!tag.isEmpty()) {
                stack.setTagInfo(FluxUtils.CONFIGS_TAG, tag);
            }
            if(customName != null){
                stack.setDisplayName(new StringTextComponent(customName));
            }
        }
        return null;
    }
}
