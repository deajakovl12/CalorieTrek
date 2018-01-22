package foi.hr.calorietrek.module_navigation;

import android.app.Fragment;

/*
Interface for modules to implement.
 */
public interface NavigationItem {
    String getItemName();
    Fragment getFragment();
}
