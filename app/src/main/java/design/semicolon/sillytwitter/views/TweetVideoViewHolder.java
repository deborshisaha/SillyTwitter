package design.semicolon.sillytwitter.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.models.Tweet;

/**
 * Created by dsaha on 2/25/16.
 */
public class TweetVideoViewHolder extends TweetViewHolder {

    @Bind(R.id.image_one_imageview)
    ImageView image_one_imageview;

    @Bind(R.id.videoplayer_view)
    VideoView videoplayerView;

    public TweetVideoViewHolder(View itemView, Context context){
        super(itemView, context);

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
