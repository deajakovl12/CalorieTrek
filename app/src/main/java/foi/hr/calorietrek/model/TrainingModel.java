package foi.hr.calorietrek.model;
import android.location.Location;
import java.util.ArrayList;
/* Model class for training. */
public class TrainingModel {
    private String date;
    private String name;
    private int userWeight;
    private ArrayList<TrainingLocationInfo> locations;

    public TrainingModel(String date, String name, ArrayList<TrainingLocationInfo> locations, int userWeight )
    {
        this.date = date;
        this.name = name;
        this.locations = locations;
        this.userWeight = userWeight;
    }

    public String getDate() { return this.date; }

    public String getName() { return this.name; }

    public int getUserWeight() { return this.userWeight; }

    public ArrayList<TrainingLocationInfo> getLocations() { return this.locations; }
}

