package sonar.fluxnetworks.client.mui.module;

import icyllis.modernui.gui.master.GlobalModuleManager;
import icyllis.modernui.gui.master.Icon;
import icyllis.modernui.gui.master.ModuleGroup;
import icyllis.modernui.gui.math.DelayedRunnable;
import icyllis.modernui.gui.widget.TextIconButton;
import icyllis.modernui.system.ConstantsLibrary;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.mui.element.FluxBackground;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The root module of all flux gui tabs
 * @author BloCamLimb
 */
public class NavigationHome extends ModuleGroup {

    private final IFluxNetwork network;

    private final List<TextIconButton> navigationButtons = new ArrayList<>();

    public NavigationHome(@Nonnull INetworkConnector connector) {
        network = FluxNetworkCache.instance.getClientNetwork(connector.getNetworkID());

        addDrawable(new FluxBackground(this));

        for (EnumNavigationTabs tab : EnumNavigationTabs.values()) {
            int i = tab.getId();
            Icon icon = new Icon(ConstantsLibrary.ICONS, ((i - 1) * 32) / 512f, 352 / 512f, (i * 32) / 512f, 384 / 512f, true);
            TextIconButton button = new TextIconButton(this, tab.getTranslatedName(), 16, 16, icon,
                    () -> switchChildModule(i), TextIconButton.Direction.UP);
            button.setId(i);
            navigationButtons.add(button);
        }
        navigationButtons.forEach(this::addWidget);

        if (connector instanceof TileFluxCore) {
            addChildModule(EnumNavigationTabs.TAB_HOME.getId(), () -> new FluxTileHome((TileFluxCore) connector));
        }

        GlobalModuleManager.INSTANCE.scheduleRunnable(new DelayedRunnable(() -> switchChildModule(EnumNavigationTabs.TAB_HOME.getId()), 0));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        int i = 0;
        float ny = height / 2f - 95;
        for (TextIconButton button : navigationButtons) {
            if (i == EnumNavigationTabs.TAB_CREATE.getId() - 1) {
                button.setPos(width / 2f + 60f, ny); // spacing = 12
            } else {
                button.setPos(width / 2f - 76f + i * 18, ny); // spacing = 2
            }
            i++;
        }
    }

    @Override
    protected void moduleChanged(int id) {
        super.moduleChanged(id);
        navigationButtons.forEach(e -> e.onModuleChanged(id));
    }

    public IFluxNetwork getNetwork() {
        return network;
    }
}
