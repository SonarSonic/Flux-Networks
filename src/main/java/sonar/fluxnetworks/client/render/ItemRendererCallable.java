package sonar.fluxnetworks.client.render;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.concurrent.Callable;

/**prevents crashes server side, by having these within this class instead*/
public class ItemRendererCallable {

    public static Callable<ItemStackTileEntityRenderer> getStorageRenderer(){
        return ItemFluxStorageRenderer::new;
    }

}
