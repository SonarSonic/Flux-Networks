package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.common.connection.*;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import java.util.List;

public class GuiTabDetailedSelection extends GuiTabSelection {

    private int timer = 0;

    public GuiTabDetailedSelection(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mGridHeight = 20;
        mGridPerPage = 6;
        mElementWidth = 146;
        mElementHeight = 19;
    }

    @Override
    protected void renderBarAndName(PoseStack poseStack, FluxNetwork element, int x, int y, boolean selected) {
        blit(poseStack, x, y, 0, 32, mElementWidth, 6);
        // stretch the middle part
        blit(poseStack, x, y + 6, mElementWidth, mElementHeight - 12, 0, 32 + 6, mElementWidth, 6, 256, 256);
        blit(poseStack, x, y + mElementHeight - 6, 0, 32 + 12, mElementWidth, 6);
        font.draw(poseStack, element.getNetworkName(), x + 4, y + 2, selected ? 0xffffff : 0x606060);

        poseStack.pushPose();
        poseStack.scale(0.75f, 0.75f, 1);
        font.draw(poseStack, "C: " + element.getStatistics().getConnectionCount() + ", T: " +
                        element.getStatistics().averageTickMicro + " \u00b5s/t",
                (int) ((x + 4) / 0.75), (int) ((y + 11) / 0.75), getNetwork() == element ? 0xffffff : 0x808080);
        poseStack.popPose();
    }

    @Override
    protected List<Component> getElementTooltips(@Nonnull FluxNetwork element) {
        final List<Component> components = super.getElementTooltips(element);
        final NetworkStatistics stats = element.getStatistics();
        components.add(FluxTranslate.PLUGS.makeComponent().append(ChatFormatting.GRAY + ": " +
                ChatFormatting.RESET + stats.fluxPlugCount));
        components.add(FluxTranslate.POINTS.makeComponent().append(ChatFormatting.GRAY + ": " +
                ChatFormatting.RESET + stats.fluxPointCount));
        components.add(FluxTranslate.STORAGES.makeComponent().append(ChatFormatting.GRAY + ": " +
                ChatFormatting.RESET + stats.fluxStorageCount));
        components.add(FluxTranslate.CONTROLLERS.makeComponent().append(ChatFormatting.GRAY + ": " +
                ChatFormatting.RESET + stats.fluxControllerCount));
        components.add(FluxTranslate.INPUT.makeComponent().append(ChatFormatting.GRAY + ": " + ChatFormatting.RESET +
                EnergyType.FE.getUsageCompact(stats.energyInput)));
        components.add(FluxTranslate.OUTPUT.makeComponent().append(ChatFormatting.GRAY + ": " + ChatFormatting.RESET +
                EnergyType.FE.getUsageCompact(stats.energyOutput)));
        components.add(FluxTranslate.BUFFER.makeComponent().append(ChatFormatting.GRAY + ": " + ChatFormatting.RESET +
                EnergyType.FE.getStorageCompact(stats.totalBuffer)));
        components.add(FluxTranslate.ENERGY.makeComponent().append(ChatFormatting.GRAY + ": " + ChatFormatting.RESET +
                EnergyType.FE.getStorageCompact(stats.totalEnergy)));
        components.add(FluxTranslate.AVERAGE_TICK.makeComponent().append(": " + stats.averageTickMicro + " \u00b5s/t"));
        return components;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (timer == 0) {
            ClientMessages.updateNetwork(getToken(), mCurrent, FluxConstants.NBT_NET_STATISTICS);
        }
        timer = (timer + 1) % 20;
    }
}
