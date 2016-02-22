package design.semicolon.sillytwitter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.TweetsAdapter;
import design.semicolon.sillytwitter.dao.TweetDao;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.dao.UserDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.fragments.ComposeNewTweetFragment;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.listerners.OnUsersLoadedListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

public class TimelineActivity extends AppCompatActivity {

    @Bind(R.id.timeline)
    RecyclerView mTimelineRecyclerView;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private OnTweetsLoadedListener mOnTweetsLoadedListener;
    private OnUsersLoadedListener mOnUsersLoadedListener;

    private TweetDaoImpl mTweetDaoImpl;
    private UserDaoImpl mUserDaoImpl;
    private TweetsAdapter mTweetAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean loading = true;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActiveAndroid.setLoggingEnabled(true);

        Tweet.deleteAll();

        // Bundle
        ButterKnife.bind(this);

        // Initialize the adapter
        mTweetAdapter = new TweetsAdapter(TimelineActivity.this);

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(this);
        mTimelineRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        mTimelineRecyclerView.setAdapter(mTweetAdapter);

        // Listener listening to data load
        mOnTweetsLoadedListener = new OnTweetsLoadedListener() {
            @Override
            public void onTweetsLoaded(List<Tweet> tweets, boolean cached) {
                loading = false;
                swipeContainer.setRefreshing(false);

                if (tweets != null) {
                    mTweetAdapter.addTweets(tweets);
                    mTweetAdapter.notifyDataSetChanged();
                }
            }
        };

        if (mTweetDaoImpl == null) {
            mTweetDaoImpl = new TweetDaoImpl();
        }

        if (mUserDaoImpl == null) {
            mUserDaoImpl = new  UserDaoImpl();
        }

        /**
         * Load data on first load
         */
        try {
            mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.NetworkOnly);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        try {
            mUserDaoImpl.reloadUser(TimelineActivity.this, null);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }


        mTimelineRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {

                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = mLinearLayoutManager.getItemCount();
                    firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                    Log.d("DEBUG", "visibleItemCount: "+visibleItemCount+" totalItemCount: "+totalItemCount+" firstVisibleItem: "+firstVisibleItem);

                    if (!loading && ((visibleItemCount + firstVisibleItem)*100/totalItemCount) > 90) {
                        loading = true;

                        Tweet tweet = mTweetAdapter.getLastTweet();

                        if (tweet != null) {
                            long max_id = tweet.getUid();

                            try {
                                mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 0, max_id, mOnTweetsLoadedListener, TweetDao.CachingStrategy.NetworkOnly);
                            } catch (NoNetworkConnectionException e) {
                                Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    swipeContainer.setRefreshing(true);
                    mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.NetworkOnly);
                } catch (NoNetworkConnectionException e) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                }
            }
        });
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showComposeNewTweetDialog() throws JSONException {
        User user = User.currentUser(TimelineActivity.this);

        ComposeNewTweetFragment frag = ComposeNewTweetFragment.newInstance(user, TimelineActivity.this, new ComposeNewTweetFragment.OnTweetPostedHandler() {
            @Override
            public void onTweetPosted(Tweet newTweet) {
                mTweetAdapter.addTweet(newTweet);
                mTweetAdapter.notifyItemInserted(0);
                mTimelineRecyclerView.smoothScrollToPosition(0);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "compose_new_tweet_fragment");
    }

}
