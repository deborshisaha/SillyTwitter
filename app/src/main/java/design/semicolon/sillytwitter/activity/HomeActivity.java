package design.semicolon.sillytwitter.activity;

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
import design.semicolon.sillytwitter.fragments.ComposeNewTweetFragment;
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
        viewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
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

//        User user = User.currentUser(TimelineActivity.this);
//        ComposeNewTweetFragment frag = ComposeNewTweetFragment.newInstance(user, TimelineActivity.this, new ComposeNewTweetFragment.OnTweetPostedHandler() {
//            @Override
//            public void onTweetPosted(Tweet newTweet) {
//                mTweetAdapter.addTweet(newTweet);
//                mTweetAdapter.notifyItemInserted(0);
//                mTimelineRecyclerView.smoothScrollToPosition(0);
//            }
//        });
//
//        FragmentManager fm = getSupportFragmentManager();
//        frag.show(fm, "compose_new_tweet_fragment");
    }
}
