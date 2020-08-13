package sonar.fluxnetworks.client.mui;

/**
 * The root module of all flux gui tabs
 * This module contains all navigation buttons, and updates gui data
 *
 * @author BloCamLimb
 */
public class NavigationHome {/*extends ModuleGroup {

    @Nonnull
    public static IFluxNetwork network = FluxNetworkInvalid.INSTANCE;

    private final List<TextIconButton> navigationButtons = new ArrayList<>();

    private final FluxBackground bg;

    private final INetworkConnector connector;

    public NavigationHome(@Nonnull INetworkConnector connector) {
        network = FluxNetworkCache.INSTANCE.getClientNetwork(connector.getNetworkID());
        this.connector = connector;

        addDrawable(bg = new FluxBackground());

        for (EnumNavigationTabs tab : EnumNavigationTabs.values()) {
            int id = tab.getId();
            Icon icon = new Icon(ConstantsLibrary.ICONS, ((id - 1) * 32) / 512f, 352 / 512f, (id * 32) / 512f, 384 / 512f, true);
            Locator locator;
            if (tab == EnumNavigationTabs.TAB_CREATE) {
                locator = new Locator(60, -95); // spacing = 12
            } else {
                locator = new Locator(id * 18 - 94, -95); // spacing = 2
            }
            TextIconButton button = new TextIconButton.Builder(icon, tab.getTranslatedName())
                    .setWidth(16)
                    .setHeight(16)
                    .setLocator(locator)
                    .setModuleId(id)
                    .setTextDirection(Direction4D.UP)
                    .build(this)
                    .buildCallback(tab == EnumNavigationTabs.TAB_HOME, () -> {
                        switchChildModule(id);
                        if (FluxConfig.enableButtonSound) {
                            playSound(RegistryLibrary.BUTTON_CLICK_1);
                        }
                    });
            navigationButtons.add(button);
        }
        navigationButtons.forEach(this::addWidget);

        if (connector instanceof TileFluxCore) {
            addChildModule(EnumNavigationTabs.TAB_HOME.getId(), () -> new FluxTileHome((TileFluxCore) connector));
        }
        addChildModule(EnumNavigationTabs.TAB_SELECTION.getId(), () -> new NetworkSelection(connector));
        addChildModule(EnumNavigationTabs.TAB_WIRELESS.getId(), WirelessCharging::new);

        switchChildModule(EnumNavigationTabs.TAB_HOME.getId());
    }

    @Override
    protected void onChildModuleChanged(int id) {
        super.onChildModuleChanged(id);
        navigationButtons.forEach(e -> e.onModuleChanged(id));
        if (id == EnumNavigationTabs.TAB_HOME.getId()) {
            bg.setRenderNetworkName(true);
        } else {
            bg.setRenderNetworkName(false);
        }
    }

    @Override
    public void tick(int ticks) {
        super.tick(ticks);
        //TODO use packet
        network = FluxNetworkCache.INSTANCE.getClientNetwork(connector.getNetworkID());
    }

    *//**
 * Flux gui background layer
 *
 * @author BloCamLimb
 *//*
    private static class FluxBackground implements IDrawable {

        private float x1, x2, y1, y2;

        private float r, g, b;

        private boolean renderNetworkName = false;

        public FluxBackground() {
            updateColor();
        }

        @Override
        public void draw(@Nonnull Canvas canvas, float v) {
            canvas.setRGBA(0, 0, 0, 0.5f);
            canvas.drawRoundedRect(x1, y1, x2, y2, 5);
            canvas.setRGBA(r, g, b, 1.0f);
            canvas.drawRoundedRectFrame(x1, y1, x2, y2, 5);
            if (renderNetworkName) {
                canvas.setLineAntiAliasing(true);
                canvas.setLineWidth(2.0f);
                canvas.drawOctagonRectFrame(x1 + 18, y1 + 6, x2 - 18, y1 + 18, 2);
                canvas.setLineAntiAliasing(false);
                canvas.resetColor();
                canvas.setTextAlign(TextAlign.LEFT);
                canvas.drawText(network.getNetworkName(), x1 + 22, y1 + 8);
            }
            RenderSystem.enableDepthTest();
            canvas.setZ(300);
            canvas.setColor(Color3i.GRAY, 0.2f);
            canvas.drawRect(20, 20, 40, 40);
            canvas.setZ(0);
            canvas.setColor(Color3i.GOLD, 1);
            canvas.drawRect(30, 20, 50, 40);

            canvas.setZ(300);
            canvas.setColor(Color3i.GRAY, 1);
            canvas.drawRect(60, 20, 80, 40);
            canvas.setZ(0);
            canvas.setColor(Color3i.GOLD, 0.2f);
            canvas.drawRect(70, 20, 90, 40);
            RenderSystem.disableDepthTest();
        }

        @Override
        public void resize(int width, int height) {
            this.x1 = width / 2f - 85;
            this.x2 = x1 + 170;
            this.y1 = height / 2f - 77;
            this.y2 = y1 + 170;
        }

        @Override
        public void tick(int ticks) {
            if ((ticks & 15) == 0) {
                //TODO use packet
                updateColor();
            }
        }

        private void updateColor() {
            int color = network.getSetting(NetworkSettings.NETWORK_COLOR);
            r = Color3i.getRedFrom(color);
            g = Color3i.getGreenFrom(color);
            b = Color3i.getBlueFrom(color);
        }

        public void setRenderNetworkName(boolean renderNetworkName) {
            this.renderNetworkName = renderNetworkName;
        }
    }*/
}
