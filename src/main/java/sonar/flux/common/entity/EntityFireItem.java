package sonar.flux.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import sonar.core.utils.SonarCompat;
import sonar.flux.FluxNetworks;

public class EntityFireItem extends EntityItem {
    public boolean changed;
    public int changeTicks;

	public EntityFireItem(World world) {
		super(world);
		isImmuneToFire = true;
	}

	public EntityFireItem(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
		isImmuneToFire = true;
	}

	public boolean isEntityInvulnerable(DamageSource source) {
		if (source == DamageSource.inFire) {
			if (!changed) {
				//if (changeTicks >= 30) {
                ItemStack stack = getEntityItem().copy();
				ItemStack newStack = SonarCompat.getEmpty();
				if (stack.getItem() == Items.REDSTONE) {
					newStack = new ItemStack(FluxNetworks.flux, SonarCompat.getCount(stack), 0);
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
