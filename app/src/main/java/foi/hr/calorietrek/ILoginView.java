package foi.hr.calorietrek;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public interface ILoginView {
    void LoginSuccessful(GoogleSignInResult result);
    void LoginFailed();
}
