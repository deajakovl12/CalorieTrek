package foi.hr.calorietrek.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;

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
    private boolean isRegistered;
    public FusedLocationProvider(long UpdateInterval,long FastestUpdateInterval,int Accuracy, Context context) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UpdateInterval);
        locationRequest.setFastestInterval(FastestUpdateInterval);
        locationRequest.setPriority(Accuracy);
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
        this.context=context;
        isRegistered=false;
    }
    public void setLocation(Location location){
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
            isRegistered=true;
            //Zahtjevanje novih lokacija
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback=new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            setLocation(locationResult.getLastLocation());
                            Log.e(TAG, "onLocationResult: "+locationResult.getLastLocation().toString() );
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
        if(isRegistered) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            isRegistered=false;
        }
    }
    public static boolean isLocationEnabled(Context context){
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
