package sonar.fluxnetworks.client.mui.module;

import icyllis.modernui.gui.master.*;

import javax.annotation.Nonnull;

public class WirelessCharging extends Module {

    protected WirelessCharging() {

    }

    private static class WirelessSlot extends Widget {

        private final Icon icon;

        public WirelessSlot(IHost host, @Nonnull Builder builder, @Nonnull Icon icon) {
            super(host, builder);
            this.icon = icon;
        }

        @Override
        protected void onDraw(@Nonnull Canvas canvas, float time) {
            canvas.drawIcon(icon, x1, y1, x2, y2);
            if (isMouseHovered()) {

            }
        }
    }
}
