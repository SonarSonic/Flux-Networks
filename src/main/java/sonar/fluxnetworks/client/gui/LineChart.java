package sonar.fluxnetworks.client.gui;

/*
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

*/
/**
 * Line chart that using OpenGL.
 *
 * @author BloCamLimb
 *//*

public class LineChart {

    private final int x, y;
    private final int height;

    private final int linePoints;

    private final String displayUnitX;
    private String displayUnitY;

    private long maxUnitY;
    private final String suffixUnitY;

    private LongList data = new LongArrayList();

    private final FloatList currentHeight;
    private final FloatList targetHeight;

    public LineChart(int x, int y, int height, int linePoints, String displayUnitX, String suffixUnitY) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.linePoints = linePoints;
        this.displayUnitX = displayUnitX;
        this.suffixUnitY = suffixUnitY;

        this.currentHeight = new FloatArrayList(linePoints);
        for (int i = 0; i < linePoints; i++) {
            currentHeight.add(y + height);
        }
        this.targetHeight = new FloatArrayList(linePoints);
        for (int i = 0; i < linePoints; i++) {
            targetHeight.add(y + height);
        }
    }

    public void drawChart(Minecraft mc, MatrixStack matrixStack, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth((float) mc.getMainWindow().getGuiScaleFactor());

        builder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < currentHeight.size(); i++) {
            builder.pos(x + 20 * i, currentHeight.getFloat(i), 0).color(255, 255, 255, 255).endVertex();
        }
        tessellator.draw();

        glDisable(GL_LINE_SMOOTH);
        glLineWidth(1.0f);

        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glPointSize((float) (4.0f * mc.getMainWindow().getGuiScaleFactor()));

        builder.begin(GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < currentHeight.size(); i++) {
            builder.pos(x + 20 * i, currentHeight.getFloat(i), 0).color(255, 255, 255, 255).endVertex();
        }
        tessellator.draw();

        glDisable(GL_POINT_SMOOTH);
        glPointSize(1.0f);

        Screen.fill(matrixStack, x - 16, y + height, x + 116, y + height + 1, 0xcfffffff);
        Screen.fill(matrixStack, x - 14, y - 6, x - 13, y + height + 3, 0xcfffffff);

        matrixStack.push();
        matrixStack.scale(0.625f, 0.625f, 1);
        mc.fontRenderer.drawString(matrixStack, suffixUnitY, (x - 15) * 1.6f - mc.fontRenderer.getStringWidth(suffixUnitY),
                (y - 7.5f) * 1.6f, 0xffffff);
        mc.fontRenderer.drawString(matrixStack, displayUnitY, (x - 15) * 1.6f - mc.fontRenderer.getStringWidth(displayUnitY),
                (y - 2) * 1.6f, 0xffffff);
        mc.fontRenderer.drawString(matrixStack, displayUnitX, ((x + 118) * 1.6f - mc.fontRenderer.getStringWidth(displayUnitX)),
                (y + height + 1.5f) * 1.6f, 0xffffff);
        for (int i = 0; i < data.size(); i++) {
            String d = FluxUtils.compact(data.getLong(i));
            mc.fontRenderer.drawString(matrixStack, d, ((x + 20 * i) * 1.6f) - (mc.fontRenderer.getStringWidth(d) * 0.5f),
                    (currentHeight.getFloat(i) - 7) * 1.6f, 0xffffff);
            String c = String.valueOf((5 - i) * 5);
            mc.fontRenderer.drawString(matrixStack, c, ((x + 20 * i) * 1.6f) - (mc.fontRenderer.getStringWidth(c) * 0.5f),
                    (y + height + 2) * 1.6f, 0xffffff);
        }
        matrixStack.pop();

        RenderSystem.disableBlend();

        updateHeight(partialTicks);
    }

    public void updateData(LongList newData) {
        this.data = newData;
        calculateUnitY(newData);
        calculateTargetHeight(newData);
    }

    private void updateHeight(float partialTick) {
        if (currentHeight.isEmpty()) {
            return;
        }
        for (int i = 0; i < currentHeight.size(); i++) {
            float diff = targetHeight.getFloat(i) - currentHeight.getFloat(i);
            if (diff == 0) {
                continue;
            }
            float r;
            float p = partialTick / 16;
            if (Math.abs(diff) <= p) {
                r = targetHeight.getFloat(i);
            } else {
                if (diff > 0)
                    r = currentHeight.getFloat(i) + Math.max(Math.min(diff, diff / 4 * partialTick), p);
                else
                    r = currentHeight.getFloat(i) + Math.min(Math.max(diff, diff / 4 * partialTick), -p);
            }
            currentHeight.set(i, r);
        }
    }

    private void calculateUnitY(@Nonnull List<Long> data) {
        long maxValue = 0;
        for (long v : data) {
            maxValue = Math.max(maxValue, v);
        }
        if (maxValue <= 0) {
            displayUnitY = "1";
            maxUnitY = 1;
            return;
        }
        long maxUnitY;
        int exp = (int) Math.log10(maxValue); // 0 = 10, 3 = 10000
        if (exp <= 0) {
            maxUnitY = maxValue + 1;
        } else if (exp <= 1) {
            maxUnitY = ((maxValue / 5) + 1) * 5;
        } else if (exp <= 2) {
            maxUnitY = ((maxValue / 50) + 1) * 50;
        } else {
            int unit = 10;
            for (int i = 1; i < exp; i++) {
                unit *= 10;
            }
            maxUnitY = ((maxValue / unit) + 1) * unit;
        }
        displayUnitY = FluxUtils.compact(maxUnitY);
        this.maxUnitY = maxUnitY;
    }

    private void calculateTargetHeight(@Nonnull List<Long> data) {
        if (data.size() != linePoints) {
            return;
        }
        int i = 0;
        for (Long value : data) {
            targetHeight.set(i, (float) (y + height * (1 - ((double) value / maxUnitY))));
            i++;
        }
    }
}
*/
