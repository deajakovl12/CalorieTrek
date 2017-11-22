package foi.hr.calorietrek.message;

import android.location.Location;

/**
 * Created by marko on 20-Nov-17.
 */

public class LocationEventMessage {
    private Location oldLocation;
    private Location newLocation;
    public LocationEventMessage(Location oldLocation, Location newLocation) {
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    public Location getOldLocation() {
        return oldLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }
}
