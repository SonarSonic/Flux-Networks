package sonar.fluxnetworks.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

/**
 * Render energy
 */
public class FluxStorageRenderType extends RenderType {

    private static final ResourceLocation ENERGY_TEXTURE = new ResourceLocation(FluxNetworks.MODID, "textures/model/flux_storage_energy.png");

    private static final RenderType INSTANCE;
    private static final RenderType DIFFUSE;

    static {
        RenderType.State state = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(ENERGY_TEXTURE, false, false))
                .shadeModel(SHADE_ENABLED)
                .diffuseLighting(DIFFUSE_LIGHTING_DISABLED)
                .alpha(DEFAULT_ALPHA)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(false);
        INSTANCE = makeType(FluxNetworks.MODID + ":storage_energy", DefaultVertexFormats.ENTITY,
                GL11.GL_QUADS, 256, state);
        state = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(ENERGY_TEXTURE, false, false))
                .shadeModel(SHADE_ENABLED)
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .alpha(DEFAULT_ALPHA)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(false);
        DIFFUSE = makeType(FluxNetworks.MODID + ":storage_energy_diffuse", DefaultVertexFormats.ENTITY,
                GL11.GL_QUADS, 256, state);
    }

    private FluxStorageRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn,
                                  boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    @Nonnull
    public static RenderType getType() {
        return INSTANCE;
    }

    @Nonnull
    public static RenderType getDiffuse(boolean diffuse) {
        return diffuse ? DIFFUSE : INSTANCE;
    }
}
