package sonar.fluxnetworks.client.gui;
/*TODO FLUX CONFIG!
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import java.util.ArrayList;
import java.util.List;

public class GuiModConfig extends ScreenCon {

    public GuiModConfig(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), FluxNetworks.MODID, false, false, FluxNetworks.NAME);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new ConfigElement(FluxConfig.config.getCategory(FluxConfig.GENERAL)));
        list.add(new ConfigElement(FluxConfig.config.getCategory(FluxConfig.CLIENT)));
        list.add(new ConfigElement(FluxConfig.config.getCategory(FluxConfig.NETWORKS)));
        list.add(new ConfigElement(FluxConfig.config.getCategory(FluxConfig.ENERGY)));
        list.add(new ConfigElement(FluxConfig.config.getCategory(FluxConfig.BLACKLIST)));
        return list;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        FluxConfig.config.save();
        FluxConfig.read();
        FluxConfig.verifyAndReadBlacklist();
    }
}
*/