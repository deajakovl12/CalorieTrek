package foi.hr.calorietrek.all_trainings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.calorie.CalorieCalculus;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.TrainingLocationInfo;
import foi.hr.calorietrek.model.TrainingModel;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;

/*
This class was created for activity wich show charts for all trainings. It use instance of DBHelper to find
all information for charts. Library wich was used is: https://github.com/PhilJay/MPAndroidChart
 */

public class AllTrainings extends AppCompatActivity {

    public @BindView(R.id.chartLayout) LinearLayout chartLayout;

    private DbHelper instance;
    private ArrayList<TrainingModel> allTrainings;

    Location oldLocation;
    float oldDistance = 0;
    float sumCalories = 0;
    int counter = 0;
    long startTime;
    long finishTime;
    float elevation = 0;
    boolean showChart = false;
    String trainingName = "Training name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trainings);

        ButterKnife.bind(this);
        instance = DbHelper.getInstance(this);
        allTrainings = new ArrayList<TrainingModel>();

        allTrainings = instance.returnAllTrainings(instance.getUserID(CurrentUser.personEmail));

        addDataForChart();

    }

    private void addDataForChart() {

        if(!allTrainings.isEmpty()) {
            for (TrainingModel data : allTrainings) {
                if(data.getName()!="") {
                    trainingName = data.getName();
                }
                        else{
                    trainingName = "Training name";
                };
                List<BarEntry> entriesBar = new ArrayList<>();
                List<Entry> entries = new ArrayList<>();
                double oldAltitude=-5555;
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
                        double tempGain = altitude-oldAltitude;
                        if(oldAltitude!=-5555 && tempGain > 0.0)
                        {
                            elevation += (float)tempGain;
                        }
                        oldAltitude=altitude;
                    }
                    CombinedChart chart = new CombinedChart(getApplicationContext());
                    setChart(entriesBar, entries, chart);
                    showChart = true;
                    oldDistance = 0;
                    sumCalories = 0;
                    counter = 0;
                    elevation = 0;
                }
            }
        }
    }

    private void setChart(List<BarEntry> entriesBar, List<Entry> entries, CombinedChart chart ) {


        chart.setBackground(getResources().getDrawable(R.drawable.img_graph_background));
        TextView textView = new TextView(getApplicationContext());

        textView.setText(trainingName);

        RelativeLayout rl = new RelativeLayout(getApplicationContext());
        rl.addView(chart);
        rl.addView(textView);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(0, 25, 0, 25);
        rl.setLayoutParams(layoutParams1);
        chartLayout.addView(rl);


        LineDataSet dataSet = new LineDataSet(entries, "Elevation"); // add entries to dataset
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(Color.rgb(226, 116, 7));
        dataSet.setCircleColor(Color.rgb(226, 116, 7));
        dataSet.setCircleRadius(1);
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
        chart.setExtraRightOffset(50f);
        chart.setExtraBottomOffset(10f);
        chart.setExtraTopOffset(5f);
        chart.setTouchEnabled(false);

        chart.setData(d);
        chart.invalidate();
    }

    @OnClick(R.id.btnHome)
    public void onClickBtnHome()
    {
        Intent intent = new Intent(getApplicationContext(), TrainingActivity.class );
        startActivity(intent);
    }

}
