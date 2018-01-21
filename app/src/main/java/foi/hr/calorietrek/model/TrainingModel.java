package foi.hr.calorietrek.model;
import android.location.Location;
import java.util.ArrayList;

public class TrainingModel {
    private int id;
    private String date;
    private String name;
    private int userWeight;
    private ArrayList<TrainingLocationInfo> locations;

    public TrainingModel(int id, String date, String name, ArrayList<TrainingLocationInfo> locations, int userWeight )
    {
        this.date = date;
        this.name = name;
        this.locations = locations;
        this.userWeight = userWeight;
        this.id = id;
    }

    public int getTrainingID() { return this.id; }

    public String getDate() { return this.date; }

    public String getName() { return this.name; }

    public int getUserWeight() { return this.userWeight; }

    public ArrayList<TrainingLocationInfo> getLocations() { return this.locations; }
}

