package sonar.flux.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import sonar.core.helpers.RenderHelper;
import sonar.flux.common.tileentity.TileStorage;

import java.awt.Color;

import static net.minecraft.client.renderer.GlStateManager.*;
public class RenderFluxStorageTile extends TileEntitySpecialRenderer<TileStorage> {

    @Override
    public void render(TileStorage te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		enableBlend();
		render(te.storage.getEnergyStored(), te.storage.getMaxEnergyStored(), FluxColourHandler.getOrRequestNetworkColour(te.getNetworkID()), x, y, z, partialTicks, destroyStage, alpha);
		disableBlend();
	}

	public static void render(int stored, int capacity, int colour, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if (stored == 0 || capacity == 0) {
			return;
		}
		pushMatrix();
		translate(x, y, z);
		disableTexture2D();
		enableAlpha();
		disableLighting();
		tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, 1, 1, 0);
		float full = 1 - 0.0625F * 3;
		float bottom = 0 + 0.0625F * 2;
		float left = 0 + 0.0625F * 2;
		float i = stored * full / capacity + bottom;

		int clearColour = Color.WHITE.getRGB();
		EnumFacing face = EnumFacing.SOUTH;

		float f3 = (float) (colour >> 24 & 255) / 255.0F;
		float f = (float) (colour >> 16 & 255) / 255.0F;
		float f1 = (float) (colour >> 8 & 255) / 255.0F;
		float f2 = (float) (colour & 255) / 255.0F;
		color(f, f1, f2, f3);


		pushMatrix();
		rotate(face.getHorizontalAngle(), 0, 1, 0);
		translate(-face.getFrontOffsetX(), 0, 0.0626F);
		RenderHelper.drawRect(left, i, 1 - 0.0625F * 2, bottom);
		popMatrix();

		face = EnumFacing.NORTH;
		pushMatrix();
		rotate(face.getHorizontalAngle(), 0, 1, 0);
		translate(-1, 0, face.getFrontOffsetZ() + 0.0625);
		RenderHelper.drawRect(left, i, 1 - 0.0625F * 2, bottom);
		popMatrix();

		face = EnumFacing.EAST;
		pushMatrix();
		rotate(face.getHorizontalAngle(), 0, 1, 0);
		translate(0, 0, -1 + 0.0625);
		RenderHelper.drawRect(left, i, 1 - 0.0625F * 2, bottom);
		popMatrix();

		face = EnumFacing.WEST;
		pushMatrix();
		rotate(face.getHorizontalAngle(), 0, 1, 0);
		translate(-1, 0, 0.0625);
		RenderHelper.drawRect(left, i, 1 - 0.0625F * 2, bottom);
		popMatrix();

		face = EnumFacing.UP;
		pushMatrix();
		rotate(90, 1, 0, 0);
		translate(0, 0, -i);
		RenderHelper.drawRect(0 + 0.0625F, 1 - 0.0625F, 1 - 0.0625F, 0 + 0.0625F);
		popMatrix();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		enableLighting();
		disableAlpha();
		enableTexture2D();
		popMatrix();
	}

}
