package foi.hr.calorietrek.ui.finished_training;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.plus.PlusShare;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import foi.hr.calorietrek.Manifest;
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
    private boolean storagePermission = false;
    public int MY_PERMISSIONS_REQUEST_STORAGE = 101;

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
        storagePermission = checkStoragePermission();

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
            case R.id.action_pdf: {
                {
                    if(storagePermission)
                    {
                        exportPDF.writePDF(allTrainings);
                        Toast.makeText(getApplicationContext(), R.string.pdf_export_finished, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        askStoragePermission();
                        storagePermission = checkStoragePermission();
                    }
                }
                break;
            }
            case R.id.action_share:
                break;
            case R.id.action_module_submenu:
                break;
            case R.id.action_share_google:
                boolean isAppInstalled = appInstalledOrNot("com.google.android.apps.plus");
                if(isAppInstalled) {
                    View v1 = getWindow().getDecorView().getRootView().findViewById(android.R.id.content);
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = v1.getDrawingCache();
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "graf", "trening");
                    v1.setDrawingCacheEnabled(false);

                    PlusShare.Builder share = new PlusShare.Builder(this);
                    share.setText("My CalorieTrek Training!");
                    share.addStream(Uri.parse(path));
                    share.setType("image/jpg");
                    startActivity(share.getIntent());
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

    public boolean checkStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast toast = Toast.makeText(this, "The application needs permission for writing files on your storage.", Toast.LENGTH_SHORT);
                toast.show();
            }
            return false;
        }
        return true;
    }

    public void askStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
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
