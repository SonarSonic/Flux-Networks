package sonar.fluxnetworks.common.integration;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import sonar.fluxnetworks.FluxNetworks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;

/**
 * A holder class that prevents class-loading when Curios not available.
 *
 * @see FluxNetworks#isCuriosLoaded()
 */
public class CuriosIntegration {

    // NOTE: the return value will be no longer valid when the player dies (isAlive() == false)
    @Nonnull
    public static Iterable<ItemStack> getFlatStacks(ServerPlayer player) {
        final LazyOptional<ICuriosItemHandler> curios = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        if (curios.isPresent()) {
            // we can cache the ICuriosItemHandler atm, but cannot for getEquippedCurios()
            final ICuriosItemHandler peek = curios.orElseThrow(RuntimeException::new);
            return () -> new FlatIterator(peek);
        }
        return Collections.emptyList();
    }

    private static class FlatIterator implements Iterator<ItemStack> {

        private final Iterator<ICurioStacksHandler> mIterator;

        private IItemHandler mHandler;
        private int mIndex;

        FlatIterator(ICuriosItemHandler curios) {
            mIterator = curios.getCurios().values().iterator();
        }

        @Override
        public boolean hasNext() {
            forward();
            return mIndex < mHandler.getSlots();
        }

        @Override
        public ItemStack next() {
            forward();
            return mHandler.getStackInSlot(mIndex++);
        }

        private void forward() {
            while ((mHandler == null || mIndex == mHandler.getSlots()) && mIterator.hasNext()) {
                mHandler = mIterator.next().getStacks();
                mIndex = 0;
            }
        }
    }
}
