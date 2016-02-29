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
import org.w3c.dom.Text;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import design.semicolon.sillytwitter.helpers.DateHelper;

@Table(name = "Tweet")
public class Tweet extends Model implements Serializable {

    public static final String TWEET = "Tweet";

    private String secondImageURL;

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

    @Column(name = "user_mentioned")
    private boolean user_mentioned;

    @Column(name = "favorited")
    private boolean favorited;

    @Column(name = "retweeted")
    private boolean retweeted;

    @Column(name = "retweeted_author_name")
    private String retweeted_author_name;

    private List<TwitterMedia> twitterMedias;

    public User getUser() {
        return user;
    }

    public List<TwitterMedia> allMedia() {

        List<TwitterMedia> mediaList = TwitterMedia.all();

        for (TwitterMedia media:mediaList) {
            Log.d("DEBUG", media.getUid()+":"+media.getTweet()+":"+media.getTweet().getUid());
        }

        if (twitterMedias == null){

            Tweet tweet = new Select()
                    .from(Tweet.class)
                    .where("uid= ?", this.uid)
                    .executeSingle();

            if (tweet != null && tweet.getId() != 0) {
                Log.d("DEBUG", tweet.getId()+":IN IN");
                twitterMedias = new Select().from(TwitterMedia.class).where("tweet = ?", tweet.getId()).execute();
            }
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

            // Original Tweet exists
            if (tweetObject.optJSONObject("retweeted_status") != null) {
                tweet = Tweet.fromJSON(tweetObject.getJSONObject("retweeted_status"));

                User u = User.fromJSON(tweetObject.getJSONObject("user"));
                tweet.retweeted_author_name = u.getFullName();
            } else {
                tweet.user = User.fromJSON(tweetObject.getJSONObject("user"));
                tweet.uid = tweetObject.getLong("id");
                tweet.uidStr = tweetObject.getString("id_str");
                tweet.createdAt = tweetObject.getString("created_at");
                tweet.retweet_count = tweetObject.getInt("retweet_count");
                tweet.favorite_count = tweetObject.getInt("favorite_count");
                tweet.timestamp = DateHelper.convertToDate(tweetObject.getString("created_at")).getTime();
                tweet.favorited = tweetObject.getBoolean("favorited");
                tweet.retweeted = tweetObject.getBoolean("retweeted");

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public boolean wasUserMentioned(User user, JSONObject tweetObject) {

        if (tweetObject == null || user == null) {return  false;}

        try {
            if (tweetObject.optJSONObject("entities") != null) {
                JSONObject entitiesJSONObject = null;

                entitiesJSONObject = tweetObject.getJSONObject("entities");

                if (entitiesJSONObject.optJSONArray("user_mentions") != null) {
                    JSONArray mentionsArray = entitiesJSONObject.getJSONArray("user_mentions");
                    for (int i = 0; i < mentionsArray.length(); i++) {
                        JSONObject userMentionObject = mentionsArray.getJSONObject(i);
                        return userMentionObject.getString("screen_name").equals(user.getUserName());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
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

    public boolean hasVideo() {

        for (TwitterMedia media: twitterMedias) {
            if (media.getMediaType() == TwitterMedia.MediaType.MEDIA_TYPE_VIDEO){
                return true;
            }
        }

        return false;
    }

    public static List<Tweet> getTweetsUserWasMentioned() {
        return new Select().from(Tweet.class).where("user_mentioned = ?", true).orderBy("timestamp DESC").executeSingle();
    }

//    public static List<Tweet> tweetsByUser(User user) {
//
//        User tempUser = null;
//        Log.d("DEBUG", "user.getId(): "+user.getId());
//        if (user.getId() == null) {
//            tempUser = User.getUserWithScreenName(user);
//        } else {
//            tempUser = user;
//        }
//
//        if (tempUser.getUid() != 0 ) {
//            return new Select().from(Tweet.class).where("user = ?", tempUser.getId()).orderBy("timestamp DESC").execute();
//        }
//
//        return null;
//    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserMentioned(boolean um) {
        this.user_mentioned = um;
    }

    public boolean isLikedByUser() {
        return this.favorited;
    }

    public void setLikedByUser(boolean liked) {
        this.favorited = liked;
        if (liked) {
            favorite_count = favorite_count + 1;
        } else {
            favorite_count = favorite_count - 1;
        }
    }

    public void setRetweetedByUser(boolean retweeted) {
        this.retweeted = retweeted;
        if (retweeted){
            retweet_count = retweet_count+1;
        } else {
            retweet_count = retweet_count-1;
        }

    }

    public String getRetweetAuthorName() {
        return retweeted_author_name;
    }

    public boolean isRetweetedByUser() {
        return this.retweeted;
    }

    public Tweet saveIfNecessaryElseGetTweet() {
        long rId = this.uid;
        Tweet existingTweet = new Select().from(Tweet.class).where("uid = ?", rId).executeSingle();
        if (existingTweet != null) {
            return existingTweet;
        } else {
            this.save();
            return this;
        }
    }

    public List<Tweet> allAfterMaxId(long max_id) {
        return new Select().from(Tweet.class).where("uid < ?", max_id).orderBy("timestamp DESC").execute();
    }

}
