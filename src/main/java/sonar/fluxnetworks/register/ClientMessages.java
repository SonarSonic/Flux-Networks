package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.ClientRepository;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.Registration.sNetwork;

/**
 * C2S message specs and S2C message handling.
 *
 * @see Messages
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class ClientMessages {

    public static void deviceBuffer(TileFluxDevice device, byte type) {
        assert type > 0; // C2S positive
        var buf = NetworkHandler.buffer(Messages.C2S_DEVICE_BUFFER);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(type);
        device.writePacket(buf, type);
        sNetwork.sendToServer(buf);
    }

    public static void superAdmin(boolean enable) {
        var buf = NetworkHandler.buffer(Messages.C2S_SUPER_ADMIN);
        buf.writeBoolean(enable);
        sNetwork.sendToServer(buf);
    }

    public static void editDevice(TileFluxDevice device, CompoundTag tag) {
        var buf = NetworkHandler.buffer(Messages.C2S_EDIT_DEVICE);
        buf.writeVarInt(FluxConstants.INVALID_NETWORK_ID);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeNbt(tag);
        sNetwork.sendToServer(buf);
    }

    public static void editDevice(int networkId, List<GlobalPos> list, CompoundTag tag) {
        if (list.isEmpty()) return;
        var buf = NetworkHandler.buffer(Messages.C2S_EDIT_DEVICE);
        buf.writeVarInt(networkId);
        buf.writeVarInt(list.size());
        for (var pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        buf.writeNbt(tag);
        sNetwork.sendToServer(buf);
    }

    public static void createNetwork(int token, String name, int color,
                                     SecurityLevel security, String password) {
        var buf = NetworkHandler.buffer(Messages.C2S_CREATE_NETWORK);
        buf.writeByte(token);
        buf.writeUtf(name);
        buf.writeInt(color);
        buf.writeByte(security.getKey());
        if (security.isEncrypted()) {
            buf.writeUtf(password);
        }
        sNetwork.sendToServer(buf);
    }

    // set (connect to) network for a flux tile entity
    public static void setTileNetwork(int token, TileFluxDevice device, int networkID, String password) {
        var buf = NetworkHandler.buffer(Messages.C2S_SET_TILE_NETWORK);
        buf.writeByte(token);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeVarInt(networkID);
        buf.writeUtf(password, 256);
        sNetwork.sendToServer(buf);
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<LocalPlayer> player) {
        Minecraft minecraft = Minecraft.getInstance();
        switch (index) {
            case Messages.S2C_DEVICE_BUFFER -> onDeviceBuffer(payload, player, minecraft);
            case Messages.S2C_RESPONSE -> onResponse(payload, player, minecraft);
            case Messages.S2C_SUPER_ADMIN -> onSuperAdmin(payload, player, minecraft);
            case Messages.S2C_NETWORK_UPDATE -> ClientRepository.onNetworkUpdate(payload); // TODO
        }
    }

    private static void onDeviceBuffer(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                       BlockableEventLoop<?> looper) {
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p != null && p.clientLevel.getBlockEntity(payload.readBlockPos()) instanceof TileFluxDevice e) {
                byte id = payload.readByte();
                if (id < 0) {
                    e.readPacket(payload, id);
                }
            }
            payload.release();
        });
        throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
    }

    private static void onResponse(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                   BlockableEventLoop<?> looper) {
        final int token = payload.readByte();
        final int key = payload.readShort();
        final int code = payload.readByte();
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            if (p.containerMenu.containerId == token &&
                    p.containerMenu instanceof FluxDeviceMenu m &&
                    m.mOnResultListener != null) {
                m.mOnResultListener.onResult(m, key, code);
            }
        });
    }

    private static void onSuperAdmin(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                     BlockableEventLoop<?> looper) {

    }
}
