package sonar.fluxnetworks.client.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public record CreatingFluxRecipe(Block base, Block crusher, ItemStack input, ItemStack output) {
}
