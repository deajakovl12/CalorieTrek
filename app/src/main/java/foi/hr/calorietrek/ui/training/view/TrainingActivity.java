package foi.hr.calorietrek.ui.training.view;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.services.ForegroundService;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;

public class TrainingActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private boolean training = false;

    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnTrain) Button btnTrain;
    public @BindView(R.id.txtTime) TextView txtTime;
    public @BindView(R.id.btnStop) Button btnStop;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        //add
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        userModel = new UserModel(sharedPref.getString("personName",null),sharedPref.getString("personEmail",null),sharedPref.getString("personPhotoUrl",null));
        //-add
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
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
            //remove1
            Intent intent = new Intent(TrainingActivity.this, ProfileActivity.class);
            //remove1
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
            if(hours < 10)
            {
                if(minutes < 10 && seconds < 10)
                {
                    txtTime.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                }
                else if(minutes < 10 && seconds >= 10)
                {
                    txtTime.setText("0" + hours + ":0" + minutes + ":" + seconds);
                }
                else if(minutes >= 10 && seconds < 10)
                {
                    txtTime.setText("0" + hours + ":" + minutes + ":0" + seconds);
                }
                else if(minutes >= 10 && seconds >= 10)
                {
                    txtTime.setText("0" + hours + ":" + minutes + ":" + seconds);
                }
            }
            else
            {
                if(minutes < 10 && seconds < 10)
                {
                    txtTime.setText(hours + ":0" + minutes + ":0" + seconds);
                }
                else if(minutes < 10 && seconds >= 10)
                {
                    txtTime.setText(hours + ":0" + minutes + ":" + seconds);
                }
                else if(minutes >= 10 && seconds < 10)
                {
                    txtTime.setText(hours + ":" + minutes + ":0" + seconds);
                }
                else if(minutes >= 10 && seconds >= 10)
                {
                    txtTime.setText(hours + ":" + minutes + ":" + seconds);
                }
            }

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
            btnTrain.setText("P A U S E");
            btnStop.setVisibility(btnStop.VISIBLE);

            Intent startIntent = new Intent(TrainingActivity.this, ForegroundService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);

            training = true;
        }
        else
        {
            StopTimer();
            btnTrain.setText("R E S U M E");

            btnStop.setVisibility(btnStop.VISIBLE);

            Intent stopIntent = new Intent(TrainingActivity.this, ForegroundService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(stopIntent);

            training = false;
        }
    }
    @OnClick(R.id.btnStop)
    public void onClickBtnStop()
    {
        mHandler.removeCallbacks(runnable);
    }
}