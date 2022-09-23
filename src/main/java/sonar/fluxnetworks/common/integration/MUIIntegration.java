package sonar.fluxnetworks.common.integration;

import icyllis.modernui.core.Core;
import icyllis.modernui.forge.MuiForgeApi;
import icyllis.modernui.forge.OpenMenuEvent;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.widget.Toast;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.mui.FluxDeviceUI;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;

public class MUIIntegration {
    @OnlyIn(Dist.CLIENT)
    public static void showToastError(@Nonnull FluxTranslate translate) {
        SpannableString text = new SpannableString(translate.get());
        text.setSpan(new ForegroundColorSpan(0xFFCF1515), 0, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (Core.isOnUiThread()) {
            Toast.makeText(text, Toast.LENGTH_SHORT).show();
        } else {
            MuiForgeApi.postToUiThread(() -> Toast.makeText(text, Toast.LENGTH_SHORT).show());
        }
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
