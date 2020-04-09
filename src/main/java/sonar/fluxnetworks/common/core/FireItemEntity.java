package sonar.fluxnetworks.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.common.registry.RegistryItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class FireItemEntity extends ItemEntity {

    public boolean changed;

    public FireItemEntity(EntityType<FireItemEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    public FireItemEntity(ItemEntity toConvert) {
        this(RegistryItems.FIRE_ITEM_ENTITY, toConvert.getEntityWorld());
        CompoundNBT copyTag = new CompoundNBT();
        read(toConvert.writeWithoutTypeId(copyTag));
    }
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source == DamageSource.IN_FIRE) {
            if (!changed) {
                ItemStack stack = getItem().copy();
                ItemStack newStack = ItemStack.EMPTY;
                if (stack.getItem() == Items.REDSTONE) {
                    newStack = new ItemStack(RegistryItems.FLUX, stack.getCount());
                }
                setItem(newStack);
                changed = true;
            }
            return true;
        } else {
            return super.isInvulnerableTo(source);
        }
    }

    public boolean isBurning() {
        return false;
    }
}
