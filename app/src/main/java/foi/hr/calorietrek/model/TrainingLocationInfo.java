package foi.hr.calorietrek.model;

import android.location.Location;

import java.util.concurrent.TimeUnit;

/**
 * Created by marko on 17-Jan-18.
 *  Model class for training location.
 */

public class TrainingLocationInfo {
    private Location location;
    private int cargoWeight;
    private long time;
    public  TrainingLocationInfo(Location location, int cargoWeight, long time){
        this.location=location;
        this.cargoWeight=cargoWeight;
        this.time=time;
}
    public Location getLocation() { return this.location; }
    public int getCargoWeight() { return this.cargoWeight; }
    public long getTime(){ return this.time; }
}
