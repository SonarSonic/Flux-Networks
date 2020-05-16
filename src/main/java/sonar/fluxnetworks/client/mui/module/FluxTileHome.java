package sonar.fluxnetworks.client.mui.module;

import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.master.IDrawable;
import icyllis.modernui.gui.master.IKeyboardListener;
import icyllis.modernui.gui.master.Module;
import icyllis.modernui.gui.math.Align3H;
import icyllis.modernui.gui.math.Color3f;
import icyllis.modernui.gui.math.Locator;
import icyllis.modernui.gui.widget.NumberInputField;
import icyllis.modernui.gui.widget.SlidingToggleButton;
import icyllis.modernui.gui.widget.TextField;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.network.TilePacketBufferConstants;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

import javax.annotation.Nonnull;

/**
 * The home page for flux tiles (plug / point / storage)
 *
 * @author BloCamLimb
 */
public class FluxTileHome extends Module {

    private final TileFluxCore tileEntity;

    private TextField.Frame nameDeco;
    private TextField.Frame priorityDeco;
    private TextField.Frame limitDeco;

    protected FluxTileHome(@Nonnull TileFluxCore tileEntity) {
        this.tileEntity = tileEntity;

        addDrawable(this.new Background());

        TextField nameField = new TextField.Builder()
                .setWidth(144)
                .setHeight(12)
                .setLocator(new Locator(-72, -52))
                .build(this);
        nameDeco = new TextField.Frame(nameField, FluxTranslate.NAME.t() + ":", -1);
        nameField.setMaxStringLength(24);
        nameField.setText(tileEntity.getCustomName());
        nameField.setDecoration(f -> nameDeco);
        nameField.setListener(this::changeName, false);
        addWidget(nameField);

        NumberInputField priorityField = new NumberInputField(this, new TextField.Builder()
                .setWidth(144)
                .setHeight(12)
                .setLocator(new Locator(-72, -35)));
        priorityDeco = new TextField.Frame(priorityField, FluxTranslate.PRIORITY.t() + ":", -1);
        priorityField.setLimit(-10000, 10000);
        priorityField.setText(String.valueOf(tileEntity.priority));
        priorityField.setDecoration(f -> priorityDeco);
        priorityField.setNumberListener(this::changePriority, true);
        addWidget(priorityField);

        NumberInputField limitField = new NumberInputField(this, new TextField.Builder()
                .setWidth(144)
                .setHeight(12)
                .setLocator(new Locator(-72, -18)));
        limitDeco = new TextField.Frame(limitField, FluxTranslate.TRANSFER_LIMIT.t() + ":", -1);
        limitField.setLimit(0, Math.min(tileEntity.getMaxTransferLimit(), 1000000000));
        limitField.setText(String.valueOf(tileEntity.limit));
        limitField.setDecoration(f -> limitDeco);
        limitField.setNumberListener(this::changeLimit, true);
        addWidget(limitField);

        SlidingToggleButton surgeToggle = new SlidingToggleButton.Builder(0x8006c909, 0x40808080, 4)
                .setLocator(new Locator(47, 38))
                .build(this)
                .buildCallback(tileEntity.surgeMode, this::changeSurgeMode);
        addWidget(surgeToggle);

        SlidingToggleButton unlimitedToggle = new SlidingToggleButton.Builder(0x8090b9f9, 0x40808080, 4)
                .setLocator(new Locator(47, 50))
                .build(this)
                .buildCallback(tileEntity.disableLimit, this::changeUnlimitedMode);
        addWidget(unlimitedToggle);

        updateDecoColor();
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);
        if ((ticks & 15) == 0) {
            //TODO use packet
            updateDecoColor();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        IKeyboardListener k = this.getKeyboardListener();
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            if (k != null && this.getKeyboardListener() != k) {
                this.setKeyboardListener(null);
            }
            return true;
        } else if (this.getKeyboardListener() != null) {
            this.setKeyboardListener(null);
            return true;
        } else {
            return false;
        }
    }

    private void changeName(@Nonnull TextField field) {
        tileEntity.customName = field.getText();
        tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_CUSTOM_NAME);
    }

    private void changePriority(@Nonnull NumberInputField field) {
        tileEntity.priority = field.getIntegerFromText();
        tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_PRIORITY);
    }

    private void changeLimit(@Nonnull NumberInputField field) {
        tileEntity.limit = field.getLongFromText();
        tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_LIMIT);
    }

    private void changeSurgeMode(boolean on) {
        tileEntity.surgeMode = on;
        tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_SURGE_MODE);
    }

    private void changeUnlimitedMode(boolean on) {
        tileEntity.disableLimit = on;
        tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_DISABLE_LIMIT);
    }

    private void updateDecoColor() {
        int color = NavigationHome.network.getSetting(NetworkSettings.NETWORK_COLOR);
        // RGB to RGBA
        color = color | 0xff000000;
        nameDeco.setColor(color);
        priorityDeco.setColor(color);
        limitDeco.setColor(color);
    }

    private class Background implements IDrawable {

        private float x1;
        private float y1;

        private float r, g, b;

        public Background() {
            updateInfo();
        }

        @Override
        public void draw(@Nonnull Canvas canvas, float v) {
            canvas.resetColor();
            canvas.setTextAlign(Align3H.LEFT);
            canvas.drawText(FluxUtils.getTransferInfo(tileEntity.getConnectionType(),
                    NavigationHome.network.getSetting(NetworkSettings.NETWORK_ENERGY),
                    tileEntity.getChange()), x1 + 28, y1 + 8);
            canvas.drawText((tileEntity.getConnectionType().isStorage() ? FluxTranslate.ENERGY.t() : FluxTranslate.BUFFER.t()) +
                    ": " + TextFormatting.BLUE + FluxUtils.format(tileEntity.getTransferHandler().getBuffer(), FluxUtils.TypeNumberFormat.COMMAS,
                    NavigationHome.network.getSetting(NetworkSettings.NETWORK_ENERGY), false), x1 + 28, y1 + 18);
            canvas.drawItemStack(tileEntity.getDisplayStack(), x1 + 9, y1 + 10);
            canvas.setRGB(r, g, b);
            canvas.drawText(FluxTranslate.SURGE_MODE.t(), x1 + 17, y1 + 38);
            canvas.drawText(FluxTranslate.DISABLE_LIMIT.t(), x1 + 17, y1 + 50);
        }

        @Override
        public void resize(int width, int height) {
            this.x1 = width / 2f - 85;
            this.y1 = height / 2f;
        }

        @Override
        public void tick(int ticks) {
            if ((ticks & 15) == 0) {
                updateInfo();
            }
        }

        private void updateInfo() {
            int color = NavigationHome.network.getSetting(NetworkSettings.NETWORK_COLOR);
            r = Color3f.getRedFrom(color);
            g = Color3f.getGreenFrom(color);
            b = Color3f.getBlueFrom(color);
        }
    }
}
