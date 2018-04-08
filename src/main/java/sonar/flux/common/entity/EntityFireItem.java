package sonar.flux.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import sonar.flux.FluxNetworks;

public class EntityFireItem extends EntityItem {
    public boolean changed;
    public int changeTicks;

	public EntityFireItem(World world) {
		super(world);
		isImmuneToFire = true;
	}

	public EntityFireItem(EntityItem toConvert) {
		this(toConvert.getEntityWorld());
		NBTTagCompound copyTag = new NBTTagCompound();
		readFromNBT(toConvert.writeToNBT(copyTag));
	}

	public boolean isEntityInvulnerable(DamageSource source) {
		if (source == DamageSource.IN_FIRE) {
			if (!changed) {
				//if (changeTicks >= 30) {
                ItemStack stack = getItem().copy();
				ItemStack newStack = ItemStack.EMPTY;
				if (stack.getItem() == Items.REDSTONE) {
					newStack = new ItemStack(FluxNetworks.flux, stack.getCount(), 0);
				}
                setItem(newStack);
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
