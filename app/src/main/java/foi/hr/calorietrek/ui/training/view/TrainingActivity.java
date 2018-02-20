package foi.hr.calorietrek.ui.training.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.location.FusedLocationProvider;
import foi.hr.calorietrek.dialog.dialog_input_weight.view.DialogInputWeight;
import foi.hr.calorietrek.dialog.dialog_input_weight.view.IDialogInputWeight;
import foi.hr.calorietrek.dialog.dialog_welcome.DialogWelcome;
import foi.hr.calorietrek.dialog.dialog_welcome.IDialogWelcome;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.services.ForegroundService;
import foi.hr.calorietrek.ui.finished_training.FinishedTraining;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;
/*
Activity class which shows the training screen. This class interacts with user inputs. Training can be started which starts foreground service, paused which is pausing all data updates
and stopped which stops data updates.
 */
public class TrainingActivity extends AppCompatActivity implements DialogInputWeight.DialogInputWeightListener{

    private boolean training = false;
    private boolean timer = false;
    private boolean locationPermission=false;
    private int minCargo = 0;
    private int maxCargo = 100;
    public int currentCargo = 20;
    private int weight = 0;
    public int MY_PERMISSIONS_REQUEST_LOCATION=101;

    public @BindView(R.id.sbCargoWeight) SeekBar seekBarCargo;
    public @BindView(R.id.cargoKg) TextView cargoKg;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnTrain) Button btnTrain;
    public @BindView(R.id.txtTime) TextView txtTime;
    public @BindView(R.id.txtDistance) TextView txtDistance;
    public @BindView(R.id.txtElevation) TextView txtElevation;
    public @BindView(R.id.btnStop) Button btnStop;
    public @BindView(R.id.txtKcal) TextView txtKcal;
    public @BindView(R.id.imgPause) ImageView imgPause;
    public @BindView(R.id.imgResume) ImageView imgResume;
    public @BindView(R.id.imgStop) ImageView imgStop;

    UserModel userModel;
    BroadcastReceiver broadcastReceiver = null;
    DbHelper instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Intent intentDialog = getIntent();
        boolean response = intentDialog.getExtras().getBoolean("userExist");
        if (!response){
            dialogInput();
            dialogWelcome();
        }

        loadWeight();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        userModel = new UserModel(sharedPref.getString("personName",null),sharedPref.getString("personEmail",null),sharedPref.getString("personPhotoUrl",null));
        ButterKnife.bind(this);
        locationPermission = checkLocationPermissions();
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        seekbarCargoProgress();
        btnStop.setVisibility(btnStop.INVISIBLE);
    }

    private void loadWeight()
    {
        instance = DbHelper.getInstance(this);
        String result = instance.returnWeight(CurrentUser.personName);
        try {
            weight = Integer.parseInt(result);
        }
        catch (NumberFormatException e){
            weight = 55;
        }
    }

    public void startMeasuring()
    {
        btnTrain.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_button));
        imgPause.setVisibility(View.VISIBLE);
        imgStop.setVisibility(View.VISIBLE);
        loadData();
        Intent startIntent = new Intent(TrainingActivity.this, ForegroundService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntent);

        registerBroadCastReceiver();

        training = true;
    }

    private void registerBroadCastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                long timeInMilliseconds = intent.getLongExtra("timeInMilliseconds", 0);
                txtTime.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeInMilliseconds),
                                                                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMilliseconds)),
                                                                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds))));
                float distance= intent.getFloatExtra("distanceInMeters",0);
                txtDistance.setText(String.format("%.2f", distance)+"m");
                double elevationGain = intent.getDoubleExtra("elevationGainInMeters",0);
                txtElevation.setText(String.format("%.2f", elevationGain)+"m");
                double calorie = intent.getDoubleExtra("calories", 0);
                txtKcal.setText(String.format("%.1f", calorie));
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION.BROADCAST_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menubar_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_profile){
            Intent intent = new Intent(TrainingActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            //Toast.makeText(TrainingActivity.this, "PROFIL!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void turnOnLocation(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gps_dialog_title));
        builder.setMessage(getString(R.string.gps_dialog_message));
        builder.setPositiveButton(getString(R.string.gps_positive_answer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,1);
            }
        }).setNegativeButton(getString(R.string.gps_negative_answer), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        android.support.v7.app.AlertDialog mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    private void Pause()
    {
        btnTrain.setBackground(getResources().getDrawable(R.drawable.bg_button_green));
        btnTrain.setText(getString(R.string.resume_training));
        imgPause.setVisibility(View.INVISIBLE);
        imgResume.setVisibility(View.VISIBLE);

        timer = false;
        Intent pauseIntent = new Intent(TrainingActivity.this, ForegroundService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        startService(pauseIntent);
    }

    private void Resume()
    {
        btnTrain.setBackground(getResources().getDrawable(R.drawable.bg_light_blue_button));
        btnTrain.setText(getString(R.string.pause_training));
        btnStop.setVisibility(btnStop.VISIBLE);
        imgPause.setVisibility(View.VISIBLE);
        imgResume.setVisibility(View.INVISIBLE);

        timer = true;
        Intent playIntent = new Intent(TrainingActivity.this, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        startService(playIntent);
    }

    private void Stop()
    {
        Intent stopIntent = new Intent(TrainingActivity.this, ForegroundService.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(stopIntent);

        ResetData();

        training = false;
    }

    @OnClick(R.id.btnTrain)
    public void onClickBtnTrain()
    {
        if(training == false)
        {
            if(locationPermission==false)askLocationPermissions();
            if(!FusedLocationProvider.isLocationEnabled(this))turnOnLocation();
            else startMeasuring();
        }
        if(timer == false && training == true)
        {
            Resume();
        }
        else
        {
            Pause();
        }
    }
    @OnClick(R.id.btnStop)
    public void onClickBtnStop()
    {
        Pause();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.training_stopped)
                .setPositiveButton(R.string.positive_answer, dialogClickListener)
                .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        switch(which)
        {
            case DialogInterface.BUTTON_POSITIVE:
                Stop();
                Intent intent = new Intent(TrainingActivity.this, FinishedTraining.class);
                intent.putExtra("ALL_TRAININGS", "false");
                startActivity(intent);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
        }
    };

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    private void ResetData()
    {
        btnTrain.setText(getString(R.string.start_training));
        btnStop.setVisibility(btnStop.INVISIBLE);
        imgPause.setVisibility(View.INVISIBLE);
        imgStop.setVisibility(View.INVISIBLE);
        imgResume.setVisibility(View.INVISIBLE);
        txtTime.setText(getString(R.string.time_format_zero));
        txtDistance.setText(getString(R.string.distance_zero));
        txtElevation.setText(getString(R.string.elevation_zero));
        txtKcal.setText(getString(R.string.calorie_zero));
        timer = false;
    }
    private void loadData()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("cargoWeight", currentCargo);
        editor.putInt("userWeight", weight);
        editor.apply();
    }

    public void seekbarCargoProgress (){
        seekBarCargo.setProgress(currentCargo);
        seekBarCargo.setMax(maxCargo);
        cargoKg.setText(String.format("%2d kg", currentCargo));

        seekBarCargo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                currentCargo = progress + minCargo;
                cargoKg.setText(String.format("%2d kg", currentCargo));
                loadData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
            Intent stopIntent = new Intent(TrainingActivity.this, ForegroundService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(stopIntent);
        }
    }

    private void dialogWelcome(){
        DialogWelcome dialogWelcome = IDialogWelcome.dialogWelcome;
        dialogWelcome.show(getSupportFragmentManager(), "dialogWelcome");
    }

    private void dialogInput(){
        DialogInputWeight dialogInputWeight = IDialogInputWeight.dialogInputWeight;
        dialogInputWeight.show(getSupportFragmentManager(), "dialogWeight");
    }

    @Override
    public void applyCargo(int cargo) {
        currentCargo = cargo;
        cargoKg.setText(String.format("%2d kg", currentCargo));
        seekBarCargo.setProgress(currentCargo);
    }

    public boolean checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast toast = Toast.makeText(this, R.string.location_permissions_not_granted, Toast.LENGTH_SHORT);
                toast.show();
            }
            return false;
        }
        else{
            return true;
        }
    }
    public void askLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }
}