package sonar.fluxnetworks.common.integration;

import icyllis.modernui.forge.MuiForgeApi;
import icyllis.modernui.forge.OpenMenuEvent;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.widget.Toast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.mui.FluxDeviceUI;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MUIIntegration {

    public static void openMenu(@Nonnull Player player, @Nonnull MenuConstructor provider,
                                @Nullable Consumer<FriendlyByteBuf> writer) {
        MuiForgeApi.openMenu(player, provider, writer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void showToastError(@Nonnull FluxTranslate translate) {
        SpannableString text = new SpannableString(translate.get());
        text.setSpan(new ForegroundColorSpan(0xFFCF1515), 0, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MuiForgeApi.postToUiThread(() -> Toast.makeText(text, Toast.LENGTH_SHORT).show());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void openMenu(@Nonnull OpenMenuEvent event) {
        if (event.getMenu() instanceof FluxMenu menu && menu.mProvider != null) {
            FluxDeviceUI fragment = new FluxDeviceUI((TileFluxDevice) menu.mProvider);
            menu.mOnResultListener = fragment;
            DataSet args = new DataSet();
            args.putInt("token", menu.containerId);
            fragment.setArguments(args);
            event.set(fragment);
        }
    }
}
