package sonar.fluxnetworks.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

///refers to anything which can be connected to a specific network, flux tiles & configurators
public interface INetworkConnector {

    int getNetworkID();

    IFluxNetwork getNetwork();

    void open(PlayerEntity player);

    void close(PlayerEntity player);
}
