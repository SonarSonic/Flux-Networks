package sonar.fluxnetworks.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A class holding the capability instance of each player.
 */
public class FluxPlayerProvider implements ICapabilitySerializable<CompoundTag>/*, Runnable*/ {

    public static final ResourceLocation CAP_KEY = FluxNetworks.location("flux_player");

    @Nonnull
    private final FluxPlayer mCap;

    private final LazyOptional<FluxPlayer> mHandler;

    public FluxPlayerProvider() {
        mCap = new FluxPlayer();
        mHandler = LazyOptional.of(() -> mCap);
    }

    /*@Override
    public void run() {
        mHandler.invalidate();
    }*/

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return FluxPlayer.FLUX_PLAYER.orEmpty(cap, mHandler);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        mCap.writeNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        mCap.readNBT(nbt);
    }
}
