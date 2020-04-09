package sonar.fluxnetworks.register;

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.capabilities.DefaultSuperAdmin;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.core.FireItemEntity;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.event.FluxConnectionEvent;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.network.NetworkUpdatePacket;
import sonar.fluxnetworks.common.network.SuperAdminPacket;
import sonar.fluxnetworks.common.registry.RegistryItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyServer implements IProxy {

    public boolean baublesLoaded;
    public boolean ocLoaded;

    public int admin_viewing_network_id = -1;
    public IFluxNetwork admin_viewing_network = FluxNetworkInvalid.instance;

    public ProxyServer(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run this on the client");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run this on the client");
    }


    public EnumFeedbackInfo getFeedback(boolean operation) {
        return null;
    }

    public void setFeedback(EnumFeedbackInfo info, boolean operation) {}

    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {}

    @Override
    public int getAdminViewingNetworkID(){
        return admin_viewing_network_id;
    }

    @Override
    public IFluxNetwork getAdminViewingNetwork(){
        return admin_viewing_network;
    }

    @Override
    public IFluxNetwork getNetwork(int networkID) {
        return FluxNetworkCache.instance.getNetwork(networkID);
    }
}
