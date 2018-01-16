package foi.hr.calorietrek.module_navigation;

import android.app.Fragment;

public interface NavigationItem {
    String getItemName();
    Fragment getFragment();
}
