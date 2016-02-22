package design.semicolon.sillytwitter.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.models.Tweet;

public class TweetWithTwoImagesViewHolder extends TweetViewHolder {

    @Bind(R.id.image_one_imageview)
    ImageView image_one_imageview;

    @Bind(R.id.image_two_imageview)
    ImageView image_two_imageview;

    Context context;

    public TweetWithTwoImagesViewHolder(View itemView, Context context){
        super(itemView, context);
        this.context = context;

        ButterKnife.bind(this, itemView);
    }

    @Override
    public void decorateViewWithTweet (Tweet tweet) {

        super.decorateViewWithTweet(tweet);

//        Glide.with(context).load(tweet.getFirstImageURL()).placeholder(R.drawable.placeholder).into(image_one_imageview);
//        Glide.with(context).load(tweet.getSecondImageURL()).placeholder(R.drawable.placeholder).into(image_two_imageview);
    }
}
