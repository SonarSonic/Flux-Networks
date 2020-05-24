package sonar.fluxnetworks.common.core;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;

public class FireItemEntity extends ItemEntity {

    public boolean changed;

    public FireItemEntity(EntityType<FireItemEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    public FireItemEntity(@Nonnull ItemEntity toConvert) {
        this(RegistryItems.FIRE_ITEM_ENTITY, toConvert.getEntityWorld());
        setItem(toConvert.getItem());
        copyDataFromOld(toConvert);
    }

    @Override
    protected void dealFireDamage(int amount) {
        if (!changed) {
            ItemStack stack = getItem().copy();
            ItemStack newStack = ItemStack.EMPTY;
            if (stack.getItem() == Items.REDSTONE) {
                newStack = new ItemStack(RegistryItems.FLUX, stack.getCount());
            }
            setItem(newStack);
            changed = true;
        }
    }
}
