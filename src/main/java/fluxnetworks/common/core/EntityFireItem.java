package fluxnetworks.common.core;

import fluxnetworks.common.registry.RegistryItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityFireItem extends EntityItem {

    public boolean changed;

    public EntityFireItem(World world) {
        super(world);
        isImmuneToFire = true;
    }

    public EntityFireItem(EntityItem toConvert) {
        this(toConvert.getEntityWorld());
        NBTTagCompound copyTag = new NBTTagCompound();
        readFromNBT(toConvert.writeToNBT(copyTag));
    }

    public boolean isEntityInvulnerable(@Nonnull DamageSource source) {
        if (source == DamageSource.IN_FIRE) {
            if (!changed) {
                ItemStack stack = getItem().copy();
                ItemStack newStack = ItemStack.EMPTY;
                if (stack.getItem() == Items.REDSTONE) {
                    newStack = new ItemStack(RegistryItems.FLUX, stack.getCount(), 0);
                }
                setItem(newStack);
                changed = true;
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
