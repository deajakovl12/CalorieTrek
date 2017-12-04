package foi.hr.calorietrek.ui.profile.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.ui.login.view.LoginActivity;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, IProfileView {

    private GoogleApiClient googleClient;

    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.txtKg) TextView showKg;
    public @BindView(R.id.sbKg) SeekBar seekBarKg;

    public @BindView(R.id.profileImage) ImageView profilePic;
    public @BindView(R.id.txtName) TextView name;
    public @BindView(R.id.btnLogOut) Button btnLogOff;

    static String personName;
    static String personEmail;
    static String personPhotoUrl;

    DbHelper instance;

    int min = 20, max = 200, current = 55;

    //Toolbar
    public void initToolbar(){
        ButterKnife.bind(this);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadWeight() {

        String result = instance.returnWeight(CurrentUser.personName);
        try {
            current = Integer.parseInt(result);
        }
        catch (NumberFormatException e){
            current = 55;
        }

    }

    //Prikaz tezine u kg
    public void showWeight(){
        ButterKnife.bind(this);

        seekBarKg.setMax(max);
        seekBarKg.setProgress(current - min);
        showKg.setText("" + current + " kg");

        seekBarKg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                current = progress + min;
                showKg.setText("" + current + " kg");

                changeWeight(String.valueOf(current));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // Rad s bazom u controller
    private void changeWeight(String newWeight) {
        boolean isUpdated = instance.updateWeight(CurrentUser.personName, newWeight);
        if (isUpdated == true){
            Toast.makeText(getApplicationContext(), "Weight updated!", Toast.LENGTH_SHORT).show();
            instance.updateWeight(CurrentUser.personName, newWeight);
        }
        else{
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnLogOut)
    public void onClickLogOut()
    {
        LogOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getAccount();
        instance = DbHelper.getInstance(this);
        loadWeight();
        initToolbar();
        showWeight();
    }

    // Prebaciti u controller
    public void getAccount(){
        //changed
        if(getIntent().hasExtra("userModel")) {
            UserModel userModel = getIntent().getParcelableExtra("userModel");

            CurrentUser.personName = userModel.getPersonName();
            personEmail = userModel.getPersonEmail();
            personPhotoUrl = userModel.getPersonPhotoUrl();

            //CurrentUser loggedUser = new CurrentUser(personName, personEmail, personPhotoUrl);
            name.setText(CurrentUser.personName);

            /*
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("personName", personName);
            editor.putString("personEmail", personEmail);
            editor.putString("personPhotoUrl", personPhotoUrl);
            editor.apply();
            Log.e("tusamPA1",sharedPref.getString("personName","not Available"));
            */
        }
        else{
            name.setText(CurrentUser.personName);
            /*
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            personName=sharedPref.getString("personName","not Available");
            personEmail=sharedPref.getString("personEmail","not Available");
            personPhotoUrl= sharedPref.getString("personPhotoUrl","noImage");
            name.setText(personName);
            Log.e("tusamPA2",sharedPref.getString("personName","not Available"));
            */
        }

        //-changed
        if (CurrentUser.profilePic != "noImage") {
            Glide.with(getApplicationContext()).load(CurrentUser.profilePic)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profilePic);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    // Prebaciti u controller
    public void LogOut()
    {
        Auth.GoogleSignInApi.signOut(googleClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()){
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Not close", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(ProfileActivity.this, TrainingActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}