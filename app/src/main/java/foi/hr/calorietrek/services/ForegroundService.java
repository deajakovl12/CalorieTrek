package foi.hr.calorietrek.services;

/**
 * Created by juras on 2017-11-30.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import foi.hr.calorietrek.R;
import foi.hr.calorietrek.calorie.CalorieCalculus;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.location.Altitude;
import foi.hr.calorietrek.location.FusedLocationProvider;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;


/*
This class is used as a foreground service in CalorieTrek application. This service turns on when user starts new training. When turned on, training continues to run in background without interruption.
After start, timer starts ticking, location starts giving updates and calories are being calculated. When user stops training, service gathers final data, removes callbacks on runnables and sends locationProvider
signal do stop giving location updates.
*/
public class ForegroundService extends Service {

    private Handler trainingHandler = new Handler();
    private Handler locationHandler = new Handler();
    private int delay = 3750;
    private Runnable locationRunnable;
    private Intent intent;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long pausedTime = 0L;
    private long updateTime = 0L;
    private boolean stopTimer = false;
    private FusedLocationProvider fusedLocationProvider = null;
    private Location oldLocation = null;
    private Location currentLocation = null;
    private float distance = 0;
    private double elevationGain=0;
    private double oldAltitude =55555;
    private double currentAltitude=55555;
    private double calories = 0;
    private int userWeight = 0;
    private int cargoWeight = 0;
    private int userID ;
    private long trainingID;
    DbHelper instance;
    Altitude altitude;
    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(Constants.ACTION.BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            fusedLocationProvider = new FusedLocationProvider(Constants.GPSPARAMETERS.UPDATE_INTERVAL,Constants.GPSPARAMETERS.FASTEST_UPDATE_INTERVAL,Constants.GPSPARAMETERS.ACCURACY,this);
            loadData();
            startTime = SystemClock.uptimeMillis();
            trainingHandler.postDelayed(updateTimerThread, 0);
            locationHandler.postDelayed(updateLocation, delay);
            Intent notificationIntent = new Intent(this, TrainingActivity.class);

            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.cklogo);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.training_started))
                    .setSmallIcon(R.drawable.cklogo)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            altitude = new Altitude(this);
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            //delete
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String personEmail = sharedPref.getString("personEmail","not Available");
            instance = DbHelper.getInstance(getApplicationContext());
            userID = instance.getUserID(personEmail);
            trainingID = instance.insertTraining(userID,userWeight);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault());
            Date date = new Date();
            instance.updateTrainingDate(trainingID, simpleDateFormat.format(date));


        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            if(fusedLocationProvider!=null) fusedLocationProvider.startLocationUpdates();
            if(altitude!=null)altitude.onResume();
            loadData();
            startTime = SystemClock.uptimeMillis();
            trainingHandler.postDelayed(updateTimerThread, 0);
            locationHandler.postDelayed(locationRunnable,delay);
        } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {
            if(fusedLocationProvider!=null)fusedLocationProvider.stopLocationUpdates();
            if(altitude!=null)altitude.onPause();
            pausedTime += timeInMilliseconds;
            trainingHandler.removeCallbacks(updateTimerThread);
            locationHandler.removeCallbacks(locationRunnable);
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            if(fusedLocationProvider!=null) {
                fusedLocationProvider.stopLocationUpdates();
            }
            if(altitude!=null)altitude.onPause();
            if(currentLocation!=null)instance.insertLocation(trainingID,currentLocation,cargoWeight);
            //if(altitude!=null && altitude.isPressureSensorAvailable()){instance.updateTraining(trainingID,elevationGainBarometer,distance,calories, updateTime);}
            //else
            if(instance!=null){instance.updateTraining(trainingID,elevationGain,distance,calories, updateTime);}
            currentLocation=null;
            oldLocation=null;
            timeInMilliseconds = 0L;
            startTime = 0L;
            pausedTime = 0L;
            updateTime = 0L;

            distance = 0;
            elevationGain = 0;
            calories = 0;
            stopTimer = true;
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = pausedTime + timeInMilliseconds;
            intent.putExtra("timeInMilliseconds", updateTime);
            intent.putExtra("distanceInMeters",distance);
            intent.putExtra("elevationGainInMeters",elevationGain);
            intent.putExtra("calories", calories);
            sendBroadcast(intent);
            if (!stopTimer) {
                trainingHandler.postDelayed(this, 0);
            }
        }
    };

    private Runnable updateLocation = new Runnable() {
        public void run() {
            if(fusedLocationProvider.GetLocation()!=null && fusedLocationProvider.GetLocation().getAltitude()!=0) {
                oldLocation = currentLocation;
                currentLocation = fusedLocationProvider.GetLocation();
            }
            if(currentLocation!=null&&altitude.isPressureSensorAvailable()&&altitude.isAltitudeAvailable())
            {
                Log.e( "imHere: ",""+altitude.getAltitude());
                currentLocation.setAltitude(altitude.getAltitude());
                oldAltitude=currentAltitude;
                currentAltitude=altitude.getAltitude();
            }
            if(CalorieCalculus.isLocationAccurateEnough(oldLocation,currentLocation,cargoWeight)) {
                if(oldLocation.getLatitude()!=currentLocation.getLatitude()||oldLocation.getLongitude()!=currentLocation.getLongitude()) {
                    instance.insertLocation(trainingID, oldLocation, cargoWeight);
                    distance += CalorieCalculus.calculateDistance(oldLocation, currentLocation);
                    double elevGain = CalorieCalculus.calculateElevationGain(currentLocation, oldLocation);
                    if (elevGain > 0.0) {
                        elevationGain += elevGain;
                    }
                    calories += CalorieCalculus.calculateCalories(currentLocation, oldLocation, userWeight, cargoWeight);
                }
            }
            locationRunnable=this;
            loadData();

            locationHandler.postDelayed(locationRunnable, delay);
        }
    };

    private void loadData()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        userWeight = sp.getInt("userWeight", 0);
        cargoWeight = sp.getInt("cargoWeight", 0);
    }
    @Override
    public void onDestroy()
    {

        trainingHandler.removeCallbacks(updateTimerThread);
        locationHandler.removeCallbacks(locationRunnable);
        if(fusedLocationProvider!=null) {
            fusedLocationProvider.stopLocationUpdates();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}