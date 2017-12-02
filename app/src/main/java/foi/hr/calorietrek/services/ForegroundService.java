package foi.hr.calorietrek.services;

/**
 * Created by juras on 2017-11-30.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import foi.hr.calorietrek.R;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;

public class ForegroundService extends Service {
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION))
        {
            Intent notificationIntent = new Intent(this, TrainingActivity.class);

            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.cklogo);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setTicker(getString(R.string.app_name))
                    .setContentText(getString(R.string.training_started))
                    .setSmallIcon(R.drawable.cklogo)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        }
        else if(intent.getAction().equals(Constants.ACTION.PLAY_ACTION))
        {


        }
        else if(intent.getAction().equals(Constants.ACTION.PAUSE_ACTION))
        {

        }
        else if(intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION))
        {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}