package design.semicolon.sillytwitter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import design.semicolon.sillytwitter.R;
import design.semicolon.sillytwitter.listerners.TweetViewHolderEventListener;
import design.semicolon.sillytwitter.models.TwitterMedia;
import design.semicolon.sillytwitter.views.MediaImageViewHolder;

/**
 * Created by dsaha on 2/28/16.
 */
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<TwitterMedia> mMedia;
    private TweetViewHolderEventListener mTweetViewHolderEventListener;

    public MediaAdapter(Context context, TweetViewHolderEventListener tweetViewHolderEventListener) {
        this.mContext = context;
        this.mTweetViewHolderEventListener = tweetViewHolderEventListener;
        this.mMedia = new ArrayList<TwitterMedia>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        viewHolder = new MediaImageViewHolder(this.mContext, inflater.inflate(R.layout.media_with_image, viewGroup, false), this.mTweetViewHolderEventListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TwitterMedia media = mMedia.get(position);

        if (holder instanceof MediaImageViewHolder){
            MediaImageViewHolder mediaImageViewHolder = (MediaImageViewHolder) holder;
            mediaImageViewHolder.decorateView(media);
        }
    }

    @Override
    public int getItemCount() {
        return mMedia.size();
    }

    public void appendMedia(List<TwitterMedia> media) {

        if (mMedia == null) {
            mMedia = new ArrayList<TwitterMedia>();
        }
        mMedia.addAll(media);
        notifyDataSetChanged();
    }
}
