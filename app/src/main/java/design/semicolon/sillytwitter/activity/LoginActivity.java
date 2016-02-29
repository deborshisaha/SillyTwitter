package design.semicolon.sillytwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.LoginPageAdapter;
import design.semicolon.sillytwitter.models.TwitterMedia;
import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

/**
 * Created by dsaha on 2/20/16.
 */
public class LoginActivity extends  OAuthLoginActionBarActivity<SillyTwitterClient> {

    ViewPager mViewPager;
    LoginPageAdapter mLoginPageAdapter;
    private int mActiveImage;

    @Override
    public void onLoginSuccess() {
        Intent timelineActivityIntent = new Intent(this, HomeActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginPageAdapter = new LoginPageAdapter(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mLoginPageAdapter);

        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {

            @Override
            public void transformPage(View page, float position) {

                int pageWidth = mViewPager.getMeasuredWidth() - mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
                page.setTranslationX(page.getWidth() * -position);

                if(position <= -1.0F || position >= 1.0F) {
                    page.setAlpha(0.0F);
                    page.setVisibility(View.GONE);
                } else if( position == 0.0F ) {
                    page.setAlpha(1.0F);
                    page.setVisibility(View.VISIBLE);
                } else {
                    page.setAlpha(1.0F - Math.abs(position));
                    page.setTranslationX(-position * (pageWidth / 2));
                    page.setVisibility(View.VISIBLE);
                }

            }
        });
    }
}
