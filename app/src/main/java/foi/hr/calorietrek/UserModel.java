package foi.hr.calorietrek;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String personName;
    private String personEmail;
    private String personPhotoUrl;

    public UserModel(String personName, String personEmail, String personPhotoUrl)
    {
        this.personName = personName;
        this.personEmail = personEmail;
        this.personPhotoUrl = personPhotoUrl;
    }

    public String getPersonName()
    {
        return personName;
    }

    public String getPersonEmail()
    {
        return personEmail;
    }

    public String getPersonPhotoUrl()
    {
        return personPhotoUrl;
    }


    protected UserModel(Parcel in) {
        personName = in.readString();
        personEmail = in.readString();
        personPhotoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(personName);
        dest.writeString(personEmail);
        dest.writeString(personPhotoUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}