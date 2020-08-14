package sonar.fluxnetworks.client.jei;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CreatingFluxRecipeType {

    public final Block base;
    public final Block crusher;

    public final ItemStack input;
    public final ItemStack output;

    public CreatingFluxRecipeType(Block base, Block crusher, ItemStack input, ItemStack output) {
        this.base = base;
        this.crusher = crusher;
        this.input = input;
        this.output = output;
    }

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