package icyllis.fluxnetworks.client.gui.button;

import icyllis.fluxnetworks.client.gui.basic.ITextBoxButton;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class TextboxButton extends TextFieldWidget {

    private String origin;
    private String extraText;
    private int textWidth;
    private boolean digitsOnly;
    private boolean hexOnly;
    private ITextBoxButton gui;

    private int outlineColor = 0xffb4b4b4;
    private static final int boxColor = 0x20000000;

    public TextboxButton(ITextBoxButton gui, String text, int componentId, FontRenderer fontRenderer, int x, int y, int par5Width, int par6Height, int width) {
        super(componentId, fontRenderer, x + width, y, par5Width - width, par6Height);
        this.extraText = text;
        this.fontRenderer = fontRenderer;
        this.textWidth = width;
        this.gui = gui;
    }

    public static TextboxButton create(ITextBoxButton gui, String text, int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
        return new TextboxButton(gui, text, id, fontRenderer, x, y, width, height, fontRenderer.getStringWidth(text));
    }

    public int getIntegerFromText(boolean nonNegative) {
        /*if(getText().isEmpty()) {
            return 0;
        }*/
        if(nonNegative) {
            return Math.max(Integer.parseInt(getText()), 0);
        }
        return Integer.parseInt(getText());
    }

    public long getLongFromText(boolean nonNegative) {
        /*if(getText().isEmpty()) {
            return 0;
        }*/
        if(nonNegative) {
            return Math.max(Long.parseLong(getText()), 0);
        }
        return Long.parseLong(getText());
    }

    public int getIntegerFromHex() {
        return Integer.parseInt(getText(), 16);
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

    /*@Override
    public boolean textboxKeyTyped(char c, int i) {
        if (digitsOnly) {
            switch (c) {
                case '\001':
                case '\003':
                case '\030':
                    return super.textboxKeyTyped(c, i);
                case '\026':
                    return false;
            }
            switch (i) {
                case 14:
                case 199:
                case 203:
                case 205:
                case 207:
                case 211:
                    return super.textboxKeyTyped(c, i);
            }
            return Character.isDigit(c) && super.textboxKeyTyped(c, i);
        }
        return super.textboxKeyTyped(c, i);
    }*/

    @Override
    public void writeText(String textToWrite) {
        if(digitsOnly) {
            for(int i = 0; i < textToWrite.length(); i++) {
                char c = textToWrite.charAt(i);
                if(!Character.isDigit(c)) {
                    if(getText().isEmpty()) {
                        if(c != '-') {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
        if(hexOnly) {
            for(int i = 0; i < textToWrite.length(); i++) {
                char c = textToWrite.charAt(i);
                if(c == '-') {
                    return;
                }
            }
            String origin = getText();
            super.writeText(textToWrite);
            try {
                Integer.parseInt(getText(), 16);
            } catch (final NumberFormatException ignored) {
                setText(origin);
            }
            return;
        }
        super.writeText(textToWrite);
    }

    /**
     * Better than Sonar Sonic's
     */
    @Override
    public void switchFocused(boolean isFocusedIn) {
        if(digitsOnly) {
            if(isFocusedIn) {
                origin = getText();
            } else {
                if(getText().isEmpty() || getText().equals("-")) {
                    setText("0");
                    return;
                }
                try {
                    Long.parseLong(getText());
                } catch (final NumberFormatException ignored) {
                    setText(origin);
                }
            }
        }
        if(!isFocusedIn) {
            gui.onTextBoxChanged(this);
        }
    }

    public TextboxButton setOutlineColor(int color) {
        this.outlineColor = color;
        return this;
    }

    public TextboxButton setTextInvisible() {
        isTextInvisible = true;
        return this;
    }

    public TextboxButton setDigitsOnly() {
        digitsOnly = true;
        return this;
    }

    public TextboxButton setHexOnly() {
        hexOnly = true;
        return this;
    }
}
