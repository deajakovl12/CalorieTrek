package foi.hr.calorietrek.ui.login.controller;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

/* Interface class used for login. */
public interface ILoginController {
    GoogleSignInOptions GmailLogin(Context context);
    void FacebookLogin();
}
