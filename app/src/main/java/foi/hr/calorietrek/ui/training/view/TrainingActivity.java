package foi.hr.calorietrek.ui.training.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private Handler mHandler = new Handler();
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private boolean training = false;
    private int minCargo = 0;
    private int maxCargo = 100;
    private int currentCargo = 20;

    public @BindView(R.id.sbCargoWeight) SeekBar seekBarCargo;
    public @BindView(R.id.cargoKg) TextView cargoKg;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnTrain) Button btnTrain;
    public @BindView(R.id.txtTime) TextView txtTime;
    public @BindView(R.id.btnStop) Button btnStop;

    UserModel userModel;

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
            startActivity(intent);
            //Toast.makeText(TrainingActivity.this, "PROFIL!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            seconds++;
            if(seconds > 59)
            {
                seconds = 0;
                minutes++;
                if(minutes > 59)
                {
                    minutes = 0;
                    hours++;
                }
            }
            txtTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            StartTimer();
        }
    };

    public void StartTimer()
    {
        mHandler.postDelayed(runnable, 1000); //ms
    }

    public void StopTimer()
    {
        mHandler.removeCallbacks(runnable);
    }

    @OnClick(R.id.btnTrain)
    public void onClickBtnTrain()
    {
        if(training == false)
        {
            StartTimer();
            btnTrain.setText(getString(R.string.pause_training));
            btnStop.setVisibility(btnStop.VISIBLE);

            Intent startIntent = new Intent(TrainingActivity.this, ForegroundService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
            training = true;
        }
        else
        {
            StopTimer();
            btnTrain.setText(getString(R.string.resume_training));
            btnStop.setVisibility(btnStop.VISIBLE);
            training = false;
        }
    }
    @OnClick(R.id.btnStop)
    public void onClickBtnStop()
    {
        mHandler.removeCallbacks(runnable);
        btnTrain.setText(getString(R.string.resume_training));
        training = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to finish the training and see the results?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    ResetData();

                    Intent stopIntent = new Intent(TrainingActivity.this, ForegroundService.class);
                    stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    startService(stopIntent);

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
    public void onPause()
    {
        super.onPause();

        SharedPreferences sharedpref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt(getString(R.string.s), seconds);
        editor.putInt(getString(R.string.m), minutes);
        editor.putInt(getString(R.string.h), hours);
        editor.putBoolean(getString(R.string.ongoing_training), training);
        editor.commit();

        Intent playIntent = new Intent(TrainingActivity.this, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        startService(playIntent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        /*
        SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);
        seconds = sp.getInt(getString(R.string.s), seconds);
        minutes = sp.getInt(getString(R.string.m), minutes);
        hours = sp.getInt(getString(R.string.h), hours);
        training = sp.getBoolean(getString(R.string.ongoing_training), training);
        if(training == true)
        {
            mHandler.postDelayed(runnable, 20);
            btnStop.setVisibility(btnStop.VISIBLE);
            btnTrain.setText(getString(R.string.pause_training));
        }
        else
        {
            txtTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            btnTrain.setText(getString(R.string.resume_training));
            btnStop.setVisibility(btnStop.VISIBLE);
        }
        */
    }

    private void ResetData()
    {
        btnTrain.setText(getString(R.string.start_training));
        btnStop.setVisibility(btnStop.INVISIBLE);
        txtTime.setText(getString(R.string.time_format_zero));
        seconds = 0;
        minutes = 0;
        hours = 0;
        training = false;
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
}