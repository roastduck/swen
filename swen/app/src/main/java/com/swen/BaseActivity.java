package com.swen;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        List<MenuItem> list = new ArrayList<>();
        /* 如果要调整顺序或者增删项，要一并改动下面的click回调 */
        list.add(new MenuItem(R.string.my_favorites, R.drawable.favorites, MenuItem.ItemType.TextWithIcon));
        list.add(new MenuItem(R.string.category_mgmt, R.drawable.category_management, MenuItem.ItemType.TextWithIcon));
        list.add(new MenuItem(R.string.no_image_mode, R.drawable.no_image, MenuItem.ItemType.TextWithIconSwitch));
        list.add(new MenuItem(R.string.night_mode, R.drawable.night_mode, MenuItem.ItemType.TextWithIconSwitch));
        list.add(new MenuItem(0, 0, MenuItem.ItemType.Nothing));

        List<News.Category> categoryList = ((ApplicationWithStorage)getApplication()).getCategorySetting().getCategories();
        for (News.Category item : categoryList)
            list.add(new MenuItem(item.getStr(), item.getIcon(), MenuItem.ItemType.TextWithIcon));

        ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent;
                switch (position)
                {
                    case 0:
                        intent = new Intent(BaseActivity.this, FavoritesActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(BaseActivity.this, CategoryFilterActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        if (position > 4)
                        {
                            intent = new Intent(BaseActivity.this, CategoryActivity.class);
                            intent.putExtra("category", categoryList.get(position - 5).getId());
                            startActivity(intent);
                        }
                }
            }
        };

        ListView lv = ((ListView)findViewById(R.id.menu_list_view));
        lv.setAdapter(new MenuItemAdapter(list, this));
        lv.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager =
                (SearchManager)getSystemService(this.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));

        return true;
    }

    private static class MenuItem {
        public enum ItemType {
            Nothing,
            Text,
            TextWithIcon,
            TextWithSwitch,
            TextWithIconSwitch
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

    private static class MenuItemAdapter extends BaseAdapter
    {
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
            tv.setVisibility(View.VISIBLE);
            ImageView iv = (ImageView)convertView.findViewById(R.id.menu_item_icon);
            SwitchCompat sc = (SwitchCompat)convertView.findViewById(R.id.menu_item_switch);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.transparent));

            if (item.itemType != MenuItem.ItemType.Nothing) {
                tv.setText(item.text);
            }
            switch (item.itemType) {
                case Nothing:
                    tv.setVisibility(View.GONE);
                    iv.setVisibility(View.GONE);
                    sc.setVisibility(View.GONE);
                    convertView.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    break;
                case Text:
                    iv.setVisibility(View.GONE);
                    sc.setVisibility(View.GONE);
                    break;
                case TextWithIcon:
                    iv.setImageResource(item.icon);
                    iv.setVisibility(View.VISIBLE);
                    sc.setVisibility(View.GONE);
                    break;
                case TextWithSwitch:
                    iv.setVisibility(View.GONE);
                    sc.setVisibility(View.VISIBLE);
                    break;
                case TextWithIconSwitch:
                    iv.setImageResource(item.icon);
                    iv.setVisibility(View.VISIBLE);
                    sc.setVisibility(View.VISIBLE);
                    break;
            }
            return convertView;
        }
    }
}
