package ru.ertel.remotecontrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<String> devicesList;

    public DeviceAdapter(Context context, List<String> devicesList) {
        this.context = context;
        this.devicesList = devicesList;
    }

    @Override
    public int getCount() {
        return devicesList != null ? devicesList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.device_item, parent, false);
        TextView txtName = rootView.findViewById(R.id.name);

        txtName.setText(devicesList.get(position));
        return rootView;
    }
}
