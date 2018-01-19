package foi.hr.calorietrek.ui.finished_training;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.TrainingModel;
import foi.hr.calorietrek.module_navigation.NavigationManager;
import foi.hr.calorietrek.pdf_export.ExportPDF;


public class FinishedTraining extends AppCompatActivity {

    public @BindView(R.id.toolbarDetails) Toolbar toolbarDetails;
    private NavigationManager navManager;
    private DbHelper instance;
    public ExportPDF exportPDF;
    public ArrayList<TrainingModel> allTrainings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_training);

        ButterKnife.bind(this);
        toolbarDetails.setTitle("");
        toolbarDetails.inflateMenu(R.menu.menu_finished_training);
        setSupportActionBar(toolbarDetails);

        navManager = NavigationManager.getInstance();
        navManager.setDependencies(this, toolbarDetails);
        instance = DbHelper.getInstance(this);
        exportPDF = new ExportPDF(this.getApplicationContext());
        allTrainings = new ArrayList<TrainingModel>();

        boolean isAllTrainings = Boolean.parseBoolean(getIntent().getStringExtra("ALL_TRAININGS"));
        if(isAllTrainings)
        {
            allTrainings = instance.returnAllTrainings(instance.getUserID(CurrentUser.personEmail));
        }
        else
        {
            allTrainings.add(instance.returnLatestTraining(instance.getUserID(CurrentUser.personEmail)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_finished_training, menu);

        //modules are added here
        navManager.addItem(new TrainingDetailsFragment());
        navManager.addItem(new TrainingDetailsAlternateFragment());

        //loading the default(first) fragment
        SubMenu submenu = toolbarDetails.getMenu().getItem(2).getSubMenu();
        if(submenu.size() > 0) {
            navManager.selectNavigationItem(submenu.getItem(0));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.action_pdf:
                exportPDF.writePDF(allTrainings);
                break;
            case R.id.action_share:
                break;
            case R.id.action_module_submenu:
                break;
            default:
                navManager.selectNavigationItem(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
