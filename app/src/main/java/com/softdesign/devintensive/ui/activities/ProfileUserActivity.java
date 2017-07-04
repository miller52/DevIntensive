package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.model.UserDto;
import com.softdesign.devintensive.ui.adapters.RepositoryAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileUserActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;
    @BindView(R.id.et_about)
    EditText mUserBio;
    @BindView(R.id.main_tv_raiting)
    TextView mRaiting;
    @BindView(R.id.main_tv_code_lines)
    TextView mCodeLines;
    @BindView(R.id.main_tv_projects)
    TextView mProjects;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.repositories_list)
    ListView mRepoList;

    String mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        ButterKnife.bind(this);
        setupToolbar();
        initProfileData();
    }

    private void initProfileData() {
        UserDto userDto = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);
        final List<String> repo = userDto.getmRepositories();

        final RepositoryAdapter repositoryAdapter = new RepositoryAdapter(this, repo);
        mRepoList.setAdapter(repositoryAdapter);

        mRepoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String url = "https://" + repo.get(i);
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (deviceHaveIntentActivity(githubIntent)) {
                    startActivity(githubIntent);
                } else {
                    Snackbar.make(mCoordinatorLayout, R.string.string_warning_does_not_match_activity, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mUserBio.setText(userDto.getmBio());

        mRaiting.setText(userDto.getmRating());
        mCodeLines.setText(userDto.getmCodeLines());
        mProjects.setText(userDto.getmProjects());
        mCollapsingToolbar.setTitle(userDto.getmFullName());

        mPhoto = userDto.getmPhoto();

        try {
            DataManager.getInstance().getPicasso()
                    .load(mPhoto)
                    .error(R.drawable.user_bg)
                    .placeholder(R.drawable.user_bg)
                    .fit()
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "load from cache");
                        }

                        @Override
                        public void onError() {
                            DataManager.getInstance().getPicasso()
                                    .load(mPhoto)
                                    .error(R.drawable.user_bg)
                                    .placeholder(R.drawable.user_bg)
                                    .fit()
                                    .centerCrop()
                                    .into(mProfileImage, new Callback() {
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
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
