package foi.hr.calorietrek.ui.login.view;

import android.content.Context;
import android.content.Intent;
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
import foi.hr.calorietrek.database.DbHelper;
import foi.hr.calorietrek.ui.login.controller.LoginControllerImpl;
import foi.hr.calorietrek.ui.profile.view.ProfileActivity;
import foi.hr.calorietrek.R;
import foi.hr.calorietrek.model.UserModel;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ILoginView {

    public  @BindView(R.id.btn_sign_in) SignInButton btnSignIn;

    LoginControllerImpl loginController = null;
    UserModel userModel = null;

    public static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;

    DbHelper instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginController = new LoginControllerImpl();
        GoogleSignInOptions gso = loginController.GmailLogin(this);
        mGoogleApiClient = GetGoogleApiClient(gso);

        instance = DbHelper.getInstance(this);
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
        Intent sendData = new Intent(LoginActivity.this, ProfileActivity.class);
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
            Toast.makeText(LoginActivity.this, "New user inserted", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(LoginActivity.this, "Existing user", Toast.LENGTH_LONG).show();
        }
    }
}

