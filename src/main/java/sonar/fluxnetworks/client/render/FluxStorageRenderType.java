package sonar.fluxnetworks.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

public class FluxStorageRenderType extends RenderType {

    private static final ResourceLocation ENERGY_TEXTURE = new ResourceLocation(FluxNetworks.MODID, "textures/model/flux_storage_energy.png");

    public static final RenderType INSTANCE = FluxStorageRenderType.getGlowType();

    private FluxStorageRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    @Nonnull
    private static RenderType getGlowType() {
        RenderType.State state = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(ENERGY_TEXTURE, false, false))
                .shadeModel(SHADE_ENABLED)
                .alpha(DEFAULT_ALPHA)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .build(true);
        return makeType("glow_type", DefaultVertexFormats.ENTITY, GL11.GL_QUADS, 256, true, true, state);
    }
}
