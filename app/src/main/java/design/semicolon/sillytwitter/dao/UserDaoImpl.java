package design.semicolon.sillytwitter.dao;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import design.semicolon.sillytwitter.SillyTwitterApplication;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.listerners.OnUsersLoadedListener;
import design.semicolon.sillytwitter.models.User;
import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

/**
 * Created by dsaha on 2/21/16.
 */
public class UserDaoImpl {

    private SillyTwitterClient client;

    public void reloadUser(final Context context, OnUsersLoadedListener onUsersLoadedListener) throws NoNetworkConnectionException {

        if (client == null) {
            client = SillyTwitterApplication.getRestClient();
        }

        client.verifyAccountCredentials(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                User.setCurrentUser(context, jsonObject);
            }


        });
    }
}
