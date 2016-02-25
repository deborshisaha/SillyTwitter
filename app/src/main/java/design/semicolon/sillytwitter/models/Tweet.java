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

    private String secondImageURL;

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "user", index = true)
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

    private List<TwitterMedia> twitterMedias;

    public User getUser() {
        return user;
    }

    public List<TwitterMedia> getTwitterMedias() {
        if (twitterMedias == null){
            twitterMedias= new ArrayList<TwitterMedia>();
            twitterMedias.addAll(getMany(TwitterMedia.class, "Tweet"));
        }

        return twitterMedias;
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

    public String getUidStr() {
        return uidStr;
    }

    public static Tweet fromJSON(JSONObject tweetObject) {

        Tweet tweet = new Tweet();

        try {
            tweet.user = User.fromJSON(tweetObject.getJSONObject("user"));

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
                    for (int i = 0; i < mediasJSONArray.length(); i++) {

                        JSONObject multimediaJSONObject = mediasJSONArray.getJSONObject(i);
                        TwitterMedia twitterMedia = TwitterMedia.fromJSON(tweet,multimediaJSONObject);
                        if (tweet.twitterMedias == null){
                            tweet.twitterMedias = new ArrayList<TwitterMedia>();
                        }

                        tweet.twitterMedias.add(twitterMedia);
                    }
                }
            } else {
                Log.d("DEBUG", "No extended entities");
            }

            if (tweet.twitterMedias != null && tweet.twitterMedias.size() > 0) {
                // Remove the hyperlink in the text
                String s = tweetObject.getString("text");
                if (s.length() > 0) {
                    s = s.replaceAll("https?://\\S+\\s?", "");
                }
                tweet.text = s;
            } else {
                tweet.text = tweetObject.getString("text");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public void persistMedia () {

        if (twitterMedias == null) {return;}

        Log.d("DEBUG", "While saving Media size:" + this.getUid() + ":" + twitterMedias.size());
        for (TwitterMedia media: twitterMedias) {
            media.save();
        }
    }

    public static List<Tweet> all() {
        return new Select().from(Tweet.class).orderBy("timestamp DESC").execute();
    }

    public String getFavoriteCount() {
        return ""+favorite_count;
    }

    public String getRetweetCount() {
        return ""+retweet_count;
    }

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

    public long getUid() {
        return uid;
    }

    public String getSecondImageURL() {

        TwitterMedia media = null;
        if (twitterMedias!= null && twitterMedias.size() > 1) {
            media = twitterMedias.get(1);
        }

        return media.getMediaUrl();
    }

    public String getThirdImageURL() {

        TwitterMedia media = null;
        if (twitterMedias!= null && twitterMedias.size() > 2) {
            media = twitterMedias.get(2);
        }

        return media.getMediaUrl();
    }

    public String getForthImageURL() {

        TwitterMedia media = null;
        if (twitterMedias!= null && twitterMedias.size() > 3) {
            media = twitterMedias.get(3);
        }

        return media.getMediaUrl();
    }
}
