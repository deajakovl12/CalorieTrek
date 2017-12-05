package foi.hr.calorietrek.ui.training.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.services.ForegroundService;
import foi.hr.calorietrek.ui.finished_training.FinishedTraining;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;

public class TrainingActivity extends AppCompatActivity {

    private boolean training = false;
    private boolean timer = false;
    private int minCargo = 0;
    private int maxCargo = 100;
    private int currentCargo = 20;

    public @BindView(R.id.sbCargoWeight) SeekBar seekBarCargo;
    public @BindView(R.id.cargoKg) TextView cargoKg;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnTrain) Button btnTrain;
    public @BindView(R.id.txtTime) TextView txtTime;
    public @BindView(R.id.txtDistance) TextView txtDistance;
    public @BindView(R.id.txtElevation) TextView txtElevation;
    public @BindView(R.id.btnStop) Button btnStop;

    UserModel userModel;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        userModel = new UserModel(sharedPref.getString("personName",null),sharedPref.getString("personEmail",null),sharedPref.getString("personPhotoUrl",null));
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        seekbarCargoProgress();
        btnStop.setVisibility(btnStop.INVISIBLE);
    }

    public void startMeasuring()
    {
        Intent startIntentTimer = new Intent(TrainingActivity.this, ForegroundService.class);
        startIntentTimer.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntentTimer);

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
                txtDistance.setText(Float.toString(distance));
                double elevationGain = intent.getDoubleExtra("elevationGainInMeters",0);
                txtElevation.setText(Double.toString(elevationGain));
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

    private void Pause()
    {
        btnTrain.setText(getString(R.string.resume_training));
        timer = false;
        Intent pauseIntent = new Intent(TrainingActivity.this, ForegroundService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        startService(pauseIntent);
    }

    private void Resume()
    {
        btnTrain.setText(getString(R.string.pause_training));
        btnStop.setVisibility(btnStop.VISIBLE);
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
            startMeasuring();
        }
        if(timer == false)
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
        builder.setMessage(R.string.training_stopped).setPositiveButton(R.string.positive_answer, dialogClickListener)
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
        txtTime.setText(getString(R.string.time_format_zero));
        timer = false;
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
}