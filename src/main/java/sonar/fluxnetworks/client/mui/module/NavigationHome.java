package sonar.fluxnetworks.client.mui.module;

import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.master.IDrawable;
import icyllis.modernui.gui.master.Icon;
import icyllis.modernui.gui.master.ModuleGroup;
import icyllis.modernui.gui.math.Align3H;
import icyllis.modernui.gui.math.Color3f;
import icyllis.modernui.gui.math.Direction4D;
import icyllis.modernui.gui.widget.TextIconButton;
import icyllis.modernui.system.ConstantsLibrary;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The root module of all flux gui tabs
 * This module contains all navigation buttons, and updates gui data
 *
 * @author BloCamLimb
 */
public class NavigationHome extends ModuleGroup {

    @Nonnull
    public static IFluxNetwork network = FluxNetworkInvalid.instance;

    private final List<TextIconButton> navigationButtons = new ArrayList<>();

    private final FluxBackground bg;

    public NavigationHome(@Nonnull INetworkConnector connector) {
        network = FluxNetworkCache.instance.getClientNetwork(connector.getNetworkID());

        addDrawable(bg = new FluxBackground());

        for (EnumNavigationTabs tab : EnumNavigationTabs.values()) {
            int id = tab.getId();
            Icon icon = new Icon(ConstantsLibrary.ICONS, ((id - 1) * 32) / 512f, 352 / 512f, (id * 32) / 512f, 384 / 512f, true);
            /*TextIconButton button = new TextIconButton(this, tab.getTranslatedName(), 16, 16, icon,
                    () -> {
                        switchChildModule(id);
                        if (FluxConfig.enableButtonSound)
                            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(RegistrySounds.BUTTON_CLICK, 1.0F));
                    }, TextIconButton.Direction.UP);
            button.setId(id);
            if (id == EnumNavigationTabs.TAB_HOME.getId()) {
                button.setDefaultOpen();
            }*/
            TextIconButton button = new TextIconButton.Builder(icon, tab.getTranslatedName())
                    .setWidth(16)
                    .setHeight(16)
                    .setModuleId(id)
                    .setTextDirection(Direction4D.UP)
                    .build(this)
                    .buildCallback(true, tab == EnumNavigationTabs.TAB_HOME, () -> {
                        switchChildModule(id);
                        if (FluxConfig.enableButtonSound) {
                            playSound(icyllis.modernui.system.RegistrySounds.BUTTON_CLICK_1);
                        }
                    }, false);
            navigationButtons.add(button);
        }
        navigationButtons.forEach(this::addWidget);

        if (connector instanceof TileFluxCore) {
            addChildModule(EnumNavigationTabs.TAB_HOME.getId(), () -> new FluxTileHome((TileFluxCore) connector));
        }

        switchChildModule(EnumNavigationTabs.TAB_HOME.getId());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        int id = 1;
        float ty = height / 2f - 95;
        for (TextIconButton button : navigationButtons) {
            if (id == EnumNavigationTabs.TAB_CREATE.getId()) {
                button.locate(width / 2f + 60f, ty); // spacing = 12
            } else {
                button.locate(width / 2f - 94f + id * 18, ty); // spacing = 2
            }
            id++;
        }
    }

    @Override
    protected void onChildModuleChanged(int id) {
        super.onChildModuleChanged(id);
        navigationButtons.forEach(e -> e.onModuleChanged(id));
        if (id == EnumNavigationTabs.TAB_HOME.getId()) {
            bg.setRenderNetworkName(true);
        } else {
            bg.setRenderNetworkName(false);
        }
    }

    /**
     * Flux gui background layer
     *
     * @author BloCamLimb
     */
    private static class FluxBackground implements IDrawable {

        private float x1, x2, y1, y2;

        private float r, g, b;

        private boolean renderNetworkName = false;

        public FluxBackground() {
            updateInfo();
        }

        @Override
        public void draw(@Nonnull Canvas canvas, float v) {
            canvas.setRGBA(0, 0, 0, 0.5f);
            canvas.drawRoundedRect(x1, y1, x2, y2, 5);
            canvas.setRGBA(r, g, b, 1.0f);
            canvas.drawRoundedRectFrame(x1, y1, x2, y2, 5);
            if (renderNetworkName) {
                canvas.setLineAntiAliasing(true);
                canvas.setLineWidth(2.0f);
                canvas.drawOctagonRectFrame(x1 + 18, y1 + 6, x2 - 18, y1 + 18, 2);
                canvas.setLineAntiAliasing(false);
                canvas.resetColor();
                canvas.setTextAlign(Align3H.LEFT);
                canvas.drawText(network.getNetworkName(), x1 + 22, y1 + 8);
            }
        }

        @Override
        public void resize(int width, int height) {
            this.x1 = width / 2f - 85;
            this.x2 = x1 + 170;
            this.y1 = height / 2f - 77;
            this.y2 = y1 + 170;
        }

        @Override
        public void tick(int ticks) {
            if ((ticks & 15) == 0) {
                updateInfo();
            }
        }

        private void updateInfo() {
            int color = network.getSetting(NetworkSettings.NETWORK_COLOR);
            r = Color3f.getRedFrom(color);
            g = Color3f.getGreenFrom(color);
            b = Color3f.getBlueFrom(color);
        }

        public void setRenderNetworkName(boolean renderNetworkName) {
            this.renderNetworkName = renderNetworkName;
        }
    }
}
