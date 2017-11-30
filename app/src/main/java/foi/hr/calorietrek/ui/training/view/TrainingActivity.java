package foi.hr.calorietrek.ui.training.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;

public class TrainingActivity extends AppCompatActivity {

    public @BindView(R.id.toolbar) Toolbar toolbar;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        //add
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        userModel = new UserModel(sharedPref.getString("personName",null),sharedPref.getString("personEmail",null),sharedPref.getString("personPhotoUrl",null));
        //-add
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menubar_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_profile){
            //remove1
            Intent intent = new Intent(TrainingActivity.this, ProfileActivity.class);
            //remove1
            startActivity(intent);
            //Toast.makeText(TrainingActivity.this, "PROFIL!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}