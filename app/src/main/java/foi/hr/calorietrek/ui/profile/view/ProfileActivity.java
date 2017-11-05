package foi.hr.calorietrek.ui.profile.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import foi.hr.calorietrek.database.DbHelperUser;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.ui.login.controller.LoginControllerImpl;
import foi.hr.calorietrek.ui.login.view.LoginActivity;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, IProfileView {

    private GoogleApiClient googleClient;

    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.txtKg) TextView showKg;
    public @BindView(R.id.sbKg) SeekBar seekBarKg;

    public @BindView(R.id.profileImage) ImageView profilePic;
    public @BindView(R.id.txtName) TextView name;
    public @BindView(R.id.btnLogOut) Button btnLogOff;

    String personName;
    String personEmail;
    String personPhotoUrl;

    DbHelperUser dbUser;

    int min = 0, max = 120, current = 55;

    //Toolbar
    public void initToolbar(){
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.menu_action_profile);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.iconback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Toast.makeText(ProfileActivity.this, "Click on toolbar!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeight() {
        dbUser = new DbHelperUser(this);
        String result = dbUser.returnWeight(personName);
        current = Integer.parseInt(result);
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

    private void changeWeight(String newWeight) {
        dbUser = new DbHelperUser(this);
        boolean isUpdated = dbUser.updateWeight(personName, newWeight);
        if (isUpdated == true){
            Toast.makeText(getApplicationContext(), "Weight updated!", Toast.LENGTH_SHORT).show();
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

        loadWeight();
        initToolbar();
        showWeight();
    }

    public void getAccount(){
        UserModel userModel = getIntent().getParcelableExtra("userModel");
        if(userModel.getPersonName() != null)
        {
            personName = userModel.getPersonName();
        }
        else
        {
            personName = "";
        }
        if(userModel.getPersonEmail() != null)
        {
            personEmail = userModel.getPersonEmail();
        }
        else
        {
            personEmail = "";
        }
        if(userModel.getPersonPhotoUrl() != null)
        {
            personPhotoUrl = userModel.getPersonPhotoUrl().toString();
        }
        else
        {
            personPhotoUrl = "";
        }

        name.setText(personName);
        Glide.with(getApplicationContext()).load(personPhotoUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profilePic);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

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
}
