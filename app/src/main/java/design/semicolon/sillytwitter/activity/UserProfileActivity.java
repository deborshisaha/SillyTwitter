package design.semicolon.sillytwitter.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.adapters.UserProfilePagerAdapter;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;

public class UserProfileActivity extends AppCompatActivity {

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.user_profile_banner_image_view)
    ImageView mUserProfileBannerImageView;

    @Bind(R.id.user_profile_picture_imageview)
    RoundedImageView mUserProfilePictureImageView;

    @Bind(R.id.user_screen_name_textview)
    TextView mUserScreenNameTextView;

    @Bind(R.id.user_full_name_textview)
    TextView mUserFullnameTextView;

    @Bind(R.id.user_about_textview)
    TextView mUserBioTextView;

    @Bind(R.id.user_location_name_textview)
    TextView mUserLocationTextView;

    @Bind(R.id.user_number_of_followers_textview)
    TextView mUserNumberOfFollowersTextView;

    @Bind(R.id.user_number_of_following_textview)
    TextView mUserNumberOfFollowingTextView;

    @Bind(R.id.user_location_icon_imageview)
    ImageView mUserLocationImageView;

    @Bind(R.id.user_profile_nested_scroll_view)
    NestedScrollView mUserProfileNestedScrollView;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsibleLayout;

    private User profileForUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        // Setting up action bar
        setSupportActionBar(mToolbar);

        if (profileForUser == null) {
            profileForUser = getUserFromBundle();
        }

        populatePageWithUserInfo(profileForUser);

        mUserProfileNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("USER_PROFILE", " scrollX: " + scrollX + " scrollY: " + scrollY + " oldScrollX: " + oldScrollX + " oldScrollY: " + oldScrollY);
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.user_generated_content_tabs);
        viewPager.setAdapter(new UserProfilePagerAdapter(getSupportFragmentManager(), new TweetViewHolderEventListener() {

            @Override
            public Tweet didPressReplyOnTweetWithId(Tweet tweet) {
                return null;
            }

            @Override
            public Tweet didPressLikeOnTweetWithId(Tweet tweet) {
                return null;
            }

            @Override
            public Tweet didPressRetweetOnTweetWithId(Tweet tweet) {
                return null;
            }

            @Override
            public void didPressProfilePicture(Tweet tweet) {

            }

            @Override
            public void didSelectTweet(Tweet tweet) {

            }
        }));

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.user_generated_content_tab_title);
        tabsStrip.setViewPager(viewPager);

    }

    private void populatePageWithUserInfo(User profileForUser) {

        if ( profileForUser != null && profileForUser.getProfileBannerImageURL()!=null) {
            Log.d("DEBUG", "getProfileBannerImageURL:"+profileForUser.getProfileBannerImageURL());
            Glide.with(this).load(profileForUser.getProfileBannerImageURL()).into(mUserProfileBannerImageView);
        }

        if ( profileForUser != null && profileForUser.getUserProfilePictureURLString()!=null) {
            Log.d("DEBUG", "getUserProfilePictureURLString:"+profileForUser.getUserProfilePictureURLString());
            Glide.with(this).load(profileForUser.getUserProfilePictureURLString()).into(mUserProfilePictureImageView);
        }

        mUserFullnameTextView.setText(profileForUser.getFullName());
        mUserScreenNameTextView.setText(profileForUser.getUserName());

        mUserBioTextView.setText(profileForUser.getBio());

        if (profileForUser.getLocation() == null || profileForUser.getLocation().length() == 0) {
            mUserLocationImageView.setVisibility(View.GONE);
            mUserLocationTextView.setVisibility(View.GONE);
        } else {
            mUserLocationImageView.setVisibility(View.VISIBLE);
            mUserLocationTextView.setVisibility(View.VISIBLE);
            mUserLocationTextView.setText(profileForUser.getLocation());
        }

        mUserNumberOfFollowersTextView.setText(profileForUser.getFollowersCount());
        mUserNumberOfFollowingTextView.setText(profileForUser.getFriendsCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public User getUserFromBundle() {

        Bundle extras = getIntent().getExtras();

        User userFromBundle = null;

        if (extras != null) {
            userFromBundle = (User) getIntent().getSerializableExtra(User.USER);
        }

        return userFromBundle;
    }

//    @Override
//    public void collapseAppBar() {
//        //mAppBarLayout.setExpanded(false, true);
//    }
//
//    @Override
//    public void expandAppBar() {
//        //mAppBarLayout.setExpanded(false, true);
//    }
//
//    @Override
//    public int getVisibleHeightForRecyclerViewInPx() {
//
//        int windowHeight, appBarHeight, headerViewHeight;
//        windowHeight = getWindow().getDecorView().getHeight();
//        appBarHeight = mAppBarLayout.getHeight();
//        headerViewHeight = 50;
//
//        return windowHeight - (appBarHeight + headerViewHeight);
//    }

}