package design.semicolon.sillytwitter.activity;

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
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.TweetsAdapter;
import design.semicolon.sillytwitter.dao.TweetDao;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.fragments.ComposeNewTweetFragment;
import design.semicolon.sillytwitter.listerners.OnTweetsLoadedListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

public class TimelineActivity extends AppCompatActivity {

    @Bind(R.id.timeline)
    RecyclerView mTimelineRecyclerView;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private OnTweetsLoadedListener mOnTweetsLoadedListener;
    private TweetDaoImpl mTweetDaoImpl;
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
                loading = true;
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

        /**
         * Load data on first load
         */
        try {
            mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.NetworkOnly);
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
                    }
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.NetworkOnly);
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
                showComposeNewTweetDialog();
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showComposeNewTweetDialog() {
        User user = new User("Deborshi Saha", "deborshisaha", "http://pbs.twimg.com/profile_images/648751321026138112/8z47ePnq.jpg");

        ComposeNewTweetFragment frag = ComposeNewTweetFragment.newInstance(user, TimelineActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "compose_new_tweet_fragment");
    }
}
