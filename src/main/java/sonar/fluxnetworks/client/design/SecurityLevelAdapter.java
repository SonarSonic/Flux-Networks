package sonar.fluxnetworks.client.design;

import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.view.ViewGroup.LayoutParams;
import icyllis.modernui.widget.BaseAdapter;
import icyllis.modernui.widget.TextView;
import sonar.fluxnetworks.api.network.SecurityLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SecurityLevelAdapter extends BaseAdapter {

    public SecurityLevelAdapter() {
    }

    @Override
    public int getCount() {
        return SecurityLevel.size();
    }

    @Override
    public SecurityLevel getItem(int position) {
        return SecurityLevel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @Nonnull ViewGroup parent) {
        final TextView view;

        if (convertView == null) {
            view = new TextView();
        } else {
            view = (TextView) convertView;
        }

        view.setText(getItem(position).getText());

        view.setTextSize(16);
        view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        final int dp4 = View.dp(4);
        view.setPadding(dp4, dp4, dp4, dp4);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);

        return view;
    }
}
