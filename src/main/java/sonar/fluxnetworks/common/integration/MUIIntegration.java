package sonar.fluxnetworks.common.integration;

import icyllis.modernui.core.Core;
import icyllis.modernui.forge.MenuScreenFactory;
import icyllis.modernui.forge.MuiForgeApi;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Spanned;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.widget.Toast;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.mui.FluxDeviceUI;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;

public class MUIIntegration {

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

    // screen the screen
    @Nonnull
    public static MenuScreens.ScreenConstructor<FluxMenu, AbstractContainerScreen<FluxMenu>> getScreenFactory(
            MenuScreens.ScreenConstructor<FluxMenu, AbstractContainerScreen<FluxMenu>> fallback) {
        MenuScreens.ScreenConstructor<FluxMenu, AbstractContainerScreen<FluxMenu>> dedicated = getScreenFactory();
        return (menu, inventory, title) -> FluxConfig.enableGuiDebug ? dedicated.create(menu, inventory, title) :
                fallback.create(menu, inventory, title);
    }

    private static MenuScreens.ScreenConstructor<FluxMenu, AbstractContainerScreen<FluxMenu>> getScreenFactory() {
        return MenuScreenFactory.create(menu -> {
            FluxDeviceUI fragment = new FluxDeviceUI((TileFluxDevice) menu.mProvider);
            menu.mOnResultListener = fragment;
            DataSet args = new DataSet();
            args.putInt("token", menu.containerId);
            fragment.setArguments(args);
            return fragment;
        });
    }
}
