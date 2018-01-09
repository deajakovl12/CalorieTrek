package foi.hr.calorietrek.module_navigation;

import android.app.Fragment;

public interface NavigationItem {
    public String getItemName();
    public Fragment getFragment();
}
