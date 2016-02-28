package design.semicolon.sillytwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONException;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.HomePagerAdapter;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.fragments.ComposeTweetFragment;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

/**
 * Created by dsaha on 2/25/16.
 */
public class HomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager(), new TweetViewHolderEventListener() {

            @Override
            public void didPressProfilePicture(Tweet tweet) {
                Log.d("DEBUG", "didPressProfilePicture: "+tweet.getUser().getFullName());
                showUserProfile(tweet.getUser());
                return;
            }

            @Override
            public Tweet didPressReplyOnTweetWithId(Tweet tweet) {
                replyToTweetDialog(tweet);
                return tweet;
            }

            @Override
            public Tweet didPressLikeOnTweetWithId(Tweet tweet) {
                Log.d("DEBUG", "didPressLikeOnTweetWithId: " + tweet.getUid());

                if (tweet.isLikedByUser()) {
                    try {
                        (new TweetDaoImpl(HomeActivity.this)).postTweetUnliked(tweet);
                        tweet.setLikedByUser(!tweet.isLikedByUser());
                    } catch (NoNetworkConnectionException e) {
                        return tweet;
                    }
                } else {
                    try {
                        (new TweetDaoImpl(HomeActivity.this)).postTweetLike(tweet);
                        tweet.setLikedByUser(!tweet.isLikedByUser());
                    } catch (NoNetworkConnectionException e) {
                        return tweet;
                    }
                }

                return tweet;
            }

            @Override
            public void didSelectTweet(Tweet tweet) {
                showTweetDetail(tweet);
            }

            @Override
            public Tweet didPressRetweetOnTweetWithId(Tweet tweet) {
                Log.d("DEBUG", "didPressRetweetOnTweetWithId: "+tweet.getUid() +":"+tweet.isRetweetedByUser());
                if (tweet.isRetweetedByUser() == true) {
                    try {
                        Log.d("DEBUG", "isRetweetedByUser: "+tweet.getUid());
                        (new TweetDaoImpl(HomeActivity.this)).postUndoRetweet(tweet);
                        tweet.setRetweetedByUser(!tweet.isRetweetedByUser());
                    } catch (NoNetworkConnectionException e) {
                        return tweet;
                    }
                } else {
                    try {
                        Log.d("DEBUG", "Not isRetweetedByUser: "+tweet.getUid());
                        (new TweetDaoImpl(HomeActivity.this)).postRetweet(tweet);
                        tweet.setRetweetedByUser(!tweet.isRetweetedByUser());
                    } catch (NoNetworkConnectionException e) {
                        return tweet;
                    }
                }

                return tweet;
            }
        }));

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_new_tweet:{
                try {
                    showComposeNewTweetDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.menu_profile:{
                Log.d("DEBUG", "Do something");
                showUserProfile(User.currentUser(this));
            }
            case R.id.menu_message:{
                Log.d("DEBUG", "Write message to friend");
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showComposeNewTweetDialog() throws JSONException {

        User user = User.currentUser(HomeActivity.this);
        ComposeTweetFragment frag = ComposeTweetFragment.newInstance(user, null, HomeActivity.this, null});

        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "compose_tweet_fragment");
    }

    private void replyToTweetDialog(Tweet tweet) {
        User user = User.currentUser(HomeActivity.this);
        ComposeTweetFragment frag = ComposeTweetFragment.newInstance(user, tweet, HomeActivity.this, null);

        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "compose_tweet_fragment");
    }

    private void showUserProfile (User user) {
        Intent intent = new Intent(this, UserProfileActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(User.USER, user);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showTweetDetail (Tweet tweet) {
        Intent intent = new Intent(this, TweetDetailedActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable( Tweet.TWEET, tweet);

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
