package fluxnetworks.common.integration;

import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.ConnectionType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.block.BlockFluxCore;
import fluxnetworks.common.core.FluxUtils;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Function;

public class OPIntegration {

    public static void preInit() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "hello");
    }

    private static boolean registered;

    public static void register() {
        if (registered)
            return;
        registered = true;
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "fluxnetworks.common.integration.OPIntegration$GetTheOneProbe");
    }

    public static class FluxConnectorInfoProvider implements IProbeInfoProvider{

        protected String id = new ResourceLocation(FluxNetworks.MODID, "flux_storage_energy").toString();

        @Override
        public String getID() {
            return id;
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
            if(!(FluxConfig.enableOneProbeBasicInfo || FluxConfig.enableOneProbeAdvancedInfo)){
                return;
            }
            if(iBlockState.getBlock() instanceof BlockFluxCore){
                TileEntity tile = world.getTileEntity(iProbeHitData.getPos());
                if(tile instanceof IFluxConnector){
                    IFluxConnector flux = (IFluxConnector)tile;
                    if(FluxConfig.enableOneProbeBasicInfo) {
                        iProbeInfo.text(TextFormatting.AQUA + (flux.getNetwork().isInvalid() ? FluxTranslate.ERROR_NO_SELECTED.t() : flux.getNetwork().getNetworkName()));
                        iProbeInfo.text(FluxUtils.getTransferInfo(flux.getConnectionType(), EnergyType.RF, flux.getChange()));
                        if (flux.getConnectionType() != ConnectionType.STORAGE) {
                            iProbeInfo.text(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.GREEN + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
                        }
                    }
                    if(FluxConfig.enableOneProbeAdvancedInfo && (!FluxConfig.enableOneProbeSneaking || entityPlayer.isSneaking())) {
                        iProbeInfo.text(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ? FluxTranslate.UNLIMITED.t() : flux.getCurrentLimit()));
                        iProbeInfo.text(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ? FluxTranslate.SURGE.t() : flux.getActualPriority()));
                        if (flux.isForcedLoading()) {
                            iProbeInfo.text(TextFormatting.YELLOW + FluxTranslate.FORCED_LOADING.t());
                        }
                    }
                }
            }
        }
    }
    public static class FluxConnectorDisplayOverride implements IBlockDisplayOverride{

        @Override
        public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
            if(iBlockState.getBlock() instanceof BlockFluxCore){
                TileEntity tile = world.getTileEntity(iProbeHitData.getPos());
                if(tile instanceof IFluxConnector) {
                    IFluxConnector flux = (IFluxConnector) tile;
                    ItemStack pickBlock = flux.getDisplayStack().setStackDisplayName(flux.getCustomName());
                    iProbeInfo.horizontal().item(pickBlock).vertical().itemLabel(pickBlock).text(TextStyleClass.MODNAME + FluxNetworks.NAME);
                    return true;
                }
            }
            return false;
        }
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        public static ITheOneProbe probe;

        @Nullable
        @Override
        public Void apply(ITheOneProbe theOneProbe) {
            probe = theOneProbe;
            probe.registerProvider(new FluxConnectorInfoProvider());
            probe.registerBlockDisplayOverride(new FluxConnectorDisplayOverride());
            return null;
        }
    }
}
