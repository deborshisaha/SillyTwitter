package design.semicolon.sillytwitter.restclient;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by dsaha on 2/20/16.
 */
public class SillyTwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "27EwXuSJkueYA9Yb0weDnpZFa";       // Change this
    public static final String REST_CONSUMER_SECRET = "HtPSzUbwVMkCGRMjaDzmLomPH0lJ9db1rr6iGGUbW089kcKEam"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpsillytwitter";

    public SillyTwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeLine(int count, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String urlString = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();

        Log.d("SillyTwitterClient", "count: " + count + "since_id: "+since_id + "max_id: "+max_id);

        if (count != 0) {
            params.put("count", count);
        }

        if (since_id != 0) {
            params.put("since_id", since_id);
        }

        if (max_id != 0) {
            params.put("max_id", max_id);
        }

        getClient().get(urlString, params, handler);
    }

    public void getUserTimelineTweets(int fetchCount, long user_id, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String urlString = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();

        Log.d("SillyTwitterClient", "count: " + fetchCount + "since_id: "+since_id + "max_id: "+max_id);

        if (user_id != 0) {
            params.put("user_id", user_id);
        }

        if (fetchCount != 0) {
            params.put("count", fetchCount);
        }

        if (since_id != 0) {
            params.put("since_id", since_id);
        }

        if (max_id != 0) {
            params.put("max_id", max_id);
        }

        getClient().get(urlString, params, handler);
    }

    public void getUserLikedTweets(int fetchCount, long user_id, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String urlString = getApiUrl("favorites/list.json");
        RequestParams params = new RequestParams();

        Log.d("SillyTwitterClient", "count: " + fetchCount + "since_id: "+since_id + "max_id: "+max_id);

        if (user_id != 0) {
            params.put("user_id", user_id);
        }

        if (fetchCount != 0) {
            params.put("count", fetchCount);
        }

        if (since_id != 0) {
            params.put("since_id", since_id);
        }

        if (max_id != 0) {
            params.put("max_id", max_id);
        }

        getClient().get(urlString, params, handler);
    }

    public void postTweet(String text, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        getClient().post(apiUrl, params, handler);
    }

    public void postCreateLike(long id, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void postDestroyLike(long id, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void verifyAccountCredentials (AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, handler);
    }

    public void getUserMentions(int count, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String urlString = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();

        if (count != 0) {
            params.put("count", count);
        }

        if (since_id != 0) {
            params.put("since_id", since_id+1);
        }

        if (max_id != 0) {
            params.put("max_id", max_id - 1);
        }

        getClient().get(urlString, params, handler);
    }

    public void postCreateTweet(long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet/"+uid+".json");
        getClient().get(apiUrl, handler);
    }

    public void postDestroyTweet(long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/unretweet/"+uid+".json");
        getClient().get(apiUrl, handler);
    }


}
