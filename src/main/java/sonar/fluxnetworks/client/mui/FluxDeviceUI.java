package sonar.fluxnetworks.client.mui;

import icyllis.modernui.ModernUI;
import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.math.Rect;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.LinearLayout;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static icyllis.modernui.view.ViewConfiguration.dp;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FluxDeviceUI extends Fragment {

    public static final int NETWORK_COLOR = 0xFF295E8A;

    public static final int id_tab_container = 0x0002;

    public static Image sButtonIcon;

    private final TileFluxDevice mDevice;

    public FluxDeviceUI(@Nonnull TileFluxDevice device) {
        mDevice = device;
    }

    @Override
    public void onAttach() {
        super.onAttach();
        getParentFragmentManager().beginTransaction()
                .setPrimaryNavigationFragment(this)
                .commit();
    }

    @Override
    public void onCreate(@Nullable DataSet savedInstanceState) {
        super.onCreate(savedInstanceState);
        sButtonIcon = Image.create(ModernUI.ID, "gui/gui_icon.png");

        var fragment = new FluxConnectorHome(mDevice);
        getChildFragmentManager().beginTransaction()
                .replace(id_tab_container, fragment, "home")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        var content = new LinearLayout();
        content.setOrientation(LinearLayout.VERTICAL);

        var buttonGroup = new LinearLayout();
        buttonGroup.setOrientation(LinearLayout.HORIZONTAL);
        buttonGroup.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        buttonGroup.setLayoutTransition(new LayoutTransition());

        int buttonSize = dp(32);
        for (int i = 0; i < 8; i++) {
            var button = new NavigationButton(sButtonIcon, i * 32);
            var params = new LinearLayout.LayoutParams(buttonSize, buttonSize);
            button.setClickable(true);
            params.setMarginsRelative(dp(i == 7 ? 26 : 2), dp(2), dp(2), dp(6));
            if (i == 0 || i == 7) {
                buttonGroup.addView(button, params);
            } else {
                int index = i;
                content.postDelayed(() -> buttonGroup.addView(button, index, params), i * 50);
            }
            if (i == 7) {
                button.setOnClickListener(__ -> {
                    var fm = getChildFragmentManager();
                    var fragment = fm.findFragmentByTag("create");
                    boolean add = false;
                    if (fragment == null) {
                        fragment = new TabCreate();
                        add = true;
                    }
                    var ft = fm.beginTransaction();
                    ft.replace(id_tab_container, fragment, "create")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    if (add)
                        ft.addToBackStack(null);
                    ft.setReorderingAllowed(true)
                            .commit();
                });
            } else if (i == 0) {
                button.setOnClickListener(__ -> getChildFragmentManager().popBackStack());
            }
        }

        content.addView(buttonGroup, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        var tabContainer = new FragmentContainerView();
        tabContainer.setBackground(new TabBackground());
        tabContainer.setId(id_tab_container);

        int tabSize = dp(340);
        content.addView(tabContainer, new LinearLayout.LayoutParams(tabSize, tabSize));

        content.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
        return content;
    }

    private static class TabBackground extends Drawable {

        private final float mRadius;

        public TabBackground() {
            mRadius = dp(16);
        }

        @Override
        public void draw(@Nonnull Canvas canvas) {
            Rect b = getBounds();
            float stroke = mRadius * 0.25f;
            float start = stroke * 0.5f;

            Paint paint = Paint.take();
            paint.setRGBA(0, 0, 0, 180);
            canvas.drawRoundRect(b.left + start, b.top + start, b.right - start, b.bottom - start, mRadius, paint);
            paint.setStyle(Paint.STROKE);
            paint.setStrokeWidth(stroke);
            paint.setColor(NETWORK_COLOR);
            canvas.drawRoundRect(b.left + start, b.top + start, b.right - start, b.bottom - start, mRadius, paint);
        }
    }

    public static class TextFieldBackground extends Drawable {

        private final float mRadius;

        public TextFieldBackground() {
            mRadius = dp(3);
        }

        @Override
        public void draw(@Nonnull Canvas canvas) {
            Rect b = getBounds();
            float start = mRadius * 0.5f;

            Paint paint = Paint.take();
            paint.setStyle(Paint.STROKE);
            paint.setStrokeWidth(mRadius);
            paint.setColor(NETWORK_COLOR);
            canvas.drawRoundRect(b.left + start, b.top + start, b.right - start, b.bottom - start, mRadius, paint);
        }

        @Override
        public boolean getPadding(@Nonnull Rect padding) {
            int h = (int) Math.ceil(mRadius);
            int v = (int) Math.ceil(mRadius * 0.5f);
            padding.set(h, v, h, v);
            return true;
        }
    }

    /**
     * Waiting for drawable states and directly use View
     */
    private static class NavigationButton extends View {

        private final Image mImage;
        private final int mSrcLeft;

        public NavigationButton(Image image, int srcLeft) {
            mImage = image;
            mSrcLeft = srcLeft;
        }

        @Override
        protected void onDraw(@Nonnull Canvas canvas) {
            Paint paint = Paint.take();
            if (!isHovered())
                paint.setRGBA(192, 192, 192, 255);
            canvas.drawImage(mImage, mSrcLeft, 352, mSrcLeft + 32, 384, 0, 0, getWidth(), getHeight(), paint);
        }

        @Override
        public void onHoverChanged(boolean hovered) {
            super.onHoverChanged(hovered);
            invalidate();
        }
    }
}
