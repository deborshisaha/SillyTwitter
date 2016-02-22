package design.semicolon.sillytwitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "User")
public class User extends Model implements Serializable {

    public User(String fullName, String screenName, String ppURL) {
        this.fullName = fullName;
        this.screenName = screenName;
        this.userProfilePictureURLString = ppURL;
    }

    public User(){}

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return screenName;
    }

    public String getUserProfilePictureURLString() {
        return userProfilePictureURLString;
    }

    public static User fromJSON(JSONObject userObject) {

        User user = new User();

        try {
            user.fullName = userObject.getString("name");
            user.screenName = userObject.getString("screen_name");
            user.uid = userObject.getLong("id");
            user.userProfilePictureURLString = userObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<Tweet> tweets() {
        return getMany(Tweet.class, "Tweet");
    }

    @Column(name = "name")
    private String fullName;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String userProfilePictureURLString;

    @Column(name = "uid", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid; // Unique id of the user

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.fullName);
//        dest.writeString(this.screenName);
//        dest.writeString(this.userProfilePictureURLString);
//        dest.writeString(this.id);
//    }
//
//    public User() {
//    }
//
//    protected User(Parcel in) {
//        this.fullName = in.readString();
//        this.screenName = in.readString();
//        this.userProfilePictureURLString = in.readString();
//        this.id = in.readString();
//    }
//
//    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
//        public User createFromParcel(Parcel source) {
//            return new User(source);
//        }
//
//        public User[] newArray(int size) {
//            return new User[size];
//        }
//    };
}
