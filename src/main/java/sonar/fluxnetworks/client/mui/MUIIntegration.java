package sonar.fluxnetworks.client.mui;

import icyllis.modernui.gui.master.GlobalModuleManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sonar.fluxnetworks.client.mui.module.NavigationHome;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class MUIIntegration {

    public static void init() {
        GlobalModuleManager.INSTANCE.registerContainerScreen(RegistryBlocks.CONTAINER_CONNECTOR, c -> () -> new NavigationHome(c.connector));
    }

}
