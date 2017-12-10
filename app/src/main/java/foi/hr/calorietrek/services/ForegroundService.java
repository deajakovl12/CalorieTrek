package foi.hr.calorietrek.services;

/**
 * Created by juras on 2017-11-30.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import foi.hr.calorietrek.R;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.location.Altitude;
import foi.hr.calorietrek.location.FusedLocationProvider;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;
import static java.lang.Math.abs;

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
            if(altitude.isPressureSensorAvailable()&&altitude.isAltitudeAvailable()&&oldAltitude!=55555 &&currentAltitude!=55555)intent.putExtra("elevationGainInMeters",currentAltitude-oldAltitude);
            else intent.putExtra("elevationGainInMeters",elevationGain);
            intent.putExtra("calories", calories);
            sendBroadcast(intent);
            if (!stopTimer) {
                trainingHandler.postDelayed(this, 0);
            }
        }
    };

    private Runnable updateLocation = new Runnable() {
        public void run() {
            oldLocation = currentLocation;
            currentLocation = fusedLocationProvider.GetLocation();
            if(currentLocation!=null&&altitude.isPressureSensorAvailable()&&altitude.isAltitudeAvailable())
            {
                currentLocation.setAltitude(altitude.getAltitude());
                oldAltitude=currentAltitude;
                currentAltitude=altitude.getAltitude();
            }
            if(isLocationAccurateEnough(oldLocation,currentLocation)) {
                calculateDistance();
                calculateElevationGain();
                calculateCalories();
            }
            locationRunnable=this;
            loadData();

            locationHandler.postDelayed(locationRunnable, delay);
        }
    };


    private  void calculateDistance() {
        distance+=oldLocation.distanceTo(currentLocation);
    }
    private  void calculateElevationGain() {
        elevationGain = currentLocation.getAltitude()-oldLocation.getAltitude();
    }

    private double calculateCoefficient()
    {
        double slope=(currentLocation.getAltitude() - oldLocation.getAltitude()) / oldLocation.distanceTo(currentLocation);
        for(int i=0;i<Constants.CALORIES.SLOPE_COEFFICIENT.length-1;i++) {
            if(slope>=Constants.CALORIES.SLOPE_COEFFICIENT[i][0] && slope<=Constants.CALORIES.SLOPE_COEFFICIENT[i+1][0])
            {
                if(slope<=0 && abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i][0]))<abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i+1][0])))
                {return Constants.CALORIES.SLOPE_COEFFICIENT[i][1];}
                else if(slope<=0 && abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i][0]))>abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i+1][0])))
                {return Constants.CALORIES.SLOPE_COEFFICIENT[i+1][1];}
                else if(slope>=0 && abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i][0]))>abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i+1][0])))
                {return Constants.CALORIES.SLOPE_COEFFICIENT[i+1][1];}
                else if(slope>=0 && abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i][0]))<abs(abs(slope)-abs(Constants.CALORIES.SLOPE_COEFFICIENT[i+1][0])))
                {return Constants.CALORIES.SLOPE_COEFFICIENT[i][1];}
            }
        }
        if(slope<Constants.CALORIES.SLOPE_COEFFICIENT[0][0])return Constants.CALORIES.SLOPE_COEFFICIENT[0][1];
        else return Constants.CALORIES.SLOPE_COEFFICIENT[Constants.CALORIES.SLOPE_COEFFICIENT.length-1][1];
    }

    private void calculateCalories()
    {
        if(currentLocation != null && oldLocation != null && oldLocation.distanceTo(currentLocation) != 0)
        {
            double coefficient = calculateCoefficient();
            calories += ((coefficient * (userWeight + cargoWeight) * oldLocation.distanceTo(currentLocation)) / Constants.CALORIES.JOULES);
        }
    }

    //tests if speed is in good range for walking/running human
    private boolean isLocationAccurateEnough(Location oldLocation, Location currentLocation)
    {
        if(oldLocation!=null && currentLocation!=null && !oldLocation.equals(currentLocation))
        {
            //time is in miliseconds
            double time = currentLocation.getTime()-oldLocation.getTime();
            //distance is in meters with deducted regular GPS error range to allow test to pass if it was gps error
            double distance= currentLocation.distanceTo(oldLocation)-Constants.GPSPARAMETERS.GPS_ERROR_RANGE;
            //speed is in meters/second
            double speed = distance/(time/1000);
            if(speed>=Constants.GPSPARAMETERS.FASTEST_HUMAN_SPEED)
                return false;
            else if(speed<Constants.GPSPARAMETERS.FASTEST_HUMAN_SPEED &&
                    speed>=Constants.GPSPARAMETERS.AVERAGE_HUMAN_FASTEST_RUNNING_SPEED &&
                    cargoWeight<Constants.GPSPARAMETERS.MAX_CARGO_FOR_FAST_RUN)
                return true;

            else if(speed<Constants.GPSPARAMETERS.AVERAGE_HUMAN_FASTEST_RUNNING_SPEED )
                return true;
            else
                return false;
        }
        else
        {
            return false;
        }
    }

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