package design.semicolon.sillytwitter.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import org.json.JSONException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.TweetsAdapter;
import design.semicolon.sillytwitter.dao.TweetDao;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.dao.UserDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.fragments.ComposeTweetFragment;
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
    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActiveAndroid.setLoggingEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_action_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Bundle
        ButterKnife.bind(this);

        // Initialize the adapter
        //mTweetAdapter = new TweetsAdapter(TimelineActivity.this);

        // Set the layout manager
        mLinearLayoutManager = new LinearLayoutManager(this);
        mTimelineRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set adapter
        mTimelineRecyclerView.setAdapter(mTweetAdapter);

        // Listener listening to data load
//        mOnTweetsLoadedListener = new OnTweetsLoadedListener() {
//            @Override
//            public void onTweetsLoaded(List<Tweet> tweets, boolean cached) {
//                loading = false;
//                swipeContainer.setRefreshing(false);
//
//                if (tweets != null) {
//                    mTweetAdapter.addTweets(tweets);
//                    mTweetAdapter.notifyDataSetChanged();
//                }
//            }
//        };

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
            mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        try {
            mUserDaoImpl.reloadUser(TimelineActivity.this, null);
        } catch (NoNetworkConnectionException e) {
            Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
        }

        /*
        if (mListItemVisibilityCalculator == null) {
            mListItemVisibilityCalculator =
                    new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), (List<? extends ListItem>) mTweetAdapter);
        }

        if (mItemsPositionGetter == null) {
            mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLinearLayoutManager, mTimelineRecyclerView);
        }
        */

        mTimelineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
                /*
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mTweetAdapter.isEmpty()){

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLinearLayoutManager.findFirstVisibleItemPosition(),
                            mLinearLayoutManager.findLastVisibleItemPosition());
                }*/
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*
                if(!mTweetAdapter.isEmpty()){
                    mListItemVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLinearLayoutManager.findFirstVisibleItemPosition(),
                            mLinearLayoutManager.findLastVisibleItemPosition() - mLinearLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
                */

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                if (dy > 0) {

                    if (!loading && ((visibleItemCount + firstVisibleItem) * 100 / totalItemCount) > 90) {
                        loading = true;

                        Tweet tweet = mTweetAdapter.getLastTweet();

                        if (tweet != null) {
                            long max_id = tweet.getUid();

                            try {
                                mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 0, max_id, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
                            } catch (NoNetworkConnectionException e) {
                                Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        /*
        mTimelineRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                // IF scrolling down
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                Log.d("DEBUG", "visibleItemCount :"+visibleItemCount);
                Log.d("DEBUG", "firstVisibleItem :"+firstVisibleItem);
                Log.d("DEBUG", "firstCompletelyVisibleItem :"+ mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());

                Log.d("DEBUG", "dx :"+ dx + "dy :" + dy);

                if (dy > 0) {

                    if (!loading && ((visibleItemCount + firstVisibleItem)*100/totalItemCount) > 90) {
                        loading = true;

                        Tweet tweet = mTweetAdapter.getLastTweet();

                        if (tweet != null) {
                            long max_id = tweet.getUid();

                            try {
                                mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 0, max_id, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
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
                    mTweetDaoImpl.fetchTimelineTweets(TimelineActivity.this, 1, 0, mOnTweetsLoadedListener, TweetDao.CachingStrategy.CacheOnly);
                } catch (NoNetworkConnectionException e) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(TimelineActivity.this, e.getReason() + ' ' + e.getRemedy(), Toast.LENGTH_LONG).show();
                }
            }
        });
*/
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
        User user = User.currentUser(TimelineActivity.this);

        ComposeTweetFragment frag = ComposeTweetFragment.newInstance(user, null, TimelineActivity.this, new ComposeTweetFragment.OnTweetPostedHandler() {
            @Override
            public void onTweetPosted(Tweet newTweet) {
                mTweetAdapter.addTweet(newTweet);
                mTweetAdapter.notifyItemInserted(0);
                mTimelineRecyclerView.smoothScrollToPosition(0);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "compose_tweet_fragment");
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        if(!mTweetAdapter.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mTimelineRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLinearLayoutManager.findFirstVisibleItemPosition(),
                            mLinearLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }
    */
}
