package foi.hr.calorietrek.ui.finished_training;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.google.android.gms.plus.PlusShare;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.TrainingModel;
import foi.hr.calorietrek.module_navigation.NavigationManager;


public class FinishedTraining extends AppCompatActivity {

    public @BindView(R.id.toolbarDetails) Toolbar toolbarDetails;
    private NavigationManager navManager;
    private DbHelper instance;
    public ArrayList<TrainingModel> allTrainings;
    private static final int REQ_SELECT_PHOTO = 1;
    private static final int REQ_START_SHARE = 2;

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
        allTrainings = new ArrayList<TrainingModel>();

        boolean isAllTrainings = Boolean.parseBoolean(getIntent().getStringExtra("ALL_TRAININGS"));
        isAllTrainings = false;
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
                break;
            case R.id.action_share:
                break;
            case R.id.action_module_submenu:
                break;

            case R.id.action_share_google:
                boolean isAppInstalled = appInstalledOrNot("com.google.android.apps.plus");
                if(isAppInstalled) {
                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                    photoPicker.setType("video/*, image/*");
                    startActivityForResult(photoPicker, REQ_SELECT_PHOTO);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Google+ app not installed!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                navManager.selectNavigationItem(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQ_SELECT_PHOTO) {
            if(resultCode == RESULT_OK) {
                Uri selectedImage = intent.getData();
                ContentResolver cr = this.getContentResolver();
                String mime = cr.getType(selectedImage);


                PlusShare.Builder share = new PlusShare.Builder(this);
                share.setText("My CalorieTrek Training!");
                share.addStream(selectedImage);
                share.setType(mime);
                startActivityForResult(share.getIntent(), REQ_START_SHARE);
            }
        }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }



}
