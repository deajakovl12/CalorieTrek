package foi.hr.calorietrek.model;

import javax.inject.Inject;

/* Model class for current user. */
public class CurrentUser {
    public static String personName;
    public static String personEmail;
    public static String profilePic;

    @Inject
    public CurrentUser(String name, String email, String picture){
        this.personName = name;
        this.personEmail = email;
        this.profilePic = picture;
    }
}
