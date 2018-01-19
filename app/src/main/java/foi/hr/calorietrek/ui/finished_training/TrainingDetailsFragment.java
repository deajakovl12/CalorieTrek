package foi.hr.calorietrek.ui.finished_training;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Entity;

import java.security.KeyStore;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.calorie.CalorieCalculus;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.TrainingLocationInfo;
import foi.hr.calorietrek.model.TrainingModel;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.module_navigation.NavigationItem;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;

public class TrainingDetailsFragment extends Fragment implements NavigationItem {
    private String name = "Details Fragment";
    private ArrayList<TrainingModel> allTrainings;

    public @BindView(R.id.chart) CombinedChart chart;
    public @BindView(R.id.txtTime) TextView txtTime;
    public @BindView(R.id.txtKcal) TextView txtKcal;
    public @BindView(R.id.txtAxisX) TextView txtAxisX;
    public @BindView(R.id.txtAxisY) TextView txtAxisY;
    public @BindView(R.id.txtDistance) TextView txtDistance;
    public @BindView(R.id.txtElevation) TextView txtElevation;
    public @BindView(R.id.btnHome) Button btnHome;

    UserModel userModel = null;
    CurrentUser curentUser;
    Location oldLocation;
    float oldDistance = 0;
    float sumCalories = 0;
    int counter = 0;
    long startTime;
    long finishTime;
    float elevation;
    boolean showChart = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        allTrainings = ((FinishedTraining) this.getActivity()).allTrainings;

        View view = inflater.inflate(R.layout.training_details_fragment, container, false);
        ButterKnife.bind(this, view);

        addDataForChart();

        txtKcal.setText(String.format("%.1f", sumCalories));
        txtTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(finishTime-startTime),
               TimeUnit.MILLISECONDS.toMinutes(finishTime-startTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(finishTime-startTime)),
               TimeUnit.MILLISECONDS.toSeconds(finishTime-startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finishTime-startTime))) );
        txtDistance.setText(String.format("%.2f", oldDistance)+"m");
        txtElevation.setText(String.format("%.2f", elevation)+"m");

        return view;

    }

    private void addDataForChart() {
        List<BarEntry> entriesBar = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();

        if(!allTrainings.isEmpty()) {
            for (TrainingModel data : allTrainings) {
                if (data.getLocations().size() > 1) {
                    for (TrainingLocationInfo loc : data.getLocations()) {
                        if (counter == 0) {
                            oldLocation = loc.getLocation();
                            startTime = loc.getTime();
                            counter++;

                        }
                        double altitude = CalorieCalculus.calculateAltitude(loc.getLocation());
                        float altitudeFloat = (float) altitude;

                        double distance = CalorieCalculus.calculateDistance(oldLocation, loc.getLocation());
                        float distanceFloat = (float) distance;

                        double calories = CalorieCalculus.calculateCalories(loc.getLocation(), oldLocation, data.getUserWeight(), loc.getCargoWeight());
                        float caloriesFloat = (float) calories;

                        entriesBar.add(new BarEntry(distanceFloat + oldDistance, caloriesFloat));
                        entries.add(new Entry(distanceFloat + oldDistance, altitudeFloat));
                        oldLocation = loc.getLocation();
                        oldDistance += distanceFloat;
                        sumCalories += caloriesFloat;
                        finishTime = loc.getTime();
                        elevation = altitudeFloat;
                    }
                    setChart(entriesBar, entries);
                    showChart = true;
                }
            }
            if(!showChart){
                txtAxisX.setVisibility(View.INVISIBLE);
                txtAxisY.setVisibility(View.INVISIBLE);
            }
            chart.setNoDataText("Not enough data for graph");
            chart.setNoDataTextColor(Color.rgb(226, 116, 7));
        }
    }

    private void setChart(List<BarEntry> entriesBar, List<Entry> entries ) {

        LineDataSet dataSet = new LineDataSet(entries, "Elevation"); // add entries to dataset
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(Color.rgb(226, 116, 7));
        dataSet.setCircleColor(Color.rgb(226, 116, 7));
        dataSet.setCircleColorHole(Color.rgb(226, 116, 7));
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(Color.rgb(255, 255, 255));
        //lineData.setDrawValues(false);

        BarDataSet barDataSet = new BarDataSet(entriesBar, "Calories");
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setColor(Color.rgb(123, 163, 175));
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);

        CombinedData d = new CombinedData();
        d.setData(lineData);
        d.setData(barData);

        Legend legend = chart.getLegend();
        legend.setTextColor(Color.rgb(255, 255, 255));
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor((Color.rgb(255, 255, 255)));
        xAxis.setGridColor((Color.rgb(29, 51, 90)));
        xAxis.setAxisLineColor((Color.rgb(29, 51, 90)));

        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setTextColor((Color.rgb(255, 255, 255)));
        leftYAxis.setGridColor((Color.rgb(29, 51, 90)));
        leftYAxis.setAxisLineColor((Color.rgb(29, 51, 90)));

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisRight().setAxisMinimum(0);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);

        chart.setData(d);
        chart.invalidate();
    }

    @OnClick(R.id.btnHome)
    public void onClickBtnHome()
    {
        Intent intent = new Intent(getContext(), TrainingActivity.class );
        startActivity(intent);
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
    public android.app.Fragment getFragment() {
        return this;
    }
}
