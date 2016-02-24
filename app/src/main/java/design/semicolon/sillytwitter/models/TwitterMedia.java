package design.semicolon.sillytwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "TwitterMedia")
public class TwitterMedia extends Model implements Serializable {

    @Column(name = "meadia_id", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long imgId;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "Tweet", index = true)
    private Tweet tweet;

    public Tweet getTweet() {
        return tweet;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public static TwitterMedia fromJSON(Tweet tweet, JSONObject twitterMediaObject) {

        TwitterMedia media = new TwitterMedia();

        try {
            media.imgId = twitterMediaObject.getLong("id");;
            media.mediaUrl = twitterMediaObject.getString("media_url");
            media.tweet = tweet;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    public static List<TwitterMedia> all() {
        return new Select().from(TwitterMedia.class).execute();
    }

    public static void deleteAll() {
        new Delete().from(TwitterMedia.class).execute();
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.id);
//        dest.writeString(this.mediaUrl);
//        dest.writeParcelable(this.tweet, 0);
//    }
//
//    public TwitterMedia() {
//    }
//
//    protected TwitterMedia(Parcel in) {
//        this.id = in.readString();
//        this.mediaUrl = in.readString();
//        this.tweet = in.readParcelable(Tweet.class.getClassLoader());
//    }
//
//    public static final Parcelable.Creator<TwitterMedia> CREATOR = new Parcelable.Creator<TwitterMedia>() {
//        public TwitterMedia createFromParcel(Parcel source) {
//            return new TwitterMedia(source);
//        }
//
//        public TwitterMedia[] newArray(int size) {
//            return new TwitterMedia[size];
//        }
//    };
}
