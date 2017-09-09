package com.swen;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class MenuItem {
    public enum ItemType {
        Text,
        TextWithIcon,
        TextWithSwitch
    }

    public int text;
    public int icon;
    public ItemType itemType;

    public MenuItem(int text, int icon, ItemType itemType) {
        this.text = text;
        this.icon = icon;
        this.itemType = itemType;
    }
}

class MenuItemAdapter extends BaseAdapter {
    private List<MenuItem> data;
    private Context context;
    private LayoutInflater inflater;

    public MenuItemAdapter(List<MenuItem> data, Context context) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_item, null);
        }
        MenuItem item = (MenuItem)getItem(position);
        TextView tv = (TextView)convertView.findViewById(R.id.menu_item_text);
        ImageView iv = (ImageView)convertView.findViewById(R.id.menu_item_icon);
        SwitchCompat sc = (SwitchCompat)convertView.findViewById(R.id.menu_item_switch);

        tv.setText(item.text);
        switch (item.itemType) {
            case Text:
                iv.setVisibility(View.GONE);
                sc.setVisibility(View.GONE);
                break;
            case TextWithIcon:
                sc.setVisibility(View.GONE);
                break;
            case TextWithSwitch:
                iv.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }
}