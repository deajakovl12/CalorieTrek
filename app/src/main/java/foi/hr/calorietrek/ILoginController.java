package foi.hr.calorietrek;


import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public interface ILoginController {
    GoogleSignInOptions GmailLogin();
    void FacebookLogin();
}
