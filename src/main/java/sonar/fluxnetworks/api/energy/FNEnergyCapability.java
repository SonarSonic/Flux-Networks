package sonar.fluxnetworks.api.energy;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.energy.CapabilityEnergy;

/**only make use of this capability if your mod can send/receive energy at a rate greater than Integer.MAX_VALUE
 * Flux Networks will handle all Forge Energy implementations as normal.
 *
 * functions the same as {@link CapabilityEnergy} but allows Long.MAX_VALUE
 * you can add this cap to Items or Tile Entities, the Flux Plug & Point also use this capability **/
public class FNEnergyCapability {

    @CapabilityInject(IFNEnergyStorage.class)
    public static Capability<IFNEnergyStorage> FN_ENERGY_STORAGE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IFNEnergyStorage.class, new Capability.IStorage<IFNEnergyStorage>(){

            @Override
            public INBT writeNBT(Capability<IFNEnergyStorage> capability, IFNEnergyStorage instance, Direction side){
                return LongNBT.valueOf(instance.getEnergyStoredL());
            }

            @Override
            public void readNBT(Capability<IFNEnergyStorage> capability, IFNEnergyStorage instance, Direction side, INBT nbt){
                if (!(instance instanceof FNEnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((FNEnergyStorage)instance).energy = ((LongNBT)nbt).getLong();
            }
        },
        () -> new FNEnergyStorage(10000));
    }

}
