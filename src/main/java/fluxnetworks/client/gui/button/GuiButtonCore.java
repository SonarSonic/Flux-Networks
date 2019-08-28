package fluxnetworks.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiButtonCore extends Gui {

    public int x;
    public int y;

    public int width;
    public int height;

    public int id;

    protected String text;

    public GuiButtonCore(int x, int y, int width, int height, int id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {}

    protected int getHoverState(boolean b) {
        return b ? 1 : 0;
    }

    public boolean isMouseHovered(Minecraft mc, int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
