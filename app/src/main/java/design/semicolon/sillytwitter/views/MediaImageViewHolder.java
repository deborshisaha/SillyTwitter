package design.semicolon.sillytwitter.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.TwitterMedia;

/**
 * Created by dsaha on 2/28/16.
 */
public class MediaImageViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.media_imageview)
    RoundedImageView media_imageview;

    private Context mContext;

    private TweetViewHolderEventListener mtweetViewHolderEventListener;

    public MediaImageViewHolder(View itemView) {
        super(itemView);
    }

    public MediaImageViewHolder(Context context, View itemView, TweetViewHolderEventListener tweetViewHolderEventListener) {
        super(itemView);
        this.mContext = context;
        this.mtweetViewHolderEventListener = tweetViewHolderEventListener;

        ButterKnife.bind(this, itemView);
    }

    public void decorateView (TwitterMedia media) {

        this.media_imageview.setImageDrawable(null);

        if (media.getMediaUrl() != null){
            Glide.with(this.mContext).load(media.getMediaUrl()).placeholder(R.drawable.placeholder_medium).into(media_imageview);
        }

        media_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtweetViewHolderEventListener.didPressMediaAtIndex(0);
            }
        });
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
