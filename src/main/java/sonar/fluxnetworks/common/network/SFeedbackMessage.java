package sonar.fluxnetworks.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.client.FluxClientCache;

import javax.annotation.Nonnull;

public class SFeedbackMessage implements IMessage {

    private EnumFeedbackInfo info;

    public SFeedbackMessage() {
    }

    public SFeedbackMessage(EnumFeedbackInfo info) {
        this.info = info;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(info.ordinal());
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        EnumFeedbackInfo info = EnumFeedbackInfo.values()[buffer.readVarInt()];
        FluxClientCache.setFeedback(info, info == EnumFeedbackInfo.SUCCESS
                || info == EnumFeedbackInfo.SUCCESS_2 || info == EnumFeedbackInfo.PASSWORD_REQUIRE);
        buffer.release();
    }
}
