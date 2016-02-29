package design.semicolon.sillytwitter.models;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import design.semicolon.sillytwitter.listerners.OnFetchingCompleteListener;

@Table(name = "User")
public class User extends Model implements Serializable {

    private static final String CURRENT_USER = "current_user";
    public static final String USER = "user";

    @Column(name = "name")
    private String fullName;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String userProfilePictureURLString;

    @Column(name = "profile_banner_url")
    private String profileBannerImageURL;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid; // Unique id of the user

    @Column(name = "bio")
    private String bio;

    @Column(name = "friends_count")
    private int friendsCount;

    @Column(name = "followers_count")
    private int followersCount;

    @Column(name = "location")
    private String location;

    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    @Column(name = "profile_background_color")
    private String profileBackgroundColor;

    public static void setCurrentUser(Context context, JSONObject jsonObject){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(CURRENT_USER, jsonObject.toString()).apply();
    }

    public static User currentUser(Context context) {

        String userJsonString = PreferenceManager.getDefaultSharedPreferences(context).getString(CURRENT_USER, "");

        if (userJsonString != null && userJsonString.length() != 0){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(userJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return User.fromJSON(jsonObject);
        } else {
            return null;
        }
    }

    public User(String fullName, String screenName, String ppURL) {
        this.fullName = fullName;
        this.screenName = screenName;
        this.userProfilePictureURLString = ppURL;
    }

    public User saveIfNecessaryElseGetUser() {
        long rId = this.uid;
        User existingUser = new Select().from(User.class).where("uid = ?", rId).executeSingle();
        if (existingUser != null) {
            return existingUser;
        } else {
            this.save();
            return this;
        }
    }

    public List<Tweet> allTweets() {

        User user = new Select()
                .from(User.class)
                .where("screen_name= ?", this.screenName)
                .executeSingle();

        return new Select().from(Tweet.class).where("user = ?", user.getId()).orderBy("timestamp DESC").execute();
    }

    public List<Tweet> favorites() {

        User user = new Select()
                .from(User.class)
                .where("screen_name= ?", this.screenName)
                .executeSingle();

        return new Select().from(Tweet.class).where("user = ? & favorited = ?", user.getId(), true).orderBy("timestamp DESC").execute();
    }


    public void fetchMediaInBackground (final OnFetchingCompleteListener onFetchingCompleteListener) {

        Handler uiHandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                List<Tweet> tweets = allTweets();
                List<TwitterMedia> userGeneratedMedia = new ArrayList<TwitterMedia>();

                for(Tweet tweet:tweets) {
                    userGeneratedMedia.addAll(tweet.allMedia());
                }
                onFetchingCompleteListener.mediaFetched(userGeneratedMedia);
            }
        };
        uiHandler.post(runnable);
    }

    public User(){}

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return "@"+screenName;
    }

    public String getUserProfilePictureURLString() {
        return userProfilePictureURLString;
    }

    public static User fromJSON(JSONObject userObject) {

        User user = new User();

        try {
            user.fullName = userObject.getString("name");
            user.screenName = userObject.getString("screen_name");
            user.bio = userObject.getString("description");
            user.uid = userObject.getLong("id");
            user.location = userObject.getString("location");
            user.followersCount = userObject.getInt("followers_count");
            user.friendsCount = userObject.getInt("friends_count");

            if (userObject.optString("profile_banner_url").length() != 0){
                user.profileBannerImageURL = userObject.getString("profile_banner_url");
            }else if (userObject.optString("profile_background_color").length() != 0){
                user.profileBackgroundColor = userObject.getString("profile_background_color");
            }

            String s = userObject.getString("profile_image_url");
            if (s != null){
                user.userProfilePictureURLString = s;
            } else {
                user.userProfilePictureURLString = null;
            }

        } catch (JSONException e) {
            Log.d("USER", userObject.toString());
        }

        return user;
    }


    public long getUid() {
        return uid;
    }

    public static List<User> all() {
        return new Select().from(User.class).execute();
    }

    public static void deleteAll() {
        new Delete().from(User.class).execute();
    }

    public List<Tweet> tweets() {
        return getMany(Tweet.class, "Tweet");
    }

    public String getProfileBannerImageURL() {
        return profileBannerImageURL;
    }

    public String getFollowersCount() {
        return followersCount+"";
    }

    public String getFriendsCount() {
        return friendsCount+"";
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    private User getUserWithScreenName(User user) {
        String screenName = user.screenName;
        User result = new Select()
                .from(User.class)
                .where("screen_name= ?", screenName)
                .executeSingle();
        return result;
    }

}
