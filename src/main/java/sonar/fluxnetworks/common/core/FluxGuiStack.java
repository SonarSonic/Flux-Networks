package sonar.fluxnetworks.common.core;

import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;

public class FluxGuiStack {

    public static ItemStack FLUX_PLUG;
    public static ItemStack FLUX_POINT;
    public static ItemStack FLUX_CONTROLLER;

    static {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("GuiColor", true);
        ItemStack stack1 = new ItemStack(RegistryBlocks.FLUX_PLUG);
        ItemStack stack2 = new ItemStack(RegistryBlocks.FLUX_POINT);
        ItemStack stack3 = new ItemStack(RegistryBlocks.FLUX_CONTROLLER);
        stack1.setTag(tag);
        stack2.setTag(tag);
        stack3.setTag(tag);
        FLUX_PLUG = stack1;
        FLUX_POINT = stack2;
        FLUX_CONTROLLER = stack3;
    }
}
