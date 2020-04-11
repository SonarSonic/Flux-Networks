package sonar.fluxnetworks.client.jei;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public class CreatingFluxRecipeType {

    public final BlockState base;
    public final BlockState crusher;
    public final ItemStack input;
    public final ItemStack output;

    public CreatingFluxRecipeType(BlockState base, BlockState crusher, ItemStack input, ItemStack output) {
        this.base = base;
        this.crusher = crusher;
        this.input = input;
        this.output = output;
    }
    public BlockState getBase() {
        return base;
    }

    public BlockState getCrusher() {
        return crusher;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }


}