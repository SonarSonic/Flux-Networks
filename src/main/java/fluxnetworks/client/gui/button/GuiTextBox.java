package fluxnetworks.client.gui.button;

import net.minecraft.client.gui.FontRenderer;

public class GuiTextBox extends GuiTextField {

    public String extraText;
    public int textWidth;
    protected boolean digitsOnly;

    protected int outlineColor = 0xffb4b4b4, boxColor = 0x20000000;

    public GuiTextBox(String text, int componentId, FontRenderer fontRenderer, int x, int y, int par5Width, int par6Height, int width) {
        super(componentId, fontRenderer, x + width, y, par5Width - width, par6Height);
        this.extraText = text;
        this.fontRenderer = fontRenderer;
        textWidth = width;
    }

    public static GuiTextBox create(String text, int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
        return new GuiTextBox(text, id, fontRenderer, x, y, width, height, fontRenderer.getStringWidth(text));
    }

    public int getIntegerFromText() {
        return Integer.valueOf(getText().isEmpty() ? "0" : getText());
    }

    public long getLongFromText() {
        return Long.valueOf(getText().isEmpty() ? "0" : getText());
    }

    @Override
    public void drawTextBox() {
        this.setEnableBackgroundDrawing(true);
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(this.x - textWidth - 1, this.y - 1, this.x + this.width + 1, this.y, outlineColor);
                drawRect(this.x - textWidth - 1, this.y + this.height, this.x + this.width + 1, this.y + this.height + 1, outlineColor);
                drawRect(this.x - textWidth - 1, this.y, this.x - textWidth, this.y + this.height, outlineColor);
                drawRect(this.x + width, this.y, this.x + this.width + 1, this.y + this.height, outlineColor);
                drawRect(this.x - textWidth, this.y, this.x + this.width, this.y + this.height, boxColor);
            }
        }
        this.setEnableBackgroundDrawing(false);
        x += 4;
        y += (this.height - 8) / 2;
        super.drawTextBox();
        fontRenderer.drawString(extraText, x - textWidth, y, outlineColor);
        x -= 4;
        y -= (this.height - 8) / 2;
    }

    @Override
    public boolean textboxKeyTyped(char c, int i) {
        if (digitsOnly) {
            switch (c) {
                case '\001':
                    return super.textboxKeyTyped(c, i);
                case '\003':
                    return super.textboxKeyTyped(c, i);
                case '\026':
                    return false;
                case '\030':
                    return super.textboxKeyTyped(c, i);
            }
            switch (i) {
                case 14:
                    return super.textboxKeyTyped(c, i);
                case 199:
                    return super.textboxKeyTyped(c, i);
                case 203:
                    return super.textboxKeyTyped(c, i);
                case 205:
                    return super.textboxKeyTyped(c, i);
                case 207:
                    return super.textboxKeyTyped(c, i);
                case 211:
                    return super.textboxKeyTyped(c, i);
            }
            return Character.isDigit(c) && super.textboxKeyTyped(c, i);
        }
        return super.textboxKeyTyped(c, i);
    }

    public GuiTextBox setOutlineColor(int color) {
        this.outlineColor = color;
        return this;
    }

    public GuiTextBox setTextInvisible() {
        isTextInvisible = true;
        return this;
    }

    public GuiTextBox setDigitalOnly() {
        digitsOnly = true;
        return this;
    }

}
