package design.semicolon.sillytwitter.models;

import android.content.Context;
import android.preference.PreferenceManager;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "User")
public class User extends Model implements Serializable {

    private static final String CURRENT_USER = "current_user";

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

    public static void setCurrentUser(Context context, JSONObject jsonObject){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(CURRENT_USER, jsonObject.toString()).apply();
    }

    public static User currentUser(Context context) throws JSONException {

        String userJsonString = PreferenceManager.getDefaultSharedPreferences(context).getString(CURRENT_USER, null);

        if (userJsonString != null || userJsonString.length() != 0){
            JSONObject jsonObject = new JSONObject(userJsonString);
            return User.fromJSON(jsonObject);
        } else {
            return null;
        }
    }

//    public static  JSONObject toJSON(User user) {
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("name", user.getFullName());
//            jsonObject.put("screen_name", user.getUserName());
//            jsonObject.put("id", user.uid);
//            jsonObject.put("profile_image_url", user.userProfilePictureURLString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return JSONObject;
//    }
}
