package foi.hr.calorietrek.module_navigation;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;

import foi.hr.calorietrek.R;

public class NavigationManager {

    private static NavigationManager instance;
    public ArrayList<NavigationItem> navigationItems;
    private Activity handlerActivity;
    private Toolbar toolbar;
    SubMenu submenu;

    private NavigationManager() {
        navigationItems = new ArrayList<NavigationItem>();
    }

    public static NavigationManager getInstance() {
        if (instance == null)
            instance = new NavigationManager();
        return instance;
    }

    public void setDependencies(Activity handlerActivity, Toolbar toolbar) {
        this.handlerActivity = handlerActivity;
        this.toolbar = toolbar;
    }

    public void addItem(NavigationItem newItem) {
        submenu = toolbar.getMenu().getItem(2).getSubMenu();
        navigationItems.add(newItem);
        submenu.add(newItem.getItemName());
    }

    public void selectNavigationItem(final MenuItem menuItem) {
        NavigationItem clickedItem = null;
        for(NavigationItem navItem : navigationItems) {
            if(menuItem.getTitle() == navItem.getItemName())
                clickedItem = navItem;
        }
        //NavigationItem clickedItem = navigationItems.get(menuItem.getItemId());

        FragmentManager fragmentManager = handlerActivity.getFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, clickedItem.getFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}