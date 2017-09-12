package com.swen;

import android.app.SearchManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SearchView;

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

        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem(R.string.category_mgmt, R.drawable.category_management, MenuItem.ItemType.TextWithIcon));
        list.add(new MenuItem(R.string.no_image_mode, R.drawable.no_image, MenuItem.ItemType.TextWithIconSwitch));
        list.add(new MenuItem(R.string.night_mode, R.drawable.night_mode, MenuItem.ItemType.TextWithIconSwitch));
        list.add(new MenuItem(0, 0, MenuItem.ItemType.Nothing));

        // add category
        //
        // for (int k = 1; k <= 12; ++k) {
        //     list.add(new MenuItem(Global.categoryStrs[k], Global.categoryImgs[k], MenuItem.ItemType.TextWithIcon));
        // }

        ((ListView)findViewById(R.id.menu_list_view)).setAdapter(new MenuItemAdapter(list, this));
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
}
