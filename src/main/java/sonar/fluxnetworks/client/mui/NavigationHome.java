package sonar.fluxnetworks.client.mui;

import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.master.IDrawable;
import icyllis.modernui.gui.master.Icon;
import icyllis.modernui.gui.master.ModuleGroup;
import icyllis.modernui.gui.math.Align3H;
import icyllis.modernui.gui.math.Color3f;
import icyllis.modernui.gui.math.Direction4D;
import icyllis.modernui.gui.math.Locator;
import icyllis.modernui.gui.widget.TextIconButton;
import icyllis.modernui.system.ConstantsLibrary;
import icyllis.modernui.system.RegistrySounds;
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
    public static IFluxNetwork network = FluxNetworkInvalid.INSTANCE;

    private final List<TextIconButton> navigationButtons = new ArrayList<>();

    private final FluxBackground bg;

    private final INetworkConnector connector;

    public NavigationHome(@Nonnull INetworkConnector connector) {
        network = FluxNetworkCache.INSTANCE.getClientNetwork(connector.getNetworkID());
        this.connector = connector;

        addDrawable(bg = new FluxBackground());

        for (EnumNavigationTabs tab : EnumNavigationTabs.values()) {
            int id = tab.getId();
            Icon icon = new Icon(ConstantsLibrary.ICONS, ((id - 1) * 32) / 512f, 352 / 512f, (id * 32) / 512f, 384 / 512f, true);
            Locator locator;
            if (tab == EnumNavigationTabs.TAB_CREATE) {
                locator = new Locator(60, -95); // spacing = 12
            } else {
                locator = new Locator(id * 18 - 94, -95); // spacing = 2
            }
            TextIconButton button = new TextIconButton.Builder(icon, tab.getTranslatedName())
                    .setWidth(16)
                    .setHeight(16)
                    .setLocator(locator)
                    .setModuleId(id)
                    .setTextDirection(Direction4D.UP)
                    .build(this)
                    .buildCallback(tab == EnumNavigationTabs.TAB_HOME, () -> {
                        switchChildModule(id);
                        if (FluxConfig.enableButtonSound) {
                            playSound(RegistrySounds.BUTTON_CLICK_1);
                        }
                    });
            navigationButtons.add(button);
        }
        navigationButtons.forEach(this::addWidget);

        if (connector instanceof TileFluxCore) {
            addChildModule(EnumNavigationTabs.TAB_HOME.getId(), () -> new FluxTileHome((TileFluxCore) connector));
        }
        addChildModule(EnumNavigationTabs.TAB_SELECTION.getId(), () -> new NetworkSelection(connector));
        addChildModule(EnumNavigationTabs.TAB_WIRELESS.getId(), WirelessCharging::new);

        switchChildModule(EnumNavigationTabs.TAB_HOME.getId());
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

    @Override
    public void tick(int ticks) {
        super.tick(ticks);
        //TODO use packet
        network = FluxNetworkCache.INSTANCE.getClientNetwork(connector.getNetworkID());
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
            updateColor();
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
                //TODO use packet
                updateColor();
            }
        }

        private void updateColor() {
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
