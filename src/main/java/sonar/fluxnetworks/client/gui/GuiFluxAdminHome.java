package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.SwitchButton;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;

public class GuiFluxAdminHome extends GuiTabCore {

    public SwitchButton mDetailedNetworkView, mSuperAdmin;

    public GuiFluxAdminHome(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);

        int color = getNetwork().getNetworkColor();
        renderNetwork(poseStack, getNetwork().getNetworkName(), color, leftPos + 20, topPos + 8);

        font.draw(poseStack, AccessLevel.SUPER_ADMIN.getFormattedName(), leftPos + 20, topPos + 30, color);
        font.draw(poseStack, FluxTranslate.DETAILED_VIEW.get(), leftPos + 20, topPos + 42, color);
    }

    @Override
    public void init() {
        super.init();

        boolean superAdmin = ClientCache.sSuperAdmin;
        mSuperAdmin = new SwitchButton(this, leftPos + 140, topPos + 30, superAdmin);
        mButtons.add(mSuperAdmin);

        mDetailedNetworkView = new SwitchButton(this, leftPos + 140, topPos + 42,
                ClientCache.sDetailedNetworkView);
        mDetailedNetworkView.setClickable(superAdmin);
        mButtons.add(mDetailedNetworkView);
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (button == mSuperAdmin) {
                // delayed toggle, wait for server response
                ClientMessages.superAdmin(getToken(), !mSuperAdmin.isChecked());
            } else if (button == mDetailedNetworkView) {
                mDetailedNetworkView.toggle();
                ClientCache.sDetailedNetworkView = mDetailedNetworkView.isChecked();
            }
        }
    }

    /*@Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            C2SNetMsg.requestNetworkUpdate(network, FluxConstants.TYPE_NET_BASIC);
            C2SNetMsg.requestAccessUpdate(network.getNetworkID());
        }
        timer++;
        timer %= 100;
    }*/

    @Override
    protected void containerTick() {
        super.containerTick();
        boolean superAdmin = ClientCache.sSuperAdmin;
        mSuperAdmin.setChecked(superAdmin);
        mDetailedNetworkView.setClickable(superAdmin);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (mouseX >= leftPos + 20 && mouseX < leftPos + 155 && mouseY >= topPos + 8 && mouseY < topPos + 20) {
                switchTab(EnumNavigationTab.TAB_SELECTION, false);
                return true;
            }
        }
        return false;
    }
}
