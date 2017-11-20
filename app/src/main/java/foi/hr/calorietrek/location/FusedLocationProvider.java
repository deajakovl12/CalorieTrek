package foi.hr.calorietrek.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;
import org.greenrobot.eventbus.EventBus;

import foi.hr.calorietrek.message.LocationEventMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by marko on 14-Nov-17.
 */

public class FusedLocationProvider {
    private Location location;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;
    private LocationCallback locationCallback;
    // Accuracy  high-100, balanced-102,  low-104, noPower-105
    public FusedLocationProvider(long UpdateInterval,long FastestUpdateInterval,int Accuracy, Context context) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UpdateInterval);
        locationRequest.setFastestInterval(FastestUpdateInterval);
        locationRequest.setPriority(Accuracy);
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
        this.context=context;
    }
    public void setLocation(Location location){
        EventBus.getDefault().post(new LocationEventMessage(this.location,location));
        this.location=location;
    }
    public Location GetLocation(){
        return location;
    }
    public int startLocationUpdates(){
        //Provjera za dozvole pristupa
        int permissionCheckLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckNetworkState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionCheckInternet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        if(permissionCheckLocation == PackageManager.PERMISSION_GRANTED && permissionCheckNetworkState == PackageManager.PERMISSION_GRANTED && permissionCheckInternet == PackageManager.PERMISSION_GRANTED){
            //Zahtjevanje novih lokacija
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback=new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            setLocation(locationResult.getLastLocation());
                            Log.e(TAG, locationResult.toString());
                        }
                    },
                    Looper.myLooper());
            return  PackageManager.PERMISSION_GRANTED;
        }
        else
        {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
