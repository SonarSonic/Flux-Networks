package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;

public class FluxEditBox extends EditBox {

    private final Font mFont;
    private final String mHeader;
    private final int mHeaderWidth;

    private String mOrigin;
    private boolean mHexOnly;

    ///digits
    private boolean mDigitsOnly;
    private long mMaxValue = Integer.MAX_VALUE;
    private boolean mAllowNegatives = false;

    private int mOutlineColor = 0xffb4b4b4;

    private FluxEditBox(String header, Font font, int x, int y, int totalWidth, int height, int headerWidth) {
        super(font, x + headerWidth, y, totalWidth - headerWidth, height, CommonComponents.EMPTY);
        mHeader = header;
        mHeaderWidth = headerWidth;
        mFont = font;
    }

    @Nonnull
    public static FluxEditBox create(String header, Font font, int x, int y, int width, int height) {
        return new FluxEditBox(header, font, x, y, width, height, font.width(header));
    }

    public int getIntegerFromText(boolean allowNegatives) {
        if (getValue().isEmpty() || getValue().equals("-")) {
            return 0;
        }
        int parseInt = Integer.parseInt(getValue());
        return allowNegatives ? parseInt : Math.max(parseInt, 0);
    }

    public long getLongFromText(boolean allowNegatives) {
        if (getValue().isEmpty() || getValue().equals("-")) {
            return 0;
        }
        long parseLong = Long.parseLong(getValue());
        return allowNegatives ? parseLong : Math.max(parseLong, 0);
    }

    public int getIntegerFromHex() {
        return Integer.parseInt(getValue(), 16);
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        if (isVisible()) {
            gr.fill(getX() - mHeaderWidth, getY(), getX() + width, getY() + height, 0x30000000);
            gr.fill(getX() - mHeaderWidth - 1, getY() - 1, getX() + width + 1, getY(), mOutlineColor);
            gr.fill(getX() - mHeaderWidth - 1, getY() + height, getX() + width + 1, getY() + height + 1, mOutlineColor);
            gr.fill(getX() - mHeaderWidth - 1, getY(), getX() - mHeaderWidth, getY() + height, mOutlineColor);
            gr.fill(getX() + width, getY(), getX() + width + 1, getY() + height, mOutlineColor);
        }

        gr.pose().pushPose();
        int dy = (height - 8) / 2;
        gr.pose().translate(4, dy, 0);

        setBordered(false);
        super.renderWidget(gr, mouseX, mouseY, deltaTicks);

        gr.drawString(mFont, mHeader, getX() - mHeaderWidth, getY(), mOutlineColor);
        gr.pose().popPose();
    }

    @Override
    public void insertText(@Nonnull String textToWrite) {
        if (mDigitsOnly) {
            for (int i = 0; i < textToWrite.length(); i++) {
                char c = textToWrite.charAt(i);
                if (!Character.isDigit(c)) {
                    if (getValue().isEmpty()) {
                        if (c != '-') {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
        if (mHexOnly) {
            for (int i = 0; i < textToWrite.length(); i++) {
                char c = textToWrite.charAt(i);
                if (c == '-') {
                    return;
                }
            }
            String origin = getValue();
            super.insertText(textToWrite);
            try {
                Integer.parseInt(getValue(), 16);
            } catch (final NumberFormatException ignored) {
                setValue(origin);
            }
            return;
        }
        super.insertText(textToWrite);
    }

    @Override
    public void setFocused(boolean isFocused) {
        if (isFocused) {
            if (mDigitsOnly) {
                mOrigin = getValue();
            }
        } else if (isFocused()) {
            if (mDigitsOnly) {
                try {
                    setValue(String.valueOf(getValidLong()));
                } catch (final NumberFormatException ignored) {
                    setValue(mOrigin);
                    //System.out.println(ignored.getMessage());
                }
            }
        }
        super.setFocused(isFocused);
    }

    public long getValidLong() {
        return Math.min(getLongFromText(mAllowNegatives), mMaxValue);
    }

    public int getValidInt() {
        return (int) Math.min(getValidLong(), Integer.MAX_VALUE);
    }

    // ARGB
    public FluxEditBox setOutlineColor(int color) {
        mOutlineColor = color;
        return this;
    }

    public int getOutlineColor() {
        return mOutlineColor;
    }

    public FluxEditBox setTextInvisible() {
        setFormatter(FluxEditBox::getInvisibleText);
        return this;
    }

    @Nonnull
    public static FormattedCharSequence getInvisibleText(String string, int cursorPos) {
        return FormattedCharSequence.forward("\u2022".repeat(string.length()), Style.EMPTY);
    }

    public FluxEditBox setDigitsOnly() {
        mDigitsOnly = true;
        return this;
    }

    public FluxEditBox setAllowNegatives(boolean allowNegatives) {
        mAllowNegatives = allowNegatives;
        return this;
    }

    public FluxEditBox setMaxValue(long max) {
        mMaxValue = max;
        return this;
    }

    public FluxEditBox setHexOnly() {
        mHexOnly = true;
        return this;
    }
}
