package sonar.fluxnetworks.client.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public record CreatingFluxRecipeType(Block base,
                                     Block crusher,
                                     ItemStack input,
                                     ItemStack output) {

    public Block getBase() {
        return base;
    }

    public Block getCrusher() {
        return crusher;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}