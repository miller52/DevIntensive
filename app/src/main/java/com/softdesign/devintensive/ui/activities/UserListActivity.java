package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.business.DeleteUserFromDb;
import com.softdesign.devintensive.data.business.LoadDataFromDbOperation;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.data.storage.model.User;
import com.softdesign.devintensive.data.storage.model.UserDto;
import com.softdesign.devintensive.ui.adapters.UserAdapter;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends BaseActivity {

    private static String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.user_list_navigation_drawer)
    DrawerLayout mNavigationDrawer;

    @BindView(R.id.user_list_recycler_view)
    RecyclerView mRecyclerView;
    UserAdapter mUserAdapter;
    private DataManager mDataManager;
    private List<User> mUsers;
    private String mQuery;
    private Handler mHandler;
    private Runnable searchUsers;
    private ChronosConnector mChronosConnector = new ChronosConnector();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        mChronosConnector.onCreate(this, savedInstanceState);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mDataManager = DataManager.getInstance();
        setupToolbar();
        setupDrawer();
        mHandler = new Handler();
        searchUsers = new Runnable() {
            @Override
            public void run() {
                UserListActivity.this.showProgress();
                mChronosConnector.runOperation(new LoadDataFromDbOperation(mQuery), false);
            }
        };

        showProgress();
        mChronosConnector.runOperation(new LoadDataFromDbOperation(), false);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = mRecyclerView.getChildAdapterPosition(viewHolder.itemView);
                try {
                    mChronosConnector.runOperation(new DeleteUserFromDb(mUsers.get(position)), false);
                    mUsers.remove(position);
                    mUserAdapter.notifyItemRemoved(position);
                } catch (IndexOutOfBoundsException e) {

                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        /* // RetainedFragment

        FragmentManager fragmentManager = getFragmentManager();
        dataFragment = ((RetainedFragment) fragmentManager.findFragmentByTag("user_data"));
        if (dataFragment == null) {
            dataFragment = new RetainedFragment();
            fragmentManager.beginTransaction().add(dataFragment, "user_data").commit();
            showProgress();
            try {
                dataFragment.loadUsers(this);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            createAdapter(dataFragment.getData());
        }
*/
    }

    /*
    public void onDataReceived(int responseCode, List<UserListRes.UserData> data) {
        hideProgress();
        if (responseCode == 200) {
//            mUsers = data;
//            createAdapter ();
        } else if (responseCode == 401) {
            Intent intent = new Intent(UserListActivity.this, AuthActivity.class);
            intent.putExtra(ConstantManager.USER_AUTORIZATION_FAILED, true);
            startActivity(intent);
            ActivityCompat.finishAfterTransition(UserListActivity.this);
        } else if (responseCode == ConstantManager.REST_API_FAILURE_RESPONSE_CODE) {
            showSnackBar("Ошибка загрузки, попробуйте позже");
        } else {
            showSnackBar("Видимо что-то случилось");
        }
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        mChronosConnector.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mChronosConnector.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChronosConnector.onPause();
    }

    public void onOperationFinished(final LoadDataFromDbOperation.Result result) {
        hideProgress();
        if (result.isSuccessful()) {
            mUsers = result.getOutput();
            if (mUsers.size() > 0) {
                createAdapter(mUsers);
            } else {
                showSnackBar(getString(R.string.list_emty_list));
            }
        } else {
            showSnackBar(getString(R.string.list_error_download_users_list));
        }
    }

    public void onOperationFinished(final DeleteUserFromDb.Result result) {
        if (result.isSuccessful()) {
            showSnackBar("Пользователь \"" + result.getOutput() + "\" успешно удален.");
        } else {
            showSnackBar("Ошибка удаления пользователя");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.list_enter_users_name));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUserByQuery(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void showUserByQuery(String query) {
        mQuery = query;
        mHandler.removeCallbacks(searchUsers);
        if (query.isEmpty()) {
            showProgress();
            mChronosConnector.runOperation(new LoadDataFromDbOperation(), false);
        } else {
            mHandler.postDelayed(searchUsers, AppConfig.SEARCH_DELAY);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void createAdapter(final List<User> users) {
        mUsers = users;
        mUserAdapter = new UserAdapter(UserListActivity.this, mUsers, new UserAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDto user = new UserDto(mUsers.get(position));
                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, user);
                startActivity(profileIntent);
            }
        });
        mRecyclerView.swapAdapter(mUserAdapter, false);
    }

    private void setupDrawer() {
        PreferencesManager preferencesManager = DataManager.getInstance().getPreferenceManager();
        NavigationView navigationView = ((NavigationView) findViewById(R.id.navigation_view));
        ImageView iv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_avatar);
        Picasso.with(this)
                .load(preferencesManager.getUserAvatar())
                .transform(new RoundedTransformation())
                .into(iv);

        TextView name = ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name_txt));
        name.setText(preferencesManager.getUserName());
        TextView email = ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_e_mail_txt));
        String e_mail = preferencesManager.loadUserProfileData().get(1);
        email.setText(e_mail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user_profile_menu:
                        Intent intentProfile = new Intent(UserListActivity.this, MainActivity.class);
                        startActivity(intentProfile);
                        ActivityCompat.finishAfterTransition(UserListActivity.this);
                        break;
                    case R.id.team_menu:
                        Intent intentTeam = new Intent(UserListActivity.this, UserListActivity.class);
                        startActivity(intentTeam);
                        ActivityCompat.finishAfterTransition(UserListActivity.this);
                        break;
                }
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
