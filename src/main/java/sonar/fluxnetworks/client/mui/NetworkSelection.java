package sonar.fluxnetworks.client.mui;

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

    protected NetworkSelection(INetworkConnector connector) {
        addWidget(new Panel(this, connector));
    }

    // Must be static, because the 3rd param is Function<Panel,> and "this" is Panel, or java will crash
    private static class Panel extends MultiPageScrollPanel<Group.Entry, Group> {

        public Panel(IHost host, INetworkConnector connector) {
            super(host, new MultiPageScrollPanel.Builder(30)
                            .setWidth(162)
                            .setHeight(132)
                            .setLocator(new Locator(-81, -59)),
                    panel -> new Group(panel, connector));
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

    // Must be static, because Panel is static nested class
    private static class Group extends UniformScrollGroup<Group.Entry> {

        private final INetworkConnector connector;

        public Group(IScrollHost window, INetworkConnector connector) {
            super(window, 12);
            this.connector = connector;
            entries.addAll(FluxNetworkCache.INSTANCE.getAllClientNetworks().stream().map(f -> new Entry(window, f)).collect(Collectors.toList()));
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

        // Can be inner class, to call connector field
        private class Entry extends UniformScrollEntry {

            private final IFluxNetwork network;

            public Entry(@Nonnull IScrollHost window, IFluxNetwork network) {
                super(window, 146, 12);
                this.network = network;
            }

            @Override
            protected void onDraw(@Nonnull Canvas canvas, float time) {
                canvas.setLineAntiAliasing(true);
                canvas.setLineWidth(2.0f);
                int color = network.getSetting(NetworkSettings.NETWORK_COLOR);
                float factor = NavigationHome.network.getNetworkID() == network.getNetworkID() ? 1.0f : isMouseHovered() ? 0.85f : 0.7f;
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
                    if (connector instanceof IFluxConnector) {
                        PacketHandler.INSTANCE.sendToServer(
                                new TilePacket(TilePacketEnum.SET_NETWORK,
                                        TilePacketHandler.getSetNetworkPacket(network.getNetworkID(), ""),
                                        ((IFluxConnector) connector).getCoords()));
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
