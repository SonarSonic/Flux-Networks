package sonar.flux.client;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.RenderHelper;
import sonar.flux.common.tileentity.TileEntityStorage;

public class RenderFluxStorage extends TileEntitySpecialRenderer<TileEntityStorage> {

	@Override
	public void renderTileEntityAt(TileEntityStorage te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.storage.getEnergyStored() != 0 && te.storage.getMaxEnergyStored() != 0) {
			GL11.glPushMatrix();
			RenderHelper.saveBlendState();			
			GlStateManager.translate(x, y, z);
			GlStateManager.disableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableBlend();
			//GlStateManager.blendFunc(770, 1);
			//GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
			OpenGlHelper.glBlendFunc(770, 1, 1, 0);
			float full =(1 - 0.0625F*3);
			float bottom = 0 + (0.0625F * 2);
			float left = 0 + (0.0625F * 2);
			float i = ((te.storage.getEnergyStored() * full) / te.storage.getMaxEnergyStored())+bottom;
			
			int colour = te.colour.getObject();
			int clearColour = Color.WHITE.getRGB();
			EnumFacing face = EnumFacing.SOUTH;
			GL11.glPushMatrix();
			GL11.glRotatef(face.getHorizontalAngle(), 0, 1, 0);
			GL11.glTranslated(-face.getFrontOffsetX(), 0, 0.0626F);
			RenderHelper.drawRect(left, i, 1 - (0.0625F * 2), bottom, colour);
			GL11.glPopMatrix();
			
			face = EnumFacing.NORTH;
			GL11.glPushMatrix();
			GL11.glRotatef(face.getHorizontalAngle(), 0, 1, 0);
			GL11.glTranslated(-1, 0, face.getFrontOffsetZ() + 0.0625);
			RenderHelper.drawRect(left, i, 1 - (0.0625F * 2), bottom, colour);
			GL11.glPopMatrix();

			face = EnumFacing.EAST;
			GL11.glPushMatrix();
			GL11.glRotatef(face.getHorizontalAngle(), 0, 1, 0);
			GL11.glTranslated(0, 0, -1 + 0.0625);
			RenderHelper.drawRect(left, i, 1 - (0.0625F * 2), bottom, colour);
			GL11.glPopMatrix();

			face = EnumFacing.WEST;
			GL11.glPushMatrix();
			GL11.glRotatef(face.getHorizontalAngle(), 0, 1, 0);
			GL11.glTranslated(-1, 0, 0.0625);
			RenderHelper.drawRect(left, i, 1 - (0.0625F * 2), bottom, colour);
			GL11.glPopMatrix();
			
			face = EnumFacing.UP;
			GL11.glPushMatrix();
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glTranslated(0, 0, -i);
			RenderHelper.drawRect(0 + (0.0625F), 1 - 0.0625F, 1 - (0.0625F), 0 + (0.0625F), colour);
			GL11.glPopMatrix();
			
			GlStateManager.enableTexture2D();
			RenderHelper.restoreBlendState();
			GL11.glPopMatrix();
		}
	}

}
