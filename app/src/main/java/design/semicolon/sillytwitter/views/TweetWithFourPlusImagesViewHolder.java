package design.semicolon.sillytwitter.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.models.Tweet;

public class TweetWithFourPlusImagesViewHolder extends TweetViewHolder {

    @Bind(R.id.image_one_imageview)
    ImageView image_one_imageview;

    @Bind(R.id.image_two_imageview)
    ImageView image_two_imageview;

    @Bind(R.id.image_three_imageview)
    ImageView image_three_imageview;

    @Bind(R.id.image_four_imageview)
    ImageView image_four_imageview;

    public TweetWithFourPlusImagesViewHolder(View itemView, Context context){
        super(itemView, context);

        ButterKnife.bind(this, itemView);
    }

    public void decorateViewWithTweet (Tweet tweet) {
        super.decorateViewWithTweet(tweet);
        String tweetMedia1stURLString = tweet.getFirstImageURL();
        String tweetMedia2ndURLString = tweet.getSecondImageURL();
        String tweetMedia3rdURLString = tweet.getThirdImageURL();
        String tweetMedia4thURLString = tweet.getForthImageURL();

        if (tweetMedia1stURLString != null){
            Glide.with(context).load(tweetMedia1stURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_one_imageview);
        }

        if (tweetMedia2ndURLString != null){
            Glide.with(context).load(tweetMedia2ndURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_two_imageview);
        }

        if (tweetMedia3rdURLString != null){
            Glide.with(context).load(tweetMedia3rdURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_three_imageview);
        }

        if (tweetMedia4thURLString != null){
            Glide.with(context).load(tweetMedia4thURLString+":medium").placeholder(R.drawable.placeholder_medium).into(image_four_imageview);
        }
    }
}
