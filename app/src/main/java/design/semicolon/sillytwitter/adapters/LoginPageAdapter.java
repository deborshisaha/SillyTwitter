package design.semicolon.sillytwitter.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import design.semicolon.sillytwitter.R;

/**
 * Created by dsaha on 2/28/16.
 */
public class LoginPageAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ImageView mImageView;
        private boolean editingMode;

        int[] mResources = {
                R.drawable.first,
                R.drawable.second
        };

        public LoginPageAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mImageView.setImageResource(mResources[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        public int sharpenBackgroundImage (int position) {
            if (position < mResources.length) {
                return mResources[position];
            }
            return -1;
        }

        public void setEditingMode(boolean editingMode) {
            this.editingMode = editingMode;
        }
}

