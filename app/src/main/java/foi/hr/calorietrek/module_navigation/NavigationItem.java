package foi.hr.calorietrek.module_navigation;

import android.app.Fragment;

public interface NavigationItem {
    public String getItemName();
    public int getPosition();
    public void setPosition(int position);
    public Fragment getFragment();
    public void setReadyForDataListener(ReadyForDataListener readyForDataListener);
    public void loadData();
}
