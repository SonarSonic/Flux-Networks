package sonar.fluxnetworks.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;

public class FeedbackPacket extends AbstractPacket {

    public EnumFeedbackInfo info;

    public FeedbackPacket(EnumFeedbackInfo info) {
        this.info = info;
    }

    public FeedbackPacket(PacketBuffer buf) {
        info = EnumFeedbackInfo.values()[buf.readInt()];
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(info.ordinal());
    }

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        if(info == EnumFeedbackInfo.SUCCESS || info == EnumFeedbackInfo.SUCCESS_2 || info == EnumFeedbackInfo.PASSWORD_REQUIRE) {
            FluxNetworks.proxy.setFeedback(info, true);
        } else {
            FluxNetworks.proxy.setFeedback(info, false);
        }
        return null;
    }

}
