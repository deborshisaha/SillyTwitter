package design.semicolon.sillytwitter.views;

import android.content.Context;
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
import design.semicolon.sillytwitter.models.Tweet;

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

    Context context;

    public TweetViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TweetViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        if (this.getClass() == TweetViewHolder.class){
            ButterKnife.bind(this, itemView);
        }
    }

    public void decorateViewWithTweet (Tweet tweet) {
        this.user_full_name_textview.setText(tweet.getUser().getFullName());
        this.username_textview.setText("@"+tweet.getUser().getUserName());
        this.relative_time_textview.setText(tweet.getCreatedAt(true));
        this.tweet_text_textview.setText(tweet.getText());
        this.retweet_count_textview.setText(tweet.getRetweetCount());
        this.favorite_count_textview.setText(tweet.getFavoriteCount());

        Log.d("DEBUG", "Tweet text "+tweet.getText());
        if (tweet.getUser().getUserProfilePictureURLString() != null) {
            Glide.with(context).load(tweet.getUser().getUserProfilePictureURLString()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder).into(user_profile_picture_imageview);
        }
    }
}
