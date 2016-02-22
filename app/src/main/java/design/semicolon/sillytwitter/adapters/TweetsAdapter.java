package design.semicolon.sillytwitter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.activity.TimelineActivity;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.views.TweetViewHolder;
import design.semicolon.sillytwitter.views.TweetWithFourImagesViewHolder;
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

    private HashMap<String,Boolean> mTweetInRecyclerViewMap = new HashMap<String,Boolean>();

    private List<Tweet> mTweets;
    Context mContext;

    public TweetsAdapter(TimelineActivity timelineActivity) {
        this.mContext = timelineActivity;
    }

    public void addTweets(List<Tweet> tweets) {

        if (mTweets == null) {
            mTweets = new ArrayList<Tweet>();
        }

        for (Tweet tweet: tweets) {
            Log.d("DEBUG", "tweet:"+tweet.getId()+" content:"+tweet.getText());
            if (!mTweetInRecyclerViewMap.containsKey(tweet.getId())) {
                // Not in the list, add it
                Log.d("DEBUG", "Adding tweet:"+tweet.getId());
                mTweetInRecyclerViewMap.put(tweet.getId().toString(), true);
                mTweets.add(tweet);
            } else {
                Log.d("DEBUG", "Not Adding tweet:"+tweet.getId()+" content:"+tweet.getText());
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
                viewHolder = new TweetWithOneImageViewHolder(inflater.inflate(R.layout.tweet_with_1_image, viewGroup, false), this.mContext);
                break;
            case VIEW_WITH_2_IMAGES:
                viewHolder = new TweetWithTwoImagesViewHolder(inflater.inflate(R.layout.tweet_with_2_images, viewGroup, false), this.mContext);
                break;
            case VIEW_WITH_3_IMAGES:
                viewHolder = new TweetWithThreeImagesViewHolder(inflater.inflate(R.layout.tweet_with_3_images, viewGroup, false), this.mContext);
                break;
            case VIEW_WITH_4_IMAGES:
                viewHolder = new TweetWithFourImagesViewHolder(inflater.inflate(R.layout.tweet_with_4_images, viewGroup, false), this.mContext);
                break;
            case VIEW_WITH_4PLUS_IMAGES:
                viewHolder = new TweetWithFourPlusImagesViewHolder(inflater.inflate(R.layout.tweet_with_4plus_images, viewGroup, false), this.mContext);
                break;
            default:
                viewHolder = new TweetViewHolder(inflater.inflate(R.layout.tweet_with_no_images, viewGroup, false), this.mContext);
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

        if (tweet.medias() != null) {
            numberOfMedias = tweet.medias().size();
        }

        if (numberOfMedias == 1) {
            return VIEW_WITH_1_IMAGE;
        } else if (numberOfMedias == 2){
            return VIEW_WITH_2_IMAGES;
        } else if (numberOfMedias == 3){
            return VIEW_WITH_3_IMAGES;
        } else if (numberOfMedias == 4) {
            return VIEW_WITH_4_IMAGES;
        } else if (numberOfMedias > 4) {
            return VIEW_WITH_4PLUS_IMAGES;
        }

        // Tweet has no image
        return VIEW_WITH_NO_IMAGE;
    }
}
