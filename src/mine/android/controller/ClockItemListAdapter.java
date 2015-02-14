package mine.android.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import mine.android.HeavenClock.MainActivity;
import mine.android.HeavenClock.R;
import mine.android.modules.ClockItem;

import java.util.Date;
import java.util.List;

/**
 * Created by Heaven on 2015/2/14.
 */
public class ClockItemListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ClockItem> mData;

    public final class ListItemView {
        TextView itemTitle;
        TextView itemText;
        Switch itemSwitch;
    }

    private ClockItemListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public static ClockItemListAdapter make(Context context, List<ClockItem> mData) {
        ClockItemListAdapter ret = new ClockItemListAdapter(context);
        ret.mData = mData;
        return ret;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ListItemView item;
        if (convertView == null) {
            item = new ListItemView();
            convertView = mInflater.inflate(R.layout.list_item, null);
            item.itemText = (TextView) convertView.findViewById(R.id.ItemText);
            item.itemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            item.itemSwitch = (Switch) convertView.findViewById(R.id.itemSwitch);
            convertView.setTag(item);
        } else {
            item = (ListItemView) convertView.getTag();
        }

        final ClockItem clockItem = mData.get(position);

        //TODO 显示 每天 每周
        Date time = clockItem.getTime();
        item.itemTitle.setText(time.getHours() + ":" + time.getMinutes() + ":00");

        String description = clockItem.getDescription();
        item.itemText.setText(description != null ? description : MainActivity.getContext().getResources().getString(R.string.no_description));

        item.itemSwitch.setChecked(clockItem.isActivated());
        item.itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ClockCtrl.setClockItemEnable(clockItem, isChecked);
            }
        });

        return convertView;
    }
}
