package com.lt3d.tools;

import android.content.Context;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class SortActionProvider extends ActionProvider {
    public SortActionProvider(Context context){
        super(context);
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    public void onPrepareSubMenu(SubMenu subMenu){
        subMenu.clear();
        subMenu.add("A to Z").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        subMenu.add("Z to A").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
