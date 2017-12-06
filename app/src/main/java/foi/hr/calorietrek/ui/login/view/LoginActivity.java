package foi.hr.calorietrek.ui.login.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foi.hr.calorietrek.constants.Constants;
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.ui.login.controller.LoginControllerImpl;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.model.UserModel;
import foi.hr.calorietrek.ui.training.view.TrainingActivity;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;
import org.json.JSONException;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ILoginView {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    String personEmail="";
    @BindView(R.id.login_button) LoginButton loginButton;

    public  @BindView(R.id.btn_sign_in) SignInButton btnSignIn;

    LoginControllerImpl loginController = null;
    UserModel userModel = null;

    public static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;

    DbHelper instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginController = new LoginControllerImpl();
        GoogleSignInOptions gso = loginController.GmailLogin(this);
        mGoogleApiClient = GetGoogleApiClient(gso);

        instance = DbHelper.getInstance(this);
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();

                // ZA EMAIL
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {
                                    personEmail = object.getString("email");
                                    String birthday = object.getString("birthday");
                                }
                                catch (JSONException e){
                                    Log.e("CalorieTrek", "unexpected JSON exception", e);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                //do tu EMAIL
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        //LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email"));

        // loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, callback);


    }

    private GoogleApiClient GetGoogleApiClient(GoogleSignInOptions gso)
    {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return mGoogleApiClient;
    }

    @OnClick(R.id.btn_sign_in)
    public void onClick()
    {
        SignIn();
    }

    private void SignIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            LoginSuccessful(result);
        }
        else
        {
            LoginFailed();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone())
        {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        else
        {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(ProfileActivity.class.getSimpleName(), "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void LoginSuccessful(GoogleSignInResult result)
    {
        GoogleSignInAccount accountData = result.getSignInAccount();
        String personPhoto;

        DbUser(accountData.getDisplayName());

        if (accountData.getPhotoUrl() != null){
            personPhoto = accountData.getPhotoUrl().toString();
        }
        else{
            personPhoto = "noImage";
        }

        userModel = new UserModel(accountData.getDisplayName(), accountData.getEmail(), personPhoto);
        CurrentUser loggedUser = new CurrentUser(accountData.getDisplayName(), accountData.getEmail(), personPhoto);
        Intent sendData = new Intent(LoginActivity.this, TrainingActivity.class);
        sendData.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        sendData.putExtra("userModel", userModel);
        startActivity(sendData);
    }

    @Override
    public void LoginFailed()
    {

    }

    public void DbUser(String nameSurname){
        boolean isInserted = instance.existingUser(nameSurname);
        if (isInserted == true){
            Toast.makeText(LoginActivity.this, R.string.new_user_inserted, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(LoginActivity.this, R.string.existing_user, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }


    private void nextActivity(Profile profile){
        String personPhoto;

        if(profile != null){
            DbUser(profile.getName());

            if ( profile.getProfilePictureUri(Constants.PHOTOPARAMETERS.PHOTO_WIDTH,Constants.PHOTOPARAMETERS.PHOTO_HEIGHT).toString() != null){
                personPhoto = profile.getProfilePictureUri(Constants.PHOTOPARAMETERS.PHOTO_WIDTH,Constants.PHOTOPARAMETERS.PHOTO_HEIGHT).toString();
            }
            else{
                personPhoto = "noImage";
            }

            //userModel = new UserModel(profile.getName(), "", personPhoto);
            userModel = new UserModel(profile.getName(), personEmail, personPhoto);
            Intent main = new Intent(LoginActivity.this, TrainingActivity.class);
            CurrentUser loggedUser = new CurrentUser(profile.getName(),personEmail, personPhoto);

            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageUrl", personPhoto);
            main.putExtra("email", personEmail);
            main.putExtra("userModel", userModel);

            startActivity(main);
        }
    }


}

