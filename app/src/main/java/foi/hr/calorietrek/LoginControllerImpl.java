package foi.hr.calorietrek;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginControllerImpl implements ILoginController {

    @Override
    public void FacebookLogin()
    {

    }

    @Override
    public GoogleSignInOptions GmailLogin()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return gso;
    }
}
