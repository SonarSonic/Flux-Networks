package sonar.flux;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import sonar.core.common.block.properties.IMetaRenderer;

public class BlockRenderRegister {
	public static void register() {
		/* registerBlock(Calculator.reinforcedStoneBlock); registerBlock(Calculator.reinforcedStoneStairs); registerBlock(Calculator.reinforcedStoneBrick); registerBlock(Calculator.reinforcedStoneBrickStairs); registerBlock(Calculator.reinforcedDirtBlock); registerBlock(Calculator.reinforcedDirtStairs); registerBlock(Calculator.reinforcedDirtBrick); registerBlock(Calculator.reinforcedDirtBrickStairs); registerBlock(Calculator.stableStone); registerBlock(Calculator.powerCube); registerBlock(Calculator.advancedPowerCube); registerBlock(Calculator.atomicCalculator); registerBlock(Calculator.dynamicCalculator); registerBlock(Calculator.reinforcedFurnace); registerBlock(Calculator.stoneSeparator); registerBlock(Calculator.algorithmSeparator); registerBlock(Calculator.hungerProcessor); registerBlock(Calculator.healthProcessor); registerBlock(Calculator.basicGreenhouse); registerBlock(Calculator.advancedGreenhouse); registerBlock(Calculator.flawlessGreenhouse); registerBlock(Calculator.CO2Generator); */
		for (Block block : FluxNetworks.registeredBlocks) {
			Item item = Item.getItemFromBlock(block);
			if (item.getHasSubtypes()) {
				NonNullList<ItemStack> stacks = NonNullList.create();
                item.getSubItems(item, FluxNetworks.tab, stacks);
				for (ItemStack stack : stacks) {
					String variant = "variant=meta" + stack.getItemDamage();
					if (block instanceof IMetaRenderer) {
						IMetaRenderer meta = (IMetaRenderer) block;
						variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
					}
                    ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(FluxNetworks.modid + ':' + item.getUnlocalizedName().substring(5), variant));
				}
			} else {
				registerBlock(block);
			}
		}
	}

	public static void registerBlock(Block block) {
		if (block != null) {
			Item item = Item.getItemFromBlock(block);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(FluxNetworks.modid + ':' + item.getUnlocalizedName().substring(5), "inventory"));
		}
	}
}
