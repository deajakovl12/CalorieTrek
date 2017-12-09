package foi.hr.calorietrek.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;

import foi.hr.calorietrek.constants.Constants;

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
        //checking if application has correct permission for accesing current location
        int permissionCheckLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckNetworkState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionCheckInternet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        if(permissionCheckLocation == PackageManager.PERMISSION_GRANTED && permissionCheckNetworkState == PackageManager.PERMISSION_GRANTED && permissionCheckInternet == PackageManager.PERMISSION_GRANTED){
            isRegistered=true;
            //requesting new locations from fused location provider
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback=new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if(isBetterLocation(locationResult.getLastLocation(),location)) {
                                setLocation(locationResult.getLastLocation());
                            }
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

    //tests if new location is better or worse than current location
    //by testing time between locations, accuracy from locations and if locations are from same provider
    private boolean isBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > Constants.GPSPARAMETERS.TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -Constants.GPSPARAMETERS.TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > Constants.GPSPARAMETERS.ACCURACY/2;

        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    public static boolean isLocationEnabled(Context context){
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
