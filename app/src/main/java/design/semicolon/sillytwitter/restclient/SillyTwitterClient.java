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
    public static final String REST_CONSUMER_KEY = "5zCYNnn1WTZPdWNLI5L6cxUwO";       // Change this
    public static final String REST_CONSUMER_SECRET = "v06NhwfGell242oQILYWEZ9JE9x7LcfPQVxutcOAJOZoqqQxeY"; // Change this
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

}