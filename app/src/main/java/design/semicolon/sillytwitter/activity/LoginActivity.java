package design.semicolon.sillytwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.models.TwitterMedia;
import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

/**
 * Created by dsaha on 2/20/16.
 */
public class LoginActivity extends  OAuthLoginActionBarActivity<SillyTwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onLoginSuccess() {
        Intent timelineActivityIntent = new Intent(this, TimelineActivity.class);
        startActivity(timelineActivityIntent);
    }

    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    public void loginToRest(View view) {
        SillyTwitterClient client = getClient();
        if (client != null) {
            client.connect();
        }
    }
}
