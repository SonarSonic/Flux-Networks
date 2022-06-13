package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.button.PageLabelButton;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * For tabs which have multiple pages: e.g. Network Selection, Network Connections
 */
public abstract class GuiTabPages<T> extends GuiTabCore {

    public final List<T> mElements = new ArrayList<>(); // all elements
    protected final List<T> mCurrent = new ArrayList<>(); // elements of current page
    protected SortType mSortType = SortType.ID; // current sort type
    protected PageLabelButton mLabelButton; // bottom button
    private boolean init;

    public int mPage = 0; // current page, 0-based indexing
    public int mPages = 1;
    public int mGridStartX = 0;
    public int mGridStartY = 0;
    public int mGridHeight = 0; // include padding, must greater than element height
    public int mGridPerPage = 1;
    public int mElementWidth = 0;
    public int mElementHeight = 0;

    public GuiTabPages(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (mPages > 1) {
            mLabelButton.drawButton(poseStack, mouseX, mouseY, deltaTicks);
        }

        for (int i = 0; i < mCurrent.size(); i++) {
            int y = (mGridStartY + mGridHeight * i);
            renderElement(poseStack, mCurrent.get(i), mGridStartX, y);
        }

        T hovered = getHoveredElement(mouseX, mouseY);
        if (hovered != null) {
            renderElementTooltip(poseStack, hovered, mouseX, mouseY);
        }
    }

    @Override
    protected void drawBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
    }

    @Nullable
    public T getHoveredElement(double mouseX, double mouseY) {
        if (mCurrent.isEmpty()) {
            return null;
        }
        for (int i = 0; i < mCurrent.size(); i++) {
            int y = (mGridStartY + mGridHeight * i);
            if (mouseX >= mGridStartX && mouseY >= y && mouseX < mGridStartX + mElementWidth && mouseY < y + mElementHeight) {
                return mCurrent.get(i);
            }
        }
        return null;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        T hovered = getHoveredElement(mouseX, mouseY);
        if (hovered != null) {
            onElementClicked(hovered, mouseButton);
            return true;
        }
        if (mPages > 1 && mLabelButton.isMouseHovered(mouseX, mouseY)) {
            if (mLabelButton.mHoveredPage != -1 && mPage != mLabelButton.mHoveredPage) {
                mPage = mLabelButton.mHoveredPage;
                refreshCurrentPage();
                return true;
            }
        }
        return false;
    }

    protected abstract void onElementClicked(T element, int mouseButton);

    @Override
    public void init() {
        super.init();
        mLabelButton = new PageLabelButton(minecraft, leftPos + 14, topPos + 157, 148, 4,
                mPage, mPages, mNetwork.getNetworkColor());
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double vScroll) {
        if (vScroll < 0 && mPage < mPages - 1) {
            mPage++;
            refreshCurrentPage();
            return true;
        } else if (vScroll > 0 && mPage > 0) {
            mPage--;
            refreshCurrentPage();
            return true;
        }
        return super.onMouseScrolled(mouseX, mouseY, vScroll);
    }

    public abstract void renderElement(PoseStack poseStack, T element, int x, int y);

    public abstract void renderElementTooltip(PoseStack poseStack, T element, int mouseX, int mouseY);

    protected void refreshPages(Collection<T> elements) {
        mElements.clear();
        mElements.addAll(elements);
        mPages = (int) Math.ceil(elements.size() / (double) mGridPerPage);
        sortGrids(mSortType);
        if (!init) {
            refreshCurrentPage();
            init = true;
        } else {
            refreshCurrentPageInternal();
        }
    }

    protected void refreshCurrentPage() {
        refreshCurrentPageInternal();
        mLabelButton.refreshPages(mPage, mPages);
    }

    protected void refreshCurrentPageInternal() {
        if (mElements.isEmpty()) {
            return;
        }

        mCurrent.clear();
        int start = mPage * mGridPerPage;
        int end = Math.min(mElements.size(), start + mGridPerPage);

        for (int i = start; i < end; i++) {
            mCurrent.add(mElements.get(i));
        }
    }

    protected void sortGrids(SortType sortType) {
    }

    public enum SortType {
        ID(FluxTranslate.SORTING_ID),
        NAME(FluxTranslate.SORTING_NAME);

        private final FluxTranslate mName;

        SortType(FluxTranslate name) {
            mName = name;
        }

        @Nonnull
        public String getTranslatedName() {
            return mName.get();
        }
    }
}
