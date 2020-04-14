package sonar.fluxnetworks.common.integration;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.common.block.FluxNetworkBlock;
import sonar.fluxnetworks.common.core.FluxUtils;
import mcjty.theoneprobe.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.text.NumberFormat;
import java.util.function.Function;

public class TOPIntegration implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(new FluxConnectorInfoProvider());
        iTheOneProbe.registerBlockDisplayOverride(new FluxConnectorDisplayOverride());
        return null;
    }

    public static class FluxConnectorInfoProvider implements IProbeInfoProvider {

        @Override
        public String getID() {
            return FluxNetworks.MODID;
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
            if(!(FluxConfig.enableOneProbeBasicInfo || FluxConfig.enableOneProbeAdvancedInfo)) {
                return;
            }
            if(blockState.getBlock() instanceof FluxNetworkBlock) {
                TileEntity tile = world.getTileEntity(iProbeHitData.getPos());
                if(tile instanceof IFluxConnector) {
                    IFluxConnector flux = (IFluxConnector) tile;
                    if(FluxConfig.enableOneProbeBasicInfo) {
                        iProbeInfo.text(TextFormatting.AQUA + (flux.getNetwork().isInvalid() ? FluxTranslate.ERROR_NO_SELECTED.t() : flux.getNetwork().getNetworkName()));
                        iProbeInfo.text(FluxUtils.getTransferInfo(flux.getConnectionType(), EnergyType.FE, flux.getChange()));
                        if(playerEntity.isShiftKeyDown()) {
                            if (flux.getConnectionType().isStorage()) {
                                iProbeInfo.text(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.GREEN + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
                            } else {
                                iProbeInfo.text(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.GREEN + NumberFormat.getInstance().format(flux.getBuffer()) + "RF");
                            }
                        } else {
                            if (flux.getConnectionType().isStorage()) {
                                iProbeInfo.text(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.GREEN + FluxUtils.format(flux.getBuffer(), FluxUtils.TypeNumberFormat.COMPACT, "RF"));
                            } else {
                                iProbeInfo.text(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.GREEN + FluxUtils.format(flux.getBuffer(), FluxUtils.TypeNumberFormat.COMPACT, "RF"));
                            }
                        }
                    }
                    if(FluxConfig.enableOneProbeAdvancedInfo && (!FluxConfig.enableOneProbeSneaking || playerEntity.isShiftKeyDown())) {
                        iProbeInfo.text(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.GREEN + (flux.getDisableLimit() ? FluxTranslate.UNLIMITED.t() : flux.getActualLimit()));
                        iProbeInfo.text(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.GREEN + (flux.getSurgeMode() ? FluxTranslate.SURGE.t() : flux.getActualPriority()));
                        if (flux.isForcedLoading()) {
                            iProbeInfo.text(TextFormatting.GOLD + FluxTranslate.FORCED_LOADING.t());
                        }
                    }
                }
            }
        }
    }

    public static class FluxConnectorDisplayOverride implements IBlockDisplayOverride {

        @Override
        public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
            if(blockState.getBlock() instanceof FluxNetworkBlock) {
                TileEntity tile = world.getTileEntity(iProbeHitData.getPos());
                if(tile instanceof IFluxConnector) {
                    IFluxConnector flux = (IFluxConnector) tile;
                    ItemStack pickBlock = flux.getDisplayStack().setDisplayName(new StringTextComponent(flux.getCustomName()));
                    iProbeInfo.horizontal().item(pickBlock).vertical().itemLabel(pickBlock).text(TextStyleClass.MODNAME + FluxNetworks.NAME);
                    return true;
                }
            }
            return false;
        }
    }
}
