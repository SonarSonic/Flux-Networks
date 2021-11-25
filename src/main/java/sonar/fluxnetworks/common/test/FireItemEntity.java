package sonar.fluxnetworks.common.test;

/*import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.World;

@Deprecated
public class FireItemEntity extends ItemEntity {

    public boolean changed;

    public FireItemEntity(EntityType<FireItemEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    *//*public FireItemEntity(@Nonnull ItemEntity toConvert) {
        this(RegistryItems.FIRE_ITEM_ENTITY, toConvert.getEntityWorld());
        setItem(toConvert.getItem());
        copyDataFromOld(toConvert);
    }*//*

    *//*@Override
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
    }*//*
}*/
