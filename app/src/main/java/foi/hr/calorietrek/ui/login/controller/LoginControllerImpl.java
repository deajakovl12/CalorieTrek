package foi.hr.calorietrek.ui.login.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
/* Controller class used for login */
public class LoginControllerImpl extends FragmentActivity implements ILoginController, GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void FacebookLogin()
    {

    }

    @Override
    public GoogleSignInOptions GmailLogin(Context context)
    {
        GoogleSignInOptions gso = GetGoogleSignInOptions();
        return gso;
        //GoogleApiClient mGoogleApiClient = GetGoogleApiClient(gso, context);
    }

    private GoogleSignInOptions GetGoogleSignInOptions()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return gso;
    }

/*
    private GoogleApiClient GetGoogleApiClient(GoogleSignInOptions gso, Context context)
    {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return mGoogleApiClient;
    }
*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
