package design.semicolon.sillytwitter.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.SillyTwitterApplication;
import design.semicolon.sillytwitter.models.Tweet;
import design.semicolon.sillytwitter.models.User;
import design.semicolon.sillytwitter.restclient.SillyTwitterClient;

import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.http.Header;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ComposeTweetFragment extends DialogFragment {

    @Bind(R.id.current_user_profile_picture_imageview)
    RoundedImageView current_user_profile_picture_imageview;

    @Bind(R.id.current_user_name_textview)
    TextView current_user_name_textview;

    @Bind(R.id.current_user_screenname_textview)
    TextView current_user_screenname_textview;

    @Bind(R.id.characters_left_textview)
    TextView characters_left_textview;

    @Bind(R.id.post_tweet_button)
    ImageButton post_tweet_button;

    private Context context;

    private Tweet replyToTweet;

    public static final String USER_KEY = "user";

    private boolean goodToPost = false;

    private SillyTwitterClient client;

    private TextInputLayout composeTweetEditTextInputlayout ;

    public ComposeTweetFragment() {}

    public OnTweetPostedHandler mOnTweetPostedHandler;

    public static ComposeTweetFragment newInstance(User user, Tweet tweet, Context context, OnTweetPostedHandler handler) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        frag.context = context;
        frag.mOnTweetPostedHandler = handler;
        frag.replyToTweet = tweet;

        Bundle args = new Bundle();
        args.putSerializable(USER_KEY, user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compose_new_tweet_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User user = (User) getArguments().getSerializable(USER_KEY);

        current_user_name_textview.setText(user.getFullName());
        current_user_screenname_textview.setText(user.getUserName());

        post_tweet_button.setEnabled(false);
        post_tweet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (client == null) {
                    client = SillyTwitterApplication.getRestClient();
                }

                EditText editText = (EditText) composeTweetEditTextInputlayout.getEditText();
                client.postTweet(editText.getText().toString(), new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject tweetJSONObject) {
                        super.onSuccess(statusCode, headers, tweetJSONObject);

                        Tweet tweet = Tweet.fromJSON(tweetJSONObject);
                        tweet.save();
                        mOnTweetPostedHandler.onTweetPosted(tweet);

                        ComposeTweetFragment.this.dismiss();
                    }

                });
            }
        });

        if (user.getUserProfilePictureURLString()!= null) {
            Glide.with(context).load(user.getUserProfilePictureURLString()).placeholder(R.drawable.placeholder).into(current_user_profile_picture_imageview);
        }

        composeTweetEditTextInputlayout = (TextInputLayout) view.findViewById(R.id.username_text_input_layout);

        EditText editText = (EditText) composeTweetEditTextInputlayout.getEditText();

        if (this.replyToTweet != null) {
            editText.setText(this.replyToTweet.getUser().getUserName()+" ");
        }

        composeTweetEditTextInputlayout.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                int charactersLeft = 140 - text.length();
                characters_left_textview.setText(charactersLeft +" characters left");

                if (charactersLeft == 140 || charactersLeft < 0) {
                    post_tweet_button.setEnabled(false);
                } else {
                    post_tweet_button.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    public interface OnTweetPostedHandler {
        void onTweetPosted(Tweet newTweet);
    }
}
