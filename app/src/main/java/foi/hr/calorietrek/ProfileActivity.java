package foi.hr.calorietrek;

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

public class ProfileActivity extends AppCompatActivity{

    LoginControllerImpl loginController = null;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txtKg) TextView showKg;
    @BindView(R.id.sbKg) SeekBar seekBarKg;

    @BindView(R.id.profileImage) ImageView profilePic;
    @BindView(R.id.txtName) TextView name;
    @BindView(R.id.btnLogOut) Button btnLogOff;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        UserModel userModel = getIntent().getParcelableExtra("userModel");
        personName = userModel.getPersonName();
        personEmail = userModel.getPersonEmail();
        personPhotoUrl = userModel.getPersonPhotoUrl().toString();

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
