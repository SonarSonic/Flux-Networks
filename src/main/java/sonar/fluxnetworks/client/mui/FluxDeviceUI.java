package sonar.fluxnetworks.client.mui;

import icyllis.modernui.animation.LayoutTransition;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.*;
import icyllis.modernui.graphics.Image;
import icyllis.modernui.graphics.drawable.ImageDrawable;
import icyllis.modernui.util.*;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.client.design.FluxDesign;
import sonar.fluxnetworks.client.design.TabBackground;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static icyllis.modernui.view.ViewGroup.LayoutParams.*;

public class FluxDeviceUI extends Fragment implements FluxMenu.OnResultListener {

    public static final int NETWORK_COLOR = 0xFF295E8A;

    public static final int id_tab_container = 0x0002;

    public static Image sButtonIcon;

    private final TileFluxDevice mDevice;

    private static final ColorStateList NAV_BUTTON_COLOR = new ColorStateList(
            new int[][]{
                    StateSet.get(StateSet.VIEW_STATE_SELECTED),
                    StateSet.get(StateSet.VIEW_STATE_HOVERED),
                    StateSet.WILD_CARD},
            new int[]{
                    FluxDesign.WHITE,
                    0xFFE0E0E0,
                    FluxDesign.LIGHT_GRAY}
    );

    public FluxDeviceUI(@Nonnull TileFluxDevice device) {
        mDevice = device;
    }

    @Override
    public void onResult(FluxMenu menu, int key, int code) {
        DataSet result = new DataSet();
        result.putInt("code", code);
        requireView().post(() -> getChildFragmentManager().setFragmentResult(switch (key) {
            case FluxConstants.REQUEST_CREATE_NETWORK -> "create_network";
            default -> "";
        }, result));
    }

    @Override
    public void onAttach(@Nonnull Context context) {
        super.onAttach(context);
        getParentFragmentManager().beginTransaction()
                .setPrimaryNavigationFragment(this)
                .commit();
    }

    @Override
    public void onCreate(@Nullable DataSet savedInstanceState) {
        super.onCreate(savedInstanceState);
        sButtonIcon = Image.create("modernui", "gui/gui_icon.png");

        var fragment = new DeviceHomeTab(mDevice);
        fragment.setArguments(getArguments());
        getChildFragmentManager().beginTransaction()
                .replace(id_tab_container, fragment, "home")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .commit();
        getChildFragmentManager().addOnBackStackChangedListener(() -> {
            View view = requireView().findViewById(id_tab_container);
            if (view.getBackground() instanceof TabBackground bg) {
                bg.setColor(NETWORK_COLOR);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        var content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);

        var buttonGroup = new LinearLayout(requireContext());
        buttonGroup.setOrientation(LinearLayout.HORIZONTAL);
        buttonGroup.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        buttonGroup.setLayoutTransition(new LayoutTransition());

        int buttonSize = content.dp(32);
        for (int i = 0; i < 8; i++) {
            var button = new ImageButton(requireContext());
            var drawable = new ImageDrawable(sButtonIcon);
            if (i == 0) {
                drawable.setSrcRect(2, 258, 66, 322);
            } else {
                drawable.setSrcRect(i * 32, 352, (i + 1) * 32, 384);
            }
            drawable.setTintList(NAV_BUTTON_COLOR);
            button.setImageDrawable(drawable);

            var params = new LinearLayout.LayoutParams(buttonSize, buttonSize);
            params.setMarginsRelative(content.dp(i == 7 ? 26 : 2), content.dp(2), content.dp(2), content.dp(6));
            if (i == 0 || i == 7) {
                buttonGroup.addView(button, params);
            } else {
                int index = i;
                content.postDelayed(() -> buttonGroup.addView(button, index, params), i * 50);
            }
            if (i == 7) {
                button.setOnClickListener(__ -> {
                    FragmentManager fm = getChildFragmentManager();
                    Fragment fragment = fm.findFragmentByTag("create");
                    if (fragment == null) {
                        fm.beginTransaction()
                                .replace(id_tab_container, CreateTab.class, getArguments(), "create")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();
                    } else {
                        fm.beginTransaction()
                                .replace(id_tab_container, fragment, "create")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .setReorderingAllowed(true)
                                .commit();
                    }
                    //__.setSelected(true);
                });
            } else if (i == 0) {
                button.setSelected(true);
                button.setOnClickListener(__ -> getChildFragmentManager().popBackStack());
            }
        }

        content.addView(buttonGroup, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        var tabContainer = new FragmentContainerView(requireContext());
        tabContainer.setBackground(new TabBackground(content));
        tabContainer.setId(id_tab_container);

        int tabSize = content.dp(340);
        content.addView(tabContainer, new LinearLayout.LayoutParams(tabSize, tabSize));

        content.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));
        return content;
    }
}
