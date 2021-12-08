package sonar.fluxnetworks.client.widget;

import icyllis.modernui.view.View;
import icyllis.modernui.widget.CompoundButton;
import icyllis.modernui.widget.RelativeLayout;

import javax.annotation.Nullable;

// may be replaced by the core widget in future
public class RelativeRadioGroup extends RelativeLayout {

    // holds the checked id; the selection is empty by default
    private int mCheckedId = NO_ID;
    // tracks children radio buttons checked state
    private final CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener = new CheckedStateTracker();
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    @Nullable
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public RelativeRadioGroup() {
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof CompoundButton button) {
            if (button.isChecked()) {
                setCheckedStateForView(mCheckedId, false);
                setCheckedId(button.getId());
            }

            button.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof CompoundButton button) {
            button.setOnCheckedChangeListener(null);
        }
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #getCheckedId()
     * @see #clearCheck()
     */
    public void check(int id) {
        // don't even bother
        if (id != NO_ID && (id == mCheckedId)) {
            return;
        }
        setCheckedStateForView(mCheckedId, false);
        setCheckedStateForView(id, true);

        setCheckedId(id);
    }

    private void setCheckedId(int id) {
        mCheckedId = id;

        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        if (viewId == NO_ID) {
            return;
        }
        if (findViewById(viewId) instanceof CompoundButton button) {
            // Modern UI: always do this without triggering the child listener, only changing its state.
            mProtectFromCheckedChange = true;
            button.setChecked(checked);
            mProtectFromCheckedChange = false;
        }
    }

    /**
     * <p>Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is {@link #NO_ID}.</p>
     *
     * @return the unique id of the selected radio button in this group
     * @see #check(int)
     * @see #clearCheck()
     */
    public int getCheckedId() {
        return mCheckedId;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedId()
     */
    public void clearCheck() {
        check(NO_ID);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    @FunctionalInterface
    public interface OnCheckedChangeListener {

        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is {@link #NO_ID}.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(RelativeRadioGroup group, int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion from this parent view
            if (!mProtectFromCheckedChange) {
                setCheckedStateForView(mCheckedId, false);
                setCheckedId(buttonView.getId());
            }
        }
    }
}
