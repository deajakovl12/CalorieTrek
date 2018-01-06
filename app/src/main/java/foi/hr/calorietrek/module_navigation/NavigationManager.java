package foi.hr.calorietrek.module_navigation;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.SubMenu;

import java.util.ArrayList;

public class NavigationManager {

    private static NavigationManager instance;
    public ArrayList<NavigationItem> navigationItems;
    private Activity handlerActivity;
    private Toolbar toolbar;
    SubMenu submenu;

    private NavigationManager(){
        navigationItems = new ArrayList<NavigationItem>();
    }

    public static NavigationManager getInstance(){
        if(instance == null)
            instance = new NavigationManager();
        return instance;
    }

    public void setDependencies(Activity handlerActivity, Toolbar toolbar) {
        this.handlerActivity = handlerActivity;
        this.toolbar = toolbar;
    }

    public void addItem(NavigationItem newItem) {
        submenu = toolbar.getMenu().getItem(2).getSubMenu();
        newItem.setPosition(navigationItems.size());
        navigationItems.add(newItem);
        submenu.add(newItem.getItemName());
    }
}
