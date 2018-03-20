package sonar.flux.client.tabs;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

//import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiTabNetworkEdit extends AbstractGuiTab {

	public SonarTextField name, r, g, b;
	public int currentColour;
	public AccessType currentAccess = AccessType.PRIVATE;
	public boolean previewSelected = true, showFullPreview = true;

	public GuiTabNetworkEdit(TileFlux tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (getCurrentTab() == GuiTab.NETWORK_CREATE) {
			initEditFields(mc.player.getName() + "'s" + " Network", colours[currentColour]);
			buttonList.add(new GuiButton(5, getGuiLeft() + 5, getGuiTop() + 140, 80, 20, "Reset"));
			buttonList.add(new GuiButton(6, getGuiLeft() + 90, getGuiTop() + 140, 80, 20, "Create"));
		} else {
			if (!common.isFakeNetwork()) {
				initEditFields(common.getNetworkName(), common.getNetworkColour());
				buttonList.add(new GuiButton(5, getGuiLeft() + 5, getGuiTop() + 140, 80, 20, "Reset"));
				buttonList.add(new GuiButton(6, getGuiLeft() + 90, getGuiTop() + 140, 80, 20, "Save Changes"));
				currentAccess = common.getAccessType();
			} else {
				disabled = true;
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (disabled) {
			renderNavigationPrompt("No network to edit", "Network Selection");
		} else {
			pushMatrix();

			if (getCurrentTab() == GuiTab.NETWORK_CREATE) {
				FontHelper.textCentre(GUI.CREATE_NETWORK.toString(), xSize, 8, Color.GRAY.getRGB());
			} else {
				FontHelper.textCentre(GUI.EDIT_NETWORK.toString(), xSize, 8, Color.GRAY.getRGB());
			}

			FontHelper.text(GUI.NETWORK_NAME + ": ", 8, 24, 0);
			FontHelper.text("Colour" + ": ", 8, 80, 0);

			FontHelper.text(TextFormatting.RED + "R:", 46, 80, -1);
			FontHelper.text(TextFormatting.GREEN + "G:", 86, 80, -1);
			FontHelper.text(TextFormatting.BLUE + "B:", 126, 80, -1);

			CustomColour colour = getCurrentColour();
			Gui.drawRect(55, 63 + 32, 165, 68 + 32 + 4, colour.getRGB());

			FontHelper.text(GUI.ACCESS_SETTING + ": " + TextFormatting.AQUA + FontHelper.translate(currentAccess.getName()), 8, 40, 0);
			FontHelper.text(FontHelper.translate("Preview") + ": ", 8, 96, 0);
			String networkName = name.getText().isEmpty() ? "Network Name" : name.getText();
			if (showFullPreview) {
				renderNetworkInFull(networkName, currentAccess, colour.getRGB(), previewSelected, 11, 110);
			} else {
				renderNetwork(networkName, currentAccess, colour.getRGB(), previewSelected, 11, 110);
			}

			if (x - getGuiLeft() > 55 && x - getGuiLeft() < 165 && y - getGuiTop() > 63 + 32 && y - getGuiTop() < 68 + 32 + 4) {
				drawHoveringText(GUI.NEXT_COLOUR.toString(), x - getGuiLeft(), y - getGuiTop());
			}
			if (x - getGuiLeft() > 5 && x - getGuiLeft() < 165 && y - getGuiTop() > 38 && y - getGuiTop() < 52) {
				drawHoveringText(GUI.CHANGE_SETTING.toString(), x - getGuiLeft(), y - getGuiTop());
			}
			popMatrix();
		}
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		switch (button.id) {
		case 5:
			resetCreateTab();
			break;
		case 6:
			if (!name.getText().isEmpty()) {

				if (getCurrentTab() == GuiTab.NETWORK_CREATE) {
					PacketHelper.sendPacketToServer(PacketType.CREATE_NETWORK, flux, PacketHelper.createNetworkCreationPacket(name.getText(), getCurrentColour(), currentAccess));
				} else {
					PacketHelper.sendPacketToServer(PacketType.EDIT_NETWORK, flux, PacketHelper.createNetworkEditPacket(getNetworkID(), name.getText(), getCurrentColour(), currentAccess));
				}

				switchTab(GuiTab.NETWORK_SELECT);
				resetCreateTab();
				return;
			}
			break;
		}

	}

	@Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (mouseButton == 1) {
			name.setText("");
		}
		if (x - getGuiLeft() > 55 && x - getGuiLeft() < 165 && y - getGuiTop() > 63 + 32 && y - getGuiTop() < 68 + 32 + 4) {
			currentColour++;
			if (currentColour >= AbstractGuiTab.colours.length) {
				currentColour = 0;
			}
			CustomColour colour = AbstractGuiTab.colours[currentColour];
			r.setText(String.valueOf(colour.red));
			g.setText(String.valueOf(colour.green));
			b.setText(String.valueOf(colour.blue));
		}
		if (x - getGuiLeft() > 5 && x - getGuiLeft() < 165 && y - getGuiTop() > 38 && y - getGuiTop() < 52) {
			currentAccess = AccessType.values()[currentAccess.ordinal() + 1 < AccessType.values().length ? currentAccess.ordinal() + 1 : 0];
		}
		if (x - getGuiLeft() > 11 && x - getGuiLeft() < 165 && y - getGuiTop() > 108 && y - getGuiTop() < 134) {
			showFullPreview = !showFullPreview;
		}

	}

	public void resetCreateTab() {
		name.setText("");
		currentColour = 0;
		currentAccess = AccessType.PRIVATE;
		reset();
	}

	public void initEditFields(String networkName, CustomColour colour) {
		name = new SonarTextField(1, getFontRenderer(), 38, 22, 130, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB());
		name.setMaxStringLength(24);
		name.setText(networkName);

		r = new SonarTextField(2, getFontRenderer(), 56, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		r.setMaxStringLength(3);
		r.setText(String.valueOf(colour.red));

		g = new SonarTextField(3, getFontRenderer(), 96, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		g.setMaxStringLength(3);
		g.setText(String.valueOf(colour.green));

		b = new SonarTextField(4, getFontRenderer(), 136, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		b.setMaxStringLength(3);
		b.setText(String.valueOf(colour.blue));
		this.fieldList.addAll(Lists.newArrayList(name, r, g, b));
	}

	public CustomColour getCurrentColour() {
		return new CustomColour(r.getIntegerFromText(), g.getIntegerFromText(), b.getIntegerFromText());
	}

	@Override
	public ResourceLocation getBackground() {
		return this.blank_flux_gui;
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_EDIT;
	}

}