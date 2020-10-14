package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import sonar.fluxnetworks.common.misc.FluxUtils;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Line chart that using OpenGL.
 *
 * @author BloCamLimb
 */
public class LineChart {

    public int x, y;
    public int height;

    public int linePoints;

    public String displayUnitX;
    public String displayUnitY;

    public long maxUnitY;
    public String suffixUnitY;

    public LongList data = new LongArrayList();

    public DoubleList currentHeight;
    public DoubleList targetHeight;

    public LineChart(int x, int y, int height, int linePoints, String displayUnitX, String suffixUnitY) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.linePoints = linePoints;
        this.displayUnitX = displayUnitX;
        this.suffixUnitY = suffixUnitY;

        this.currentHeight = new DoubleArrayList(linePoints);
        for (int i = 0; i < linePoints; i++) {
            currentHeight.add(y + height);
        }
        this.targetHeight = new DoubleArrayList(linePoints);
        for (int i = 0; i < linePoints; i++) {
            targetHeight.add(y + height);
        }
    }

    public void drawChart(Minecraft mc, MatrixStack matrixStack) {
        RenderSystem.pushLightingAttributes();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glBegin(GL_LINE_STRIP);
        for (int i = 0; i < currentHeight.size(); i++) {
            glVertex3d(x + 20 * i, currentHeight.get(i), 1);
        }
        glEnd();

        RenderSystem.blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, 1, 0);

        glEnable(GL_POINT_SMOOTH);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glPointSize(8.0f);
        glBegin(GL_POINTS);
        for (int i = 0; i < currentHeight.size(); i++) {
            glVertex3d(x + 20 * i, currentHeight.get(i), 1);
        }
        glEnd();
        glDisable(GL_POINT_SMOOTH);
        glDisable(GL_BLEND);

        Screen.fill(matrixStack, x - 16, y + height, x + 116, y + height + 1, 0xffffffff);
        Screen.fill(matrixStack, x - 14, y - 6, x - 13, y + height + 3, 0xffffffff);

        RenderSystem.scaled(0.625f, 0.625f, 0.625f);
        mc.fontRenderer.drawString(matrixStack, suffixUnitY, (float) ((x - 15) * 1.6) - mc.fontRenderer.getStringWidth(suffixUnitY), (float) ((y - 7.5) * 1.6), 0xffffff);
        mc.fontRenderer.drawString(matrixStack, displayUnitY, (float) ((x - 15) * 1.6) - mc.fontRenderer.getStringWidth(displayUnitY), (float) ((y - 2) * 1.6), 0xffffff);
        mc.fontRenderer.drawString(matrixStack, displayUnitX, (float) (((x + 118) * 1.6) - mc.fontRenderer.getStringWidth(displayUnitX)), (float) ((y + height + 1.5) * 1.6), 0xffffff);
        for (int i = 0; i < data.size(); i++) {
            String d = FluxUtils.format(data.getLong(i), FluxUtils.TypeNumberFormat.COMPACT, "");
            mc.fontRenderer.drawString(matrixStack, d, ((x + 20 * i) * 1.6F) - (mc.fontRenderer.getStringWidth(d) / 2F) + 1.0f, (float) ((currentHeight.getDouble(i) - 7) * 1.6), 0xffffff);
            String c = String.valueOf((5 - i) * 5);
            mc.fontRenderer.drawString(matrixStack, c, ((x + 20 * i) * 1.6F) - (mc.fontRenderer.getStringWidth(c) / 2F), (float) ((y + height + 2) * 1.6), 0xffffff);
        }
        RenderSystem.scaled(1.6f, 1.6f, 1.6f);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popAttributes();
    }

    public void updateData(LongList newData) {
        this.data = newData;
        calculateUnitY(newData);
        calculateTargetHeight(newData);
    }

    public void updateHeight(float partialTick) {
        if (currentHeight.size() == 0) {
            return;
        }
        for (int i = 0; i < currentHeight.size(); i++) {
            double a = targetHeight.getDouble(i) - currentHeight.getDouble(i);
            if (a == 0) {
                continue;
            }
            double c;
            double p = partialTick / 16;
            if (Math.abs(a) <= p) {
                c = targetHeight.getDouble(i);
            } else {
                if (a > 0)
                    c = currentHeight.getDouble(i) + Math.max(Math.min(a, a / 4 * partialTick), p);
                else
                    c = currentHeight.getDouble(i) + Math.min(Math.max(a, a / 4 * partialTick), -p);
            }
            currentHeight.set(i, c);
        }
    }

    private void calculateUnitY(List<Long> data) {
        long maxValue = 0;
        for (long v : data) {
            maxValue = Math.max(maxValue, v);
        }
        if (maxValue == 0) {
            maxUnitY = 1;
            displayUnitY = FluxUtils.format(maxUnitY, FluxUtils.TypeNumberFormat.COMPACT, "");
            return;
        }
        int measureLevel = (int) Math.log10(maxValue); // 0 = 10, 3 = 10000
        switch (measureLevel) {
            case 0:
                maxUnitY = maxValue + 1;
                break;
            case 1:
                maxUnitY = ((maxValue / 5) + 1) * 5;
                break;
            case 2:
                maxUnitY = ((maxValue / 50) + 1) * 50;
                break;
            default:
                int p = (int) Math.pow(10, measureLevel - 1);
                maxUnitY = ((maxValue / p) + 1) * p;
                break;
        }
        displayUnitY = FluxUtils.format(maxUnitY, FluxUtils.TypeNumberFormat.COMPACT, "");
    }

    private void calculateTargetHeight(List<Long> data) {
        if (data.size() != linePoints) {
            return;
        }
        int i = 0;
        for (Long value : data) {
            targetHeight.set(i, y + height * (1 - ((double) value / maxUnitY)));
            i++;
        }
    }
}
