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
    }

    private GoogleSignInOptions GetGoogleSignInOptions()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return gso;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
