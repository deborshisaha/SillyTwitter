package design.semicolon.sillytwitter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.visibility_utils.items.ListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.activity.TimelineActivity;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.views.TweetVideoViewHolder;
import design.semicolon.sillytwitter.views.TweetViewHolder;
import design.semicolon.sillytwitter.views.TweetWithFourPlusImagesViewHolder;
import design.semicolon.sillytwitter.views.TweetWithOneImageViewHolder;
import design.semicolon.sillytwitter.views.TweetWithThreeImagesViewHolder;
import design.semicolon.sillytwitter.views.TweetWithTwoImagesViewHolder;

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int VIEW_WITH_NO_IMAGE = 0;
    public final int VIEW_WITH_1_IMAGE = 1;
    public final int VIEW_WITH_2_IMAGES = VIEW_WITH_1_IMAGE << 1;
    public final int VIEW_WITH_3_IMAGES = VIEW_WITH_1_IMAGE << 2;
    public final int VIEW_WITH_4_IMAGES = VIEW_WITH_1_IMAGE << 3;
    public final int VIEW_WITH_4PLUS_IMAGES = VIEW_WITH_1_IMAGE << 4;
    public final int VIEW_WITH_VIDEO = VIEW_WITH_1_IMAGE << 5;

    private HashMap<String,Boolean> mTweetInRecyclerViewMap = new HashMap<String,Boolean>();
    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    private List<Tweet> mTweets;
    Context mContext;
    private boolean empty;

    public TweetsAdapter(Context context, TweetViewHolderEventListener tweetViewHolderEventListener) {
        this.mContext = context;
        this.mTweetViewHolderEventListener = tweetViewHolderEventListener;
    }

    public void addTweets(List<Tweet> tweets) {

        if (mTweets == null) {
            mTweets = new ArrayList<Tweet>();
        }

        for (Tweet tweet: tweets) {
            Log.d("DEBUG", "tweet:"+tweet.getId()+" content:"+tweet.getText());
            if (!mTweetInRecyclerViewMap.containsKey(tweet.getUidStr())) {
                // Not in the list, add it
                Log.d("DEBUG", "Adding tweet:"+tweet.getUidStr());
                mTweetInRecyclerViewMap.put(tweet.getUidStr().toString(), true);
                mTweets.add(tweet);
            } else {
                Log.d("DEBUG", "Not Adding tweet:"+tweet.getUidStr()+" content:"+tweet.getText());
            }
        }

        Log.d("DEBUG", "Number of tweets:"+mTweets.size());
    }

    public void clearTweets() {

        if (mTweets != null && mTweets.size() != 0) {
            mTweets.removeAll(mTweets);
        }

        if (mTweetInRecyclerViewMap != null && mTweetInRecyclerViewMap.size() != 0) {
            mTweetInRecyclerViewMap.clear();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {

            case VIEW_WITH_1_IMAGE:
                viewHolder = new TweetWithOneImageViewHolder(inflater.inflate(R.layout.tweet_with_1_image, viewGroup, false), this.mContext, this.mTweetViewHolderEventListener);
                break;
            case VIEW_WITH_2_IMAGES:
                viewHolder = new TweetWithTwoImagesViewHolder(inflater.inflate(R.layout.tweet_with_2_images, viewGroup, false), this.mContext, this.mTweetViewHolderEventListener);
                break;
            case VIEW_WITH_3_IMAGES:
                viewHolder = new TweetWithThreeImagesViewHolder(inflater.inflate(R.layout.tweet_with_3_images, viewGroup, false), this.mContext, this.mTweetViewHolderEventListener);
                break;
            case VIEW_WITH_4PLUS_IMAGES:
                viewHolder = new TweetWithFourPlusImagesViewHolder(inflater.inflate(R.layout.tweet_with_4plus_images, viewGroup, false), this.mContext, this.mTweetViewHolderEventListener);
                break;
            case VIEW_WITH_VIDEO:
                viewHolder = new TweetVideoViewHolder(inflater.inflate(R.layout.tweet_with_video, viewGroup, false), this.mContext, this.mTweetViewHolderEventListener);
                break;
            default:
                viewHolder = new TweetViewHolder(inflater.inflate(R.layout.tweet_with_no_images, viewGroup, false), this.mContext, this.mTweetViewHolderEventListener);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Tweet tweet = mTweets.get(position);

        if (holder instanceof TweetViewHolder){
            TweetViewHolder tweetViewHolder = (TweetViewHolder) holder;
            tweetViewHolder.decorateViewWithTweet(tweet);
        }
    }

    @Override
    public int getItemCount() {
        if ( mTweets != null) {
            return mTweets.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        Tweet tweet = mTweets.get(position);

        int numberOfMedias = 0;

        if (tweet!=null && tweet.getTwitterMedias() != null) {
            Log.d("DEBUG", "Media size:"+tweet.getUid()+ ":"+tweet.getTwitterMedias().size());
            numberOfMedias = tweet.getTwitterMedias().size();
        }

        if (numberOfMedias > 0 && tweet.hasVideo()) {
            return VIEW_WITH_VIDEO;
        } else if (numberOfMedias == 1) {
            return VIEW_WITH_1_IMAGE;
        } else if (numberOfMedias == 2){
            return VIEW_WITH_2_IMAGES;
        } else if (numberOfMedias == 3){
            return VIEW_WITH_3_IMAGES;
        } else if (numberOfMedias >= 4) {
            return VIEW_WITH_4PLUS_IMAGES;
        }

        // Tweet has no image
        return VIEW_WITH_NO_IMAGE;
    }

    public void addTweet(Tweet newTweet) {

        if (mTweets == null) {
            mTweets = new ArrayList<Tweet>();
        }

        if (!mTweetInRecyclerViewMap.containsKey(newTweet.getUidStr())) {
            mTweetInRecyclerViewMap.put(newTweet.getUidStr(), true);
            mTweets.add(0, newTweet);
        }
    }

    public Tweet getLastTweet() {

        if (mTweets.size() != 0) {
            return mTweets.get(mTweets.size() - 1);
        }
        return null;
    }

    public Tweet getTweetAtPosition(int position) {

        if (position < mTweets.size()) {
            return mTweets.get(position);
        }

        return null;
    }
}
