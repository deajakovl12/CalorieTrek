package foi.hr.calorietrek.calorie;

import android.location.Location;

import foi.hr.calorietrek.constants.Constants;

import static java.lang.Math.abs;

/**
 * Created by marko on 11-Jan-18.
 */

public final class CalorieCalculus {
    public static  double calculateDistance(Location oldLocation, Location currentLocation) {
        return oldLocation.distanceTo(currentLocation);
    }
    public static  double calculateElevationGain(Location currentLocation, Location oldLocation) {
        return currentLocation.getAltitude()-oldLocation.getAltitude();
    }

    public static double calculateCoefficient(Location currentLocation, Location oldLocation)
    {
        double slope=(currentLocation.getAltitude() - oldLocation.getAltitude()) / oldLocation.distanceTo(currentLocation);
        for(int i = 0; i< Constants.CALORIES.SLOPE_COEFFICIENT.length-1; i++) {
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

    public static double calculateCalories(Location currentLocation, Location oldLocation, int userWeight, int cargoWeight)
    {
        if(currentLocation != null && oldLocation != null && oldLocation.distanceTo(currentLocation) != 0)
        {
            double coefficient = calculateCoefficient(currentLocation, oldLocation);
            return ((coefficient * (userWeight + cargoWeight) * oldLocation.distanceTo(currentLocation)) / Constants.CALORIES.JOULES);
        }
        return 0;
    }
    //tests if speed is in good range for walking/running human
    public static boolean isLocationAccurateEnough(Location oldLocation, Location currentLocation, int cargoWeight)
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
}
