package foi.hr.calorietrek.ui.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.ui.login.controller.LoginControllerImpl;

public class ProfileActivity extends AppCompatActivity{

    LoginControllerImpl loginController = null;

    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.txtKg) TextView showKg;
    public @BindView(R.id.sbKg) SeekBar seekBarKg;

    public @BindView(R.id.profileImage) ImageView profilePic;
    public @BindView(R.id.txtName) TextView name;
    public @BindView(R.id.btnLogOut) Button btnLogOff;

    String personName;
    String personEmail;
    String personPhotoUrl;

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
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick(R.id.btnLogOut)
    public void onClickLogOut()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

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

        initToolbar();
        showWeight();
    }

    private void LogOut(GoogleApiClient mGoogleApiClient)
    {

    }
}
