package foi.hr.calorietrek.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import foi.hr.calorietrek.location.FusedLocationProvider;
import foi.hr.calorietrek.message.LocationEventMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by marko on 17-Nov-17.
 */

public class LocationService extends Service {
    FusedLocationProvider fusedLocationProvider;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fusedLocationProvider = new FusedLocationProvider(10000,5000,100,this);
        fusedLocationProvider.startLocationUpdates();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        fusedLocationProvider.stopLocationUpdates();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onLocationMessageEvent(LocationEventMessage locationEventMessage) {
        if(locationEventMessage.getOldLocation()!=null) {
            Log.e(TAG, Float.toString(locationEventMessage.getNewLocation().distanceTo(locationEventMessage.getOldLocation())));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
