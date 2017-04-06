package sonar.flux.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import sonar.flux.FluxNetworks;

public class EntityFireItem extends EntityItem {
	public boolean changed = false;
	public int changeTicks = 0;

	public EntityFireItem(World world) {
		super(world);
		isImmuneToFire = true;
	}

	public EntityFireItem(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
		isImmuneToFire = true;
	}

	public boolean isEntityInvulnerable(DamageSource source) {
		if (source == DamageSource.IN_FIRE) {
			if (!changed) {
				//if (changeTicks >= 30) {
				ItemStack stack = getEntityItem().copy();
				ItemStack newStack = ItemStack.EMPTY;
				if (stack.getItem() == Items.REDSTONE) {
					newStack = new ItemStack(FluxNetworks.flux, stack.getCount(), 0);
				} else if (stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) {
					newStack = new ItemStack(FluxNetworks.fluxBlock, stack.getCount(), 0);
				} else if (stack.getItem() == Items.ENDER_EYE) {					
					newStack = new ItemStack(FluxNetworks.fluxCore, stack.getCount(), 0);
				}
				setEntityItemStack(newStack);
				changed = true;
				//	changeTicks = 0;
				//} else {
				//	changeTicks++;
				//}
			}
			return true;
		} else {
			return super.isEntityInvulnerable(source);
		}
	}

	public boolean isBurning() {
		return false;
	}

}
