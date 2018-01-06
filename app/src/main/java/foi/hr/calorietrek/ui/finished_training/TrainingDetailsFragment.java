package foi.hr.calorietrek.ui.finished_training;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foi.hr.calorietrek.R;
import foi.hr.calorietrek.module_navigation.NavigationItem;
import foi.hr.calorietrek.module_navigation.ReadyForDataListener;

public class TrainingDetailsFragment extends Fragment implements NavigationItem {

    private int position;
    private String name = "Details Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public String getItemName() {
        return name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public android.app.Fragment getFragment() {
        return null;
    }

    @Override
    public void setReadyForDataListener(ReadyForDataListener readyForDataListener) {

    }

    @Override
    public void loadData() {

    }
}
