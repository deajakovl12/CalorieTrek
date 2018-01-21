package foi.hr.calorietrek.ui.login.view;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
/* View class used for login */
public interface ILoginView {
    void LoginSuccessful(GoogleSignInResult result);
    void LoginFailed();
}
