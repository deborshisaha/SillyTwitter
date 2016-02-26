package design.semicolon.sillytwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "TwitterMedia")
public class TwitterMedia extends Model implements Serializable {

    private MediaType mediaType;

    @Column(name = "meadia_id", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long imgId;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "type")
    private String type;

    @Column(name = "Tweet")
    private Tweet tweet;

    @Column(name = "video_duration")
    private int videoDuration;

    @Column(name = "low_bitrate_media_url")
    private String lowBitrateMediaURL;

    @Column(name = "high_bitrate_media_url")
    private String highBitrateMediaURL;

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
            media.type = twitterMediaObject.getString("type");

            if (media.type.equals("video")) {
                media.mediaType = MediaType.MEDIA_TYPE_VIDEO;
            } else if (media.type.equals("image")) {
                media.mediaType = MediaType.MEDIA_TYPE_IMAGE;
            } else {
                media.mediaType = MediaType.MEDIA_TYPE_UNKNOWN;
            }

            media.tweet = tweet;

            if (media.mediaType == MediaType.MEDIA_TYPE_VIDEO) {
                extractVideoInformation(media, twitterMediaObject.getJSONObject("video_info"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    private static void extractVideoInformation(TwitterMedia media, JSONObject mediaVideoObject) {
        int maxBitRate = Integer.MIN_VALUE;
        int minBitRate = Integer.MAX_VALUE;

        try {

            if (mediaVideoObject.optJSONArray("variants") != null) {
                JSONArray arrayOfVariants = mediaVideoObject.getJSONArray("variants");
                for (int i = 0; i < arrayOfVariants.length() ; i++) {
                    JSONObject variantObject = (JSONObject)arrayOfVariants.get(i);

                    if (variantObject.optDouble("bitrate") != 0) {
                        maxBitRate = Math.max(maxBitRate,variantObject.getInt("bitrate"));
                        minBitRate = Math.min(minBitRate, variantObject.getInt("bitrate"));
                    }
                }

                for (int i = 0; i < arrayOfVariants.length() ; i++) {
                    JSONObject variantObject = (JSONObject)arrayOfVariants.get(i);

                    if (variantObject.optDouble("bitrate") != 0) {
                        int br = variantObject.getInt("bitrate");

                        if (br == minBitRate ) {
                            media.lowBitrateMediaURL = variantObject.getString("url");
                        } else if (br == maxBitRate) {
                            media.highBitrateMediaURL = variantObject.getString("url");
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<TwitterMedia> all() {
        return new Select().from(TwitterMedia.class).execute();
    }

    public static void deleteAll() {
        new Delete().from(TwitterMedia.class).execute();
    }

    public enum MediaType {
        MEDIA_TYPE_VIDEO,
        MEDIA_TYPE_IMAGE,
        MEDIA_TYPE_UNKNOWN
    }

    public MediaType getMediaType() {

        if (mediaType == MediaType.MEDIA_TYPE_UNKNOWN) {
            if (type.equals("video")) {
                mediaType = MediaType.MEDIA_TYPE_VIDEO;
            } else if (type.equals("photo")){
                mediaType = MediaType.MEDIA_TYPE_IMAGE;
            }
        }
        return mediaType;
    }

    public String getHighBitrateMediaURL() {
        return highBitrateMediaURL;
    }

    public String getLowBitrateMediaURL() {
        return lowBitrateMediaURL;
    }

    public int getVideoDuration() {
        return videoDuration;
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
    public TwitterMedia() {
        super();
        this.mediaType = MediaType.MEDIA_TYPE_UNKNOWN;
    }
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


