package sonar.flux.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.common.block.SonarBlock;
import sonar.core.common.block.SonarBlockTip;
import sonar.core.registries.SonarRegistryBlock;
import sonar.flux.FluxTranslate;
import sonar.flux.client.FluxColourHandler;
import sonar.flux.common.block.FluxStorage;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemNetworkConnector extends SonarBlockTip {

    public static final String CUSTOM_NAME_TAG = "cust_name";
    public static final String PRIORITY_TAG = "priority";
    public static final String TRANSFER_LIMIT_TAG = "limit";
    public static final String DISABLE_LIMIT_TAG = "e_limit";
    public static final String NETWORK_ID_TAG = "network_id";

    public ItemNetworkConnector(Block block) {
        super(block);
    }


    @Override
    public String getItemStackDisplayName(ItemStack stack){
        NBTTagCompound tag = stack.getSubCompound(SonarBlock.DROP_TAG_NAME);
        if(tag != null && tag.hasKey(CUSTOM_NAME_TAG)){
            return tag.getString(CUSTOM_NAME_TAG);
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
        NBTTagCompound tag = stack.getSubCompound(SonarBlock.DROP_TAG_NAME);
        if(tag != null) {
            list.add(FluxTranslate.NETWORK_NAME.t() + ": " + FluxColourHandler.getOrRequestNetworkName(tag.getInteger(NETWORK_ID_TAG)));
            list.add(FluxTranslate.TRANSFER_LIMIT.t() + ": " + (tag.getBoolean(DISABLE_LIMIT_TAG) ? FluxTranslate.NO_LIMIT.t() : tag.getLong(TRANSFER_LIMIT_TAG)));
            list.add(FluxTranslate.PRIORITY.t() + ": " + tag.getLong(PRIORITY_TAG));
        }else{
            super.addInformation(stack, world, list, flag);
        }
    }

    public static class FluxConnectorRegistry<T extends Block> extends SonarRegistryBlock<T>{

        public FluxConnectorRegistry(T block, String name, Class tile) {
            super(block, name, tile);
        }

        public FluxConnectorRegistry(T block, String name) {
            super(block, name);
        }

        @Override
        public ModelResourceLocation getItemBlockRendererLocation(String modid, Item item){
            if(this.getValue() instanceof FluxStorage){
                return new ModelResourceLocation(modid + ':' + "fluxstoragebuiltin", "inventory");
            }
            return super.getItemBlockRendererLocation(modid, item);
        }

        @Override
        public Item getItemBlock() {
            return new ItemNetworkConnector(value);
        }
    }
}
