package sonar.fluxnetworks.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.IMessage;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;

import javax.annotation.Nonnull;

public class SFeedbackMessage implements IMessage {

    private FeedbackInfo info;

    public SFeedbackMessage() {
    }

    public SFeedbackMessage(FeedbackInfo info) {
        this.info = info;
    }

    @Override
    public void encode(@Nonnull PacketBuffer buffer) {
        buffer.writeVarInt(info.ordinal());
    }

    @Override
    public void handle(@Nonnull PacketBuffer buffer, @Nonnull NetworkEvent.Context context) {
        FeedbackInfo info = FeedbackInfo.values()[buffer.readVarInt()];
        boolean operational = info == FeedbackInfo.SUCCESS
                || info == FeedbackInfo.SUCCESS_2 || info == FeedbackInfo.PASSWORD_REQUIRE;
        Screen screen = Minecraft.getInstance().currentScreen;
        if (!operational) {
            FluxClientCache.setFeedback(info);
        } else if (screen instanceof GuiFluxCore) {
            ((GuiFluxCore) screen).onOperationalFeedback(info);
        }
        buffer.release();
    }
}
