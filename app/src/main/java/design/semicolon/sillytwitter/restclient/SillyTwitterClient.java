package design.semicolon.sillytwitter.restclient;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by dsaha on 2/20/16.
 */
public class SillyTwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "O1iJFV7Q4VVOWVkurejzcbHTI";       // Change this
    public static final String REST_CONSUMER_SECRET = "cPTz3d75lMbwqQIptSb6oM048y7TzxaKPDkscqm5QiRTCUqGph"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cpsillytwitter";

    public SillyTwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeLine(int count, AsyncHttpResponseHandler handler) {
        String urlString = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("since_id",1);
        params.put("count", count);

        getClient().get(urlString, params, handler);
    }

    public void postTweet(String text, AsyncHttpResponseHandler handler) {

        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        getClient().post(apiUrl, params, handler);
    }

}
