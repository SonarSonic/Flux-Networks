package sonar.fluxnetworks.client.mui.module;

import icyllis.modernui.gui.master.Canvas;
import icyllis.modernui.gui.master.IHost;
import icyllis.modernui.gui.master.Module;
import icyllis.modernui.gui.math.Align3H;
import icyllis.modernui.gui.math.Color3f;
import icyllis.modernui.gui.math.Locator;
import icyllis.modernui.gui.scroll.IScrollHost;
import icyllis.modernui.gui.scroll.MultiPageScrollPanel;
import icyllis.modernui.gui.scroll.UniformScrollEntry;
import icyllis.modernui.gui.scroll.UniformScrollGroup;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.TilePacket;
import sonar.fluxnetworks.common.network.TilePacketEnum;
import sonar.fluxnetworks.common.network.TilePacketHandler;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class NetworkSelection extends Module {

    private final INetworkConnector connector;

    protected NetworkSelection(INetworkConnector connector) {
        this.connector = connector;
        addWidget(new Panel());
    }

    private class Panel extends MultiPageScrollPanel<Entry, Group> {

        //TODO crash here
        public Panel() {
            super(NetworkSelection.this, new MultiPageScrollPanel.Builder(30)
                            .setWidth(162)
                            .setHeight(134)
                            .setLocator(new Locator(-81, -59)),
                    Group::new);
        }

        @Override
        protected void onDraw(@Nonnull Canvas canvas, float time) {
            super.onDraw(canvas, time);
            canvas.resetColor();
            canvas.setTextAlign(Align3H.RIGHT);
            String a = FluxTranslate.TOTAL.t() + ": " + allEntries.size();
            canvas.drawText(a, x2 - 12, y1 - 11);
        }
    }

    private class Group extends UniformScrollGroup<Entry> {

        public Group(IScrollHost window) {
            super(window, 12);
            entries.addAll(FluxNetworkCache.instance.getAllClientNetworks().stream().map(f -> new Entry(window, f)).collect(Collectors.toList()));
            height = entries.size() * entryHeight;
        }

        @Override
        public void locate(float px, float py) {
            super.locate(px, py);
            int i = 0;
            for (Entry entry : entries) {
                float cy = py + i * entryHeight;
                entry.locate(px, cy);
                i++;
            }
        }
    }

    private class Entry extends UniformScrollEntry {

        private final IFluxNetwork network;

        private float br = 0.7f;

        public Entry(@Nonnull IScrollHost window, IFluxNetwork network) {
            super(window, 146, 12);
            this.network = network;
        }

        @Override
        protected void onDraw(@Nonnull Canvas canvas, float time) {
            canvas.setLineAntiAliasing(true);
            canvas.setLineWidth(2.0f);
            int color = network.getSetting(NetworkSettings.NETWORK_COLOR);
            float factor = NavigationHome.network.getNetworkID() == network.getNetworkID() ? 1 : br;
            canvas.setRGBA(Color3f.getRedFrom(color) * factor, Color3f.getGreenFrom(color) * factor, Color3f.getBlueFrom(color) * factor, 1);
            canvas.drawOctagonRectFrame(x1, y1 + 1, x2, y2 - 1, 2);
            canvas.setLineAntiAliasing(false);
            factor = factor * factor * factor;
            canvas.setRGB(factor, factor, factor);
            canvas.setTextAlign(Align3H.LEFT);
            canvas.drawText(network.getNetworkName(), x1 + 4, y1 + 2);
        }

        @Override
        protected boolean onMouseLeftClick(double mouseX, double mouseY) {
            if (NavigationHome.network.getNetworkID() != network.getNetworkID()) {
                PacketHandler.INSTANCE.sendToServer(
                        new TilePacket(TilePacketEnum.SET_NETWORK,
                                TilePacketHandler.getSetNetworkPacket(network.getNetworkID(), ""),
                                ((IFluxConnector) connector).getCoords()));
                return true;
            }
            return false;
        }

        @Override
        protected void onMouseHoverEnter(double mouseX, double mouseY) {
            super.onMouseHoverEnter(mouseX, mouseY);
            br = 0.85f;
        }

        @Override
        protected void onMouseHoverExit() {
            super.onMouseHoverExit();
            br = 0.7f;
        }
    }
}
