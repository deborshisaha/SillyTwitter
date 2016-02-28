package design.semicolon.sillytwitter.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;

public class TweetWithOneImageViewHolder extends TweetViewHolder {

    @Bind(R.id.image_one_imageview)
    ImageView image_one_imageview;

    public TweetWithOneImageViewHolder(View itemView, Context context, TweetViewHolderEventListener tweetViewHolderEventListener){
        super(itemView, context, tweetViewHolderEventListener);

        ButterKnife.bind(this, itemView);
    }

    public void decorateViewWithTweet (Tweet tweet) {
        super.decorateViewWithTweet(tweet);
        String tweetMediaURLString = tweet.getFirstImageURL();

        if (tweetMediaURLString != null){
            Glide.with(context).load(tweetMediaURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_one_imageview);
        }
    }
}
