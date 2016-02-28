package design.semicolon.sillytwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.dao.TweetDaoImpl;
import design.semicolon.sillytwitter.exceptions.NoNetworkConnectionException;
import design.semicolon.sillytwitter.fragments.ComposeTweetFragment;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

/**
 * Created by dsaha on 2/27/16.
 */
public class TweetDetailedActivity extends AppCompatActivity {

    @Bind(R.id.user_screen_name_textview)
    TextView user_screen_name_textview;

    @Bind(R.id.user_full_name_textview)
    TextView user_full_name_textview;

    @Bind(R.id.user_profile_picture_imageview)
    RoundedImageView user_profile_picture_imageview;

    @Bind(R.id.tweet_text_textview)
    TextView tweet_text_textview;

    @Bind(R.id.favorite_icon_imageview)
    ImageView favorite_icon_imageview;

    @Bind(R.id.reply_icon_imageview)
    ImageView reply_icon_imageview;

    @Bind(R.id.retweet_icon_imageview)
    ImageView retweet_icon_imageview;

    @Bind(R.id.tweet_media_imageview)
    RoundedImageView tweet_media_imageview;

    @Bind(R.id.number_of_retweets_textview)
    TextView number_of_retweets_textview;

    @Bind(R.id.number_of_likes_textview)
    TextView number_of_likes_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tweet_details);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        final Tweet tweet = getTweetFromBundle();

        if (tweet != null) {

            String profilePictureURLString = tweet.getUser().getUserProfilePictureURLString();

            if (profilePictureURLString != null){
                Glide.with(this).load(profilePictureURLString).into(user_profile_picture_imageview);
            }
            this.number_of_likes_textview.setText(tweet.getFavoriteCount());
            this.number_of_retweets_textview.setText(tweet.getRetweetCount());
            this.user_full_name_textview.setText(tweet.getUser().getFullName());
            this.user_screen_name_textview.setText(tweet.getUser().getUserName());
            this.tweet_text_textview.setText(tweet.getText());

            if (tweet.getFirstImageURL() != null && tweet.getFirstImageURL().length() != 0) {
                Glide.with(this).load(tweet.getFirstImageURL()).into(tweet_media_imageview);
                tweet_media_imageview.setVisibility(View.VISIBLE);
            } else {
                tweet_media_imageview.setVisibility(View.GONE);
            }

            View.OnClickListener replyOnClickListener = getReplyOnClickListener(tweet);
            View.OnClickListener profilePictureTapOnClickListener = getProfilePictureTapOnClickListener(tweet);

            this.retweet_icon_imageview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    if (tweet.isRetweetedByUser() == true) {
                        try {
                            (new TweetDaoImpl(TweetDetailedActivity.this)).postUndoRetweet(tweet);
                            tweet.setRetweetedByUser(!tweet.isRetweetedByUser());
                            retweet_icon_imageview.setImageResource(R.drawable.ic_retweet_grey);
                            number_of_retweets_textview.setText(tweet.getRetweetCount());
                        } catch (NoNetworkConnectionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            (new TweetDaoImpl(TweetDetailedActivity.this)).postRetweet(tweet);
                            tweet.setRetweetedByUser(!tweet.isRetweetedByUser());
                            retweet_icon_imageview.setImageResource(R.drawable.ic_retweet_green);
                            number_of_retweets_textview.setText(tweet.getRetweetCount());
                        } catch (NoNetworkConnectionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            this.reply_icon_imageview.setOnClickListener(replyOnClickListener);

            this.favorite_icon_imageview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (tweet.isLikedByUser()) {
                        try {
                            (new TweetDaoImpl(TweetDetailedActivity.this)).postTweetUnliked(tweet);
                            tweet.setLikedByUser(!tweet.isLikedByUser());
                            favorite_icon_imageview.setImageResource(R.drawable.ic_fav_grey);
                            number_of_likes_textview.setText(tweet.getFavoriteCount());
                        } catch (NoNetworkConnectionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            (new TweetDaoImpl(TweetDetailedActivity.this)).postTweetLike(tweet);
                            tweet.setLikedByUser(!tweet.isLikedByUser());
                            favorite_icon_imageview.setImageResource(R.drawable.ic_fav_red);
                            number_of_likes_textview.setText(tweet.getFavoriteCount());
                        } catch (NoNetworkConnectionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            this.user_profile_picture_imageview.setOnClickListener(profilePictureTapOnClickListener);
        }
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
                showComposeNewTweetDialog();
                break;
            }
            case R.id.menu_profile:{
                Log.d("DEBUG", "Do something");
                showUserProfile(User.currentUser(this));
            }
            case R.id.menu_message:{
                Log.d("DEBUG", "Write message to friend");
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Tweet getTweetFromBundle() {

        Bundle extras = getIntent().getExtras();

        Tweet tweetFromBundle = null;

        if (extras != null) {
            tweetFromBundle = (Tweet) getIntent().getSerializableExtra(Tweet.TWEET);
        }

        return tweetFromBundle;
    }

    private View.OnClickListener getProfilePictureTapOnClickListener(final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showUserProfile(tweet.getUser());
            }
        };
    }

    private View.OnClickListener getReplyOnClickListener (final Tweet tweet) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                replyToTweetDialog(tweet);
            }
        };
    }

    private void showUserProfile (User user) {
        Intent intent = new Intent(this, UserProfileActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(User.USER, user);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showComposeNewTweetDialog() {

    }

    private void replyToTweetDialog(Tweet tweet) {
        User user = User.currentUser(TweetDetailedActivity.this);
        ComposeTweetFragment frag = ComposeTweetFragment.newInstance(user, tweet, TweetDetailedActivity.this, new ComposeTweetFragment.OnTweetPostedHandler() {
            @Override
            public void onTweetPosted(Tweet newTweet) {
//                mTweetAdapter.addTweet(newTweet);
//                mTweetAdapter.notifyItemInserted(0);
//                mTimelineRecyclerView.smoothScrollToPosition(0);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "compose_tweet_fragment");
    }

}
