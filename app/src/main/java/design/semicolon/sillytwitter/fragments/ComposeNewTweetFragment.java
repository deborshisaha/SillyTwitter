package design.semicolon.sillytwitter.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.models.User;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

public class ComposeNewTweetFragment extends DialogFragment {

    @Bind(R.id.current_user_profile_picture_imageview)
    RoundedImageView current_user_profile_picture_imageview;

    @Bind(R.id.current_user_name_textview)
    TextView current_user_name_textview;

    @Bind(R.id.current_user_screenname_textview)
    TextView current_user_screenname_textview;

    @Bind(R.id.characters_left_textview)
    TextView characters_left_textview;

    @Bind(R.id.tweet_edittext)
    EditText tweet_edittext;

    @Bind(R.id.post_tweet_button)
    ImageButton post_tweet_button;

    private Context context;

    public static final String USER_KEY = "user";

    public ComposeNewTweetFragment() {}

    public static ComposeNewTweetFragment newInstance(User user, Context context) {
        ComposeNewTweetFragment frag = new ComposeNewTweetFragment();
        frag.context = context;
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
        current_user_screenname_textview.setText("@"+user.getUserName());
        characters_left_textview.setText("140");

        if (user.getUserProfilePictureURLString()!= null) {
            Glide.with(context).load(user.getUserProfilePictureURLString()).placeholder(R.drawable.placeholder).into(current_user_profile_picture_imageview);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
