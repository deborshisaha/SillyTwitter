package design.semicolon.sillytwitter.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import design.semicolon.sillytwitter.helpers.DateHelper;

@Table(name = "Tweet")
public class Tweet extends Model implements Serializable {

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt(boolean relative) {
        if (relative == true) {
            return DateHelper.getRelativeTimeAgo(createdAt);
        }

        return createdAt;
    }

    public static Tweet fromJSON(JSONObject tweetObject) {

        Tweet tweet = new Tweet();

        try {
            tweet.user = User.fromJSON(tweetObject.getJSONObject("user"));
            tweet.text = tweetObject.getString("text");
            tweet.uid = tweetObject.getLong("id");
            tweet.uidStr = tweetObject.getString("id_str");
            tweet.createdAt = tweetObject.getString("created_at");
            tweet.retweet_count = tweetObject.getInt("retweet_count");
            tweet.favorite_count = tweetObject.getInt("favorite_count");

            tweet.timestamp = DateHelper.convertToDate(tweetObject.getString("created_at")).getTime();

            if (tweetObject.optJSONObject("extended_entities") != null) {
                JSONObject extendedEntitiesJSONObject = tweetObject.getJSONObject("extended_entities");


                if (extendedEntitiesJSONObject.optJSONArray("media")!= null) {
                    JSONArray mediasJSONArray = extendedEntitiesJSONObject.getJSONArray("media");
                    ActiveAndroid.beginTransaction();
                    try {

                        for (int i = 0; i < mediasJSONArray.length(); i++) {

                            JSONObject multimediaJSONObject = mediasJSONArray.getJSONObject(i);
                            TwitterMedia twitterMedia = TwitterMedia.fromJSON(tweet,multimediaJSONObject);
                            twitterMedia.save();
                            if (tweet.twitterMedias == null){
                                tweet.twitterMedias = new ArrayList<TwitterMedia>();
                            }

                            tweet.twitterMedias.add(twitterMedia);
                            break;
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                }
            } else {
                Log.d("DEBUG", "No extended entities");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static List<Tweet> all() {
        return new Select().from(Tweet.class).orderBy("timestamp DESC").execute();
    }

    public List<TwitterMedia> medias() {
        return this.twitterMedias;
    }

    public String getFavoriteCount() {
        return ""+favorite_count;
    }

    public String getRetweetCount() {
        return ""+retweet_count;
    }

    private List<TwitterMedia> twitterMedias;

    @Column(name = "user", index = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "text")
    private String text;

    @Column(name = "uid", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "uidStr")
    private String uidStr;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "retweet_count")
    private int retweet_count;

    @Column(name = "favorite_count")
    private int favorite_count;

    public static void deleteAll() {
        new Delete().from(Tweet.class).execute();
    }

    public String getFirstImageURL() {
        if (this.twitterMedias == null) {
            return null;
        }

        if (this.twitterMedias.size() > 0) {
            TwitterMedia media = (TwitterMedia) this.twitterMedias.get(0);
            return media.getMediaUrl();
        }

        return null;
    }
}
