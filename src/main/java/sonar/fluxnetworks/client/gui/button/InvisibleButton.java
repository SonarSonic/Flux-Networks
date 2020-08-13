package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;

public class InvisibleButton extends Button {

    public InvisibleButton(int widthIn, int heightIn, int width, int height, String text, IPressable onPress) {
        super(widthIn, heightIn, width, height, ITextComponent.func_244388_a(text), onPress);
    }

    @Override
    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {

    }
}
