package design.semicolon.sillytwitter.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

public class TweetViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.user_profile_picture_imageview)
    RoundedImageView user_profile_picture_imageview;

    @Bind(R.id.user_full_name_textview)
    TextView user_full_name_textview;

    @Bind(R.id.username_textview)
    TextView username_textview;

    @Bind(R.id.relative_time_textview)
    TextView relative_time_textview;

    @Bind(R.id.tweet_text_textview)
    TextView tweet_text_textview;

    @Bind(R.id.favorite_count_textview)
    TextView favorite_count_textview;

    @Bind(R.id.retweet_count_textview)
    TextView retweet_count_textview;

    @Bind(R.id.retweet_icon)
    ImageView retweet_icon_imageview;

    @Bind(R.id.reply_icon_imageview)
    ImageView reply_icon_imageview;

    @Bind(R.id.favorite_icon)
    ImageView favourite_icon_imageview;

    @Bind(R.id.retweeted_info_icon)
    ImageView retweeted_info_icon_imageview;

    @Bind(R.id.retweeted_author_name_textview)
    TextView retweeted_author_name_textview;

    public TweetViewHolderEventListener mTweetViewHolderEventListener;

    public Context context;

    public TweetViewHolder(View itemView) {
        super(itemView);
        if (this.getClass() == TweetViewHolder.class){
            ButterKnife.bind(this, itemView);
        }
    }

    public TweetViewHolder(View itemView, Context context, TweetViewHolderEventListener tweetViewHolderEventListener) {
        super(itemView);
        this.context = context;
        this.mTweetViewHolderEventListener = tweetViewHolderEventListener;

        if (this.getClass() == TweetViewHolder.class){
            ButterKnife.bind(this, itemView);
        }
    }

    public void decorateViewWithTweet (final Tweet tweet) {

        if (tweet == null) { return; }

        User user = tweet.getUser();

        if (user != null){
            this.user_full_name_textview.setText(user.getFullName());
            this.username_textview.setText( user.getUserName());
        }

        this.relative_time_textview.setText(tweet.getCreatedAt(true));
        this.tweet_text_textview.setText(tweet.getText());
        this.retweet_count_textview.setText(tweet.getRetweetCount());
        this.favorite_count_textview.setText(tweet.getFavoriteCount());

        if (tweet.getUser().getUserProfilePictureURLString() != null) {
            Log.d("TweetViewHolder", tweet.getUser().getUserProfilePictureURLString() );
            Glide.with(context).load(tweet.getUser().getUserProfilePictureURLString()).diskCacheStrategy(DiskCacheStrategy.ALL).into(user_profile_picture_imageview);
        }

        if (tweet.getRetweetAuthorName() == null || tweet.getRetweetAuthorName().length() == 0 ) {
            this.retweeted_info_icon_imageview.setVisibility(View.GONE);
            this.retweeted_author_name_textview.setVisibility(View.GONE);
        } else {
            this.retweeted_info_icon_imageview.setVisibility(View.VISIBLE);
            this.retweeted_author_name_textview.setVisibility(View.VISIBLE);
            this.retweeted_author_name_textview.setText(tweet.getRetweetAuthorName()+" Retweeted");
        }

        View.OnClickListener replyOnClickListener = getReplyOnClickListener(tweet);
        View.OnClickListener retweetOnClickListener = getRetweetOnClickListener(tweet);
        View.OnClickListener likedOnClickListener = getLikedOnClickListener(tweet);
        View.OnClickListener profilePictureTapOnClickListener = getProfilePictureTapOnClickListener(tweet);
        View.OnClickListener tweetTapOnClickListener = getTweetTapOnClickListener(tweet);

        this.itemView.setOnClickListener(tweetTapOnClickListener);
        this.user_profile_picture_imageview.setOnClickListener(profilePictureTapOnClickListener);
        this.retweet_icon_imageview.setOnClickListener(retweetOnClickListener);
        this.retweet_count_textview.setOnClickListener(retweetOnClickListener);
        this.reply_icon_imageview.setOnClickListener(replyOnClickListener);
        this.favourite_icon_imageview.setOnClickListener(likedOnClickListener);
        this.favorite_count_textview.setOnClickListener(likedOnClickListener);

        if (tweet.isRetweetedByUser()) {
            this.retweet_icon_imageview.setImageResource(R.drawable.ic_retweet_green);
        } else {
            this.retweet_icon_imageview.setImageResource(R.drawable.ic_retweet_grey);
        }

        if (tweet.isLikedByUser()) {
            this.favourite_icon_imageview.setImageResource(R.drawable.ic_fav_red);
        } else {
            this.favourite_icon_imageview.setImageResource(R.drawable.ic_fav_grey);
        }
    }

    private View.OnClickListener getProfilePictureTapOnClickListener(final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mTweetViewHolderEventListener != null) {
                    mTweetViewHolderEventListener.didPressProfilePicture(tweet);
                }
            }
        };
    }

    private View.OnClickListener getReplyOnClickListener (final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mTweetViewHolderEventListener != null) {
                    mTweetViewHolderEventListener.didPressReplyOnTweetWithId(tweet);
                }
            }
        };
    }

    private View.OnClickListener getRetweetOnClickListener (final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mTweetViewHolderEventListener != null) {
                    Tweet resultingTweet = mTweetViewHolderEventListener.didPressRetweetOnTweetWithId(tweet);
                    if (resultingTweet.isRetweetedByUser()) {
                        retweet_icon_imageview.setImageResource(R.drawable.ic_retweet_green);
                    } else {
                        retweet_icon_imageview.setImageResource(R.drawable.ic_retweet_grey);
                    }
                    retweet_count_textview.setText(tweet.getRetweetCount());
                }
            }
        };
    }

    private View.OnClickListener getLikedOnClickListener (final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mTweetViewHolderEventListener != null) {
                    Tweet resultingTweet = mTweetViewHolderEventListener.didPressLikeOnTweetWithId(tweet);
                    if (resultingTweet.isLikedByUser()) {
                        favourite_icon_imageview.setImageResource(R.drawable.ic_fav_red);
                    } else {
                        favourite_icon_imageview.setImageResource(R.drawable.ic_fav_grey);
                    }

                    favorite_count_textview.setText(tweet.getFavoriteCount());
                }
            }
        };
    }

    private View.OnClickListener getTweetTapOnClickListener (final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mTweetViewHolderEventListener != null) {
                    mTweetViewHolderEventListener.didSelectTweet(tweet);
                }
            }
        };
    }
}
