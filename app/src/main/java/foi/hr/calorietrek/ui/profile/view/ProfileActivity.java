package foi.hr.calorietrek.ui.profile.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.MediaCas;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
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
import foi.hr.calorietrek.all_trainings.AllTrainings;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.ui.finished_training.FinishedTraining;
import foi.hr.calorietrek.ui.login.view.LoginActivity;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;


import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.w3c.dom.Text;

import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, IProfileView {

    private GoogleApiClient googleClient;

    public @BindView(R.id.toolbar) Toolbar toolbar;

    public @BindView(R.id.txtInputWeight) TextView inputWeight;
    public @BindView(R.id.txtYourWeight) TextView txtYourWeight;

    public @BindView(R.id.profileImage) ImageView profilePic;
    public @BindView(R.id.txtName) TextView name;
    public @BindView(R.id.btnLogOut) Button btnLogOff;

    static String personName;
    static String personEmail;
    static String personPhotoUrl;

    private ShareDialog shareDialog;

    DbHelper instance;

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

    // Rad s bazom u controller
    private void changeWeight(String newWeight) {
        boolean isUpdated = instance.updateWeight(CurrentUser.personName, newWeight);
        if (isUpdated == true && !newWeight.matches("")){
            Toast.makeText(getApplicationContext(), R.string.weight_saved, Toast.LENGTH_SHORT).show();
            instance.updateWeight(CurrentUser.personName, newWeight);
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.weight_not_saved, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnAllTrainings)
    public void onClickAllTrainings()
    {
        Intent intent = new Intent(ProfileActivity.this, AllTrainings.class);
        //intent.putExtra("ALL_TRAININGS", "true");
        startActivity(intent);
    }

    @OnClick(R.id.btnLogOut)
    public void onClickLogOut()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            LoginManager.getInstance().logOut();
            accessToken.isExpired();
            onBackPressed();
        }
        else{
            LogOut();
        }

        Intent login = new Intent(ProfileActivity.this, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof TextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        txtYourWeight.setVisibility(View.INVISIBLE);

        inputWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                inputWeight.getBackground().setColorFilter(getResources().getColor(R.color.light_green), PorterDuff.Mode.SRC_ATOP);
                txtYourWeight.setVisibility(View.VISIBLE);
                if(!hasFocus)
                {
                    txtYourWeight.setVisibility(View.INVISIBLE);
                    inputWeight.getBackground().setColorFilter(getResources().getColor(R.color.silver), PorterDuff.Mode.SRC_ATOP);
                    changeWeight(inputWeight.getText().toString());
                }
            }
        });


        shareDialog = new ShareDialog(this);
        String fbname,fbsurname,fbimageUrl,fbemail;

        if(getIntent().hasExtra("name") && getIntent().hasExtra("surname")) {
            Bundle inBundle = getIntent().getExtras();
            fbname = inBundle.get("name").toString();
            fbsurname = inBundle.get("surname").toString();
            fbimageUrl = inBundle.get("imageUrl").toString();

            if(getIntent().hasExtra("userModel")) {
                UserModel userModel = getIntent().getParcelableExtra("userModel");

                personName = userModel.getPersonName();
                personEmail = userModel.getPersonEmail();
                personPhotoUrl = userModel.getPersonPhotoUrl();
                name.setText(CurrentUser.personName);
            }

            else{
                name.setText(CurrentUser.personName);
            }

            if(fbimageUrl != "noImage") {
                new ProfileActivity.DownloadImage(profilePic).execute(fbimageUrl);
            }
        }
        else {

            getAccount();
        }

        instance = DbHelper.getInstance(this);
        initToolbar();
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

        }
        else{
            name.setText(CurrentUser.personName);
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
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage){
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try{
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result){
            bmImage.setImageBitmap(result);
        }

    }


}