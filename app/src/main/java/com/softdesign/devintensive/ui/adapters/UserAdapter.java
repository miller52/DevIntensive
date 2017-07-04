package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.model.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

/**
 * Created by roman on 15.07.16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String TAG = ConstantManager.TAG_PREFIX + " UserAdapter";
    Context mContext;
    List<User> mUsers;
    UserViewHolder.CustomClickListener mCustomClickListener;

    public UserAdapter(Context mContext, List<User> mUsers, UserViewHolder.CustomClickListener customClickListener) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.mCustomClickListener = customClickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final String userPhoto;

        if (user.getPhoto().isEmpty()) {
            userPhoto = "null";
            Log.e(TAG, "onBindViewHolder: user with name " + user.getFullName() + " has empty name");
        } else {
            userPhoto = user.getPhoto();
        }

        try {
            DataManager.getInstance().getPicasso()
                    .load(userPhoto)
                    .error(holder.mDummy)
                    .placeholder(holder.mDummy)
                    .fit()
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.mUserPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "load from cache");
                        }

                        @Override
                        public void onError() {
                            DataManager.getInstance().getPicasso()
                                    .load(userPhoto)
                                    .error(holder.mDummy)
                                    .placeholder(holder.mDummy)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.mUserPhoto, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "load from cache");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.d(TAG, "Could not fetch image");
                                        }
                                    });
                        }
                    });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setText(user.getBio());
            holder.mBio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected AspectRatioImageView mUserPhoto;
        protected TextView mFullName;
        protected TextView mRating;
        protected TextView mCodeLines;
        protected TextView mProjects;
        protected TextView mBio;
        protected Drawable mDummy;

        protected Button mShowMore;
        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);

            mListener = customClickListener;
            mShowMore = ((Button) itemView.findViewById(R.id.item_user_info_btn));
            mUserPhoto = (AspectRatioImageView) itemView.findViewById(R.id.item_image);

            mFullName = (TextView) itemView.findViewById(R.id.item_full_name);
            mRating = (TextView) itemView.findViewById(R.id.item_user_rating);
            mCodeLines = (TextView) itemView.findViewById(R.id.item_user_code_lines);
            mProjects = (TextView) itemView.findViewById(R.id.item_user_projects);
            mBio = (TextView) itemView.findViewById(R.id.item_user_bio);
            mDummy = ContextCompat.getDrawable(mUserPhoto.getContext(), R.drawable.user_bg);

            mShowMore.setOnClickListener(this);
            mUserPhoto.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {
            void onUserItemClickListener(int position);
        }
    }
}
