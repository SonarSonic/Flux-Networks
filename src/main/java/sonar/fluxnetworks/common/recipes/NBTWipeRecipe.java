package sonar.fluxnetworks.common.recipes;

import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.core.FluxUtils;

/**save Flux Storage energy when wiping NBT*/
public class NBTWipeRecipe extends ShapelessRecipe {

    public NBTWipeRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
    }

    public NBTWipeRecipe(ShapelessRecipe recipe){
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeOutput(), recipe.getIngredients());
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory) {
        ItemStack originalStack = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty()){
                originalStack = stack;
                break;
            }
        }
        if(originalStack != null) {
            ItemStack output = getRecipeOutput().copy();
            if (Block.getBlockFromItem(output.getItem()) instanceof FluxStorageBlock) {
                CompoundNBT fluxData = originalStack.getChildTag(FluxUtils.FLUX_DATA);
                int energy = 0;
                if (fluxData != null) {
                    energy = fluxData.getInt("energy");
                }
                if(energy != 0){
                    CompoundNBT newTag = output.getOrCreateChildTag(FluxUtils.FLUX_DATA);
                    newTag.putInt("energy", energy);
                }
            }
            return output;
        }
        return super.getCraftingResult(inventory);
    }

    public boolean matches(CraftingInventory inv, World worldIn) {
        return super.matches(inv, worldIn);
    }

    public IRecipeSerializer<?> getSerializer(){
        return NBTWipeRecipeSerializer.INSTANCE;
    }

}
