package sonar.fluxnetworks.client.mui;

import icyllis.modernui.gui.master.GlobalModuleManager;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sonar.fluxnetworks.client.mui.module.NavigationHome;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

public class MUIIntegration {

    public static void init(FMLClientSetupEvent event){
        ScreenManager.registerFactory(RegistryBlocks.CONTAINER_CONNECTOR, GlobalModuleManager.INSTANCE.castModernScreen(c -> () -> new NavigationHome(c.connector)));
    }

}
