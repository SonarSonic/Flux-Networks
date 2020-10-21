package sonar.fluxnetworks.common.integration;

import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.common.block.FluxDeviceBlock;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.misc.NumberFormatType;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class TOPIntegration implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(@Nonnull ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(new FluxDeviceInfoProvider());
        iTheOneProbe.registerBlockDisplayOverride(new FluxDeviceDisplayOverride());
        return null;
    }

    public static class FluxDeviceInfoProvider implements IProbeInfoProvider {

        @Override
        public String getID() {
            return FluxNetworks.MODID;
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world,
                                 BlockState blockState, IProbeHitData iProbeHitData) {
            if (!(FluxConfig.enableOneProbeBasicInfo || FluxConfig.enableOneProbeAdvancedInfo)) {
                return;
            }
            if (blockState.getBlock() instanceof FluxDeviceBlock) {
                TileEntity tile = world.getTileEntity(iProbeHitData.getPos());
                if (tile instanceof TileFluxDevice) {
                    TileFluxDevice flux = (TileFluxDevice) tile;
                    if (FluxConfig.enableOneProbeBasicInfo) {
                        iProbeInfo.text((flux.getNetwork().isValid() ?
                                new StringTextComponent(TextFormatting.AQUA + flux.getNetwork().getNetworkName())
                                : new StringTextComponent(TextFormatting.AQUA + FluxTranslate.ERROR_NO_SELECTED.t())));

                        iProbeInfo.text(new StringTextComponent(
                                FluxUtils.getTransferInfo(flux.getDeviceType(), EnergyType.FE, flux.getTransferChange())));

                        if (playerEntity.isSneaking()) {
                            if (flux.getDeviceType().isStorage()) {
                                iProbeInfo.text(FluxTranslate.ENERGY_STORED.getTextComponent()
                                        .appendString(": " + TextFormatting.GREEN + FluxUtils.format(flux.getTransferBuffer(),
                                                NumberFormatType.FULL, EnergyType.FE, false))
                                );
                            } else {
                                iProbeInfo.text(FluxTranslate.INTERNAL_BUFFER.getTextComponent()
                                        .appendString(": " + TextFormatting.GREEN + FluxUtils.format(flux.getTransferBuffer(),
                                                NumberFormatType.FULL, EnergyType.FE, false))
                                );
                            }
                        } else {
                            if (flux.getDeviceType().isStorage()) {
                                iProbeInfo.text(FluxTranslate.ENERGY_STORED.getTextComponent()
                                        .appendString(": " + TextFormatting.GREEN + FluxUtils.format(flux.getTransferBuffer(),
                                                NumberFormatType.COMPACT, EnergyType.FE, false))
                                );
                            } else {
                                iProbeInfo.text(FluxTranslate.INTERNAL_BUFFER.getTextComponent()
                                        .appendString(": " + TextFormatting.GREEN + FluxUtils.format(flux.getTransferBuffer(),
                                                NumberFormatType.COMPACT, EnergyType.FE, false))
                                );
                            }
                        }
                    }
                    if (FluxConfig.enableOneProbeAdvancedInfo &&
                            (!FluxConfig.enableOneProbeSneaking || playerEntity.isSneaking())) {

                        if (flux.getDisableLimit()) {
                            iProbeInfo.text(FluxTranslate.TRANSFER_LIMIT.getTextComponent()
                                    .appendString(": " + TextFormatting.GREEN + FluxTranslate.UNLIMITED)
                            );
                        } else {
                            iProbeInfo.text(FluxTranslate.TRANSFER_LIMIT.getTextComponent()
                                    .appendString(": " + TextFormatting.GREEN + flux.getRawLimit())
                            );
                        }

                        if (flux.getSurgeMode()) {
                            iProbeInfo.text(FluxTranslate.PRIORITY.getTextComponent()
                                    .appendString(": " + TextFormatting.GREEN + FluxTranslate.SURGE)
                            );
                        } else {
                            iProbeInfo.text(FluxTranslate.PRIORITY.getTextComponent()
                                    .appendString(": " + TextFormatting.GREEN + flux.getRawPriority())
                            );
                        }

                        if (flux.isForcedLoading()) {
                            iProbeInfo.text(new StringTextComponent(TextFormatting.GOLD + FluxTranslate.FORCED_LOADING.t()));
                        }
                    }
                }
            }
        }
    }

    public static class FluxDeviceDisplayOverride implements IBlockDisplayOverride {

        @Override
        public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world,
                                            @Nonnull BlockState blockState, IProbeHitData iProbeHitData) {
            if (blockState.getBlock() instanceof FluxDeviceBlock) {
                TileEntity tile = world.getTileEntity(iProbeHitData.getPos());
                if (tile instanceof IFluxDevice) {
                    IFluxDevice flux = (IFluxDevice) tile;
                    ItemStack itemStack = flux.getDisplayStack();
                    CompoundNBT tag = itemStack.getOrCreateChildTag(FluxConstants.TAG_FLUX_DATA);
                    tag.putInt(FluxConstants.NETWORK_ID, flux.getNetworkID());
                    tag.putString(FluxConstants.CUSTOM_NAME, flux.getCustomName());
                    iProbeInfo.horizontal().item(itemStack)
                            .vertical().itemLabel(itemStack)
                            .text(new StringTextComponent(TextStyleClass.MODNAME + FluxNetworks.NAME));
                    return true;
                }
            }
            return false;
        }
    }
}
