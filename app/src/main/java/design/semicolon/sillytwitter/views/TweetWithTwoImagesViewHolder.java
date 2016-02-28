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

public class TweetWithTwoImagesViewHolder extends TweetViewHolder {

    @Bind(R.id.image_one_imageview)
    ImageView image_one_imageview;

    @Bind(R.id.image_two_imageview)
    ImageView image_two_imageview;

    public TweetWithTwoImagesViewHolder(View itemView, Context context, TweetViewHolderEventListener tweetViewHolderEventListener){
        super(itemView, context, tweetViewHolderEventListener);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void decorateViewWithTweet (Tweet tweet) {

        super.decorateViewWithTweet(tweet);

        String tweetMedia1stURLString = tweet.getFirstImageURL();
        String tweetMedia2ndURLString = tweet.getSecondImageURL();

        if (tweetMedia1stURLString != null){
            Glide.with(context).load(tweetMedia1stURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_one_imageview);
        }

        if (tweetMedia2ndURLString != null){
            Glide.with(context).load(tweetMedia2ndURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_two_imageview);
        }
    }
}
