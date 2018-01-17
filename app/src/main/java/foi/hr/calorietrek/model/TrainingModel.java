package foi.hr.calorietrek.model;
import android.location.Location;
import java.util.ArrayList;

public class TrainingModel {
    private String date;
    private String name;
    private ArrayList<Location> locations;

    public TrainingModel(String date, String name, ArrayList<Location> locations)
    {
        this.date = date;
        this.name = name;
        this.locations = locations;
    }

    public String getDate() { return this.date; }

    public String getName() { return this.name; }

    public ArrayList<Location> getLocations() { return this.locations; }
}

