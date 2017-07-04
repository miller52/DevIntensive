package com.softdesign.devintensive.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.data.network.res.UploadProfilePhotoRes;
import com.softdesign.devintensive.utils.AppUtils;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkHelper;
import com.softdesign.devintensive.utils.ProfileDataTextWatcher;
import com.softdesign.devintensive.utils.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    public static final String TAG = ConstantManager.TAG_PREFIX + MainActivity.class.getSimpleName();
    private boolean mCurrentEditMode = false;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placrholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppbarLayout;
    @BindView(R.id.user_photo)
    ImageView mProfileImage;

    @BindView(R.id.til_phone)
    TextInputLayout mTilPhone;
    @BindView(R.id.til_email)
    TextInputLayout mTilEmail;
    @BindView(R.id.til_vk)
    TextInputLayout mTilVk;
    @BindView(R.id.til_github)
    TextInputLayout mTilGitHub;

    @BindViews({R.id.et_phone, R.id.et_email, R.id.et_vk, R.id.et_github, R.id.et_about})
    List<EditText> mUserInfoList;

    @BindViews({R.id.main_tv_raiting, R.id.main_tv_code_lines, R.id.main_tv_projects})
    List<TextView> mUserValuesViews;

    private final int PROFILE_ET_PHONE_POSITION = 0;
    private final int PROFILE_ET_EMAIL_POSITION = 1;
    private final int PROFILE_ET_VK_POSITION = 2;
    private final int PROFILE_ET_GITHUB_POSITION = 3;
    private boolean mPhotoIsChanged = false;

    /**
     * Метод вызывается при старте активити
     * В данном методе инициализируются/производятся:
     * UI пользовательский интерфейс;
     * инициализация статических даных;
     * связь данных со списками (инициализация адаптеров).
     * <p/>
     * Не запускать длительные операции по работе с данными.
     *
     * @param savedInstanceState объект со значениями, сохраненными в {@link Bundle} - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

//Реализованный синглтон созданный исспользуя Picasso Builder хранящийся в  Datamanager  надо было исспользовать тут тоже чтобы 
//получить Instance Picasso c уже готовыми настройками кеширования и контекста
        Picasso.with(this)
                .load(DataManager.getInstance().getPreferenceManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg)
                .resize(width, getResources().getDimensionPixelOffset(R.dimen.size_profile_image))
                .centerCrop()
                .into(mProfileImage);

        setValidators();

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_stat_panel);
        if (savedInstanceState == null) {
            mCurrentEditMode = false;

        } else {
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
            setEditMode(mCurrentEditMode);
            int llPadding = savedInstanceState.getInt(ConstantManager.STAT_PANEL_PADDING_KEY, getResources().getDimensionPixelSize(R.dimen.padding_large_24));
            if (ll != null) {
                ll.setPadding(0, llPadding, 0, llPadding);
            }
        }
        mPhotoIsChanged = false;
    }

    /**
     * Метод сохранения пользовательских данных при пересоздании Активити
     *
     * @param outState обьект типа {@link Bundle}, в который сохраняются пользовательские данные
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_stat_panel);
        outState.putInt(ConstantManager.STAT_PANEL_PADDING_KEY, ll.getPaddingTop());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод вызывается при старте активити перед тем, как UI станет доступен пользователю
     * как правило в этом методе происходит регистрация подписки на события, овтановка которых
     * была произведена в onStop()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Метод вызывается когда активити становится доступной пользователю ля взаимодействия
     * в данном методе происходит запуск анимаций/аудио/видео BrodcastReceiver необходимых
     * для реализации UI-логики/запуск выполнения потоков и т.д.
     * Метод должен быть максимально легковесным для максимальной отзывчивости UI
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Метод вызывается, когда текщая активити теряет фокус, но остается видимойс (всплытие диалогового
     * окна/частичное перекрытие другой активити и т.п.)
     * <p/>
     * В данном методе реализуют сохранение легковесных UI-данных, остановку анимаций/аудио/видео.
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveUserInfoValues();
    }

    /**
     * Метод вызывается, когда активити становится невидимой для пользователя.
     * В данно методе необходимо отписываться от событий, останавливать сложные анимации, выполнять сложные операции по
     * сохраненю данных/прерывание запущенных потоков и т.д.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Метод вызывается при окончании работы активити (когда это происходит системно или при вызове finish())
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Метод вызывается при рестарте активити/возобновлении работы после выхова метода onStop()
     * В данном методе реализуется специфическая бизнес-логика, которая должна быть реализована именно при
     * рестарте активити - например запрос к серверу, которые необходимо выхзывать при выходе из другой активити
     * (обновление данных, подписка на определнное событие проинициализированное на другом экране/
     * специфичная бизнес-логика, завязанная именно на перезапуск активити)
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Метод обрабоки запрашиваемых данных от других {@link android.app.Activity}
     *
     * @param requestCode код запроса, на основании которого понимаем от какой активности пришел ответ
     * @param resultCode  результат запроса
     * @param data        данные, упакованые в {@link Intent}, которые возвращены запрашиваемой активностью
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_CAMERA_PHOTO:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                    mPhotoFile = new File(AppUtils.getPathByUri(mSelectedImage));
                }
                break;
            case ConstantManager.REQUEST_PERMISSION_CODE:
                showImageProviderChooseDialog();
                break;
            default:
                break;
        }
    }

    private void insertProfileImage(Uri selectedImage) {
        PreferencesManager preferencesManager = DataManager.getInstance().getPreferenceManager();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Picasso.with(this)
                .load(selectedImage)
                .placeholder(R.drawable.collapsing_photo)
                .resize(width, getResources().getDimensionPixelOffset(R.dimen.size_profile_image))
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mProfileImage);

        preferencesManager.saveUserPhoto(selectedImage);
        mPhotoIsChanged = true;
    }

    /**
     * Метод отображает сообщение в {@link Snackbar}
     *
     * @param message сообщение
     */
    public void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Метод настраивает туллбар при запуске прложения
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = ((AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams());
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(DataManager.getInstance().getPreferenceManager().getUserName());
        }
    }

    /**
     * Метод настраивает {@link NavigationView} при запуске приложения
     */
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
        String e_mail = preferencesManager.loadUserProfileData().get(PROFILE_ET_EMAIL_POSITION);
        email.setText(e_mail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user_profile_menu:
                        Intent intentProfile = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intentProfile);
                        ActivityCompat.finishAfterTransition(MainActivity.this);
                        break;
                    case R.id.team_menu:
                        Intent intentTeam = new Intent(MainActivity.this, UserListActivity.class);
                        startActivity(intentTeam);
                        ActivityCompat.finishAfterTransition(MainActivity.this);
                        break;
                }
                item.setChecked(true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Метод пеерключает режим редактирования информации о пользователе
     *
     * @param mode если true, то переключает в режим редактирования, если false, в режим просмотра
     */
    private void setEditMode(boolean mode) {
        for (EditText userValue : mUserInfoList) {
            userValue.setEnabled(mode);
            userValue.setFocusable(mode);
            userValue.setFocusableInTouchMode(mode);
        }
        int fabIcon;
        if (mode) {
            fabIcon = R.drawable.ic_done_black_24dp;
            showProfilePlaceholder(true);
            lockToolbar(true);
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        } else {
            fabIcon = R.drawable.ic_create_black_24dp;
            showProfilePlaceholder(false);
            lockToolbar(false);
            saveUserInfoValues();
            mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        }
        mFab.setImageResource(fabIcon);
    }

    /**
     * Метод чтения информации о пользователе из {@link android.content.SharedPreferences}
     */
    private void initUserInfoValue() {
        List<String> userData = DataManager.getInstance().getPreferenceManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoList.get(i).setText(userData.get(i));
        }
    }

    /**
     * Метод записи информации о пользователе в {@link android.content.SharedPreferences}
     */
    private void saveUserInfoValues() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoList) {
            userData.add(userFieldView.getText().toString());
        }
        DataManager.getInstance().getPreferenceManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            takeGalleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_gallery_picker)), ConstantManager.REQUEST_GALLERY_PICTURE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            }, ConstantManager.GALLERY_REQUEST_PERMISSION_CODE);
            Snackbar.make(mCoordinatorLayout, R.string.string_need_permissions, Snackbar.LENGTH_LONG)
                    .setAction(R.string.string_allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(mCoordinatorLayout, R.string.error_create_tempfile, Snackbar.LENGTH_LONG).show();
            }
            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PHOTO);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            Snackbar.make(mCoordinatorLayout, R.string.string_need_permissions, Snackbar.LENGTH_LONG)
                    .setAction(R.string.string_allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    private void initUserFields() {
        List<String> userData = DataManager.getInstance().getPreferenceManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValuesViews.get(i).setText(userData.get(i));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadPhotoFromCamera();
            }
        } else if (requestCode == ConstantManager.GALLERY_REQUEST_PERMISSION_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPhotoFromGallery();
            }
        }
    }

    /**
     * Метод, показывающий/скрывающий лэйаут для смены изображения при редактировании параметров пользователя
     *
     * @param show true: показать лэйаут смены при редактировании параметров пользователя; false: скрыть
     */
    private void showProfilePlaceholder(boolean show) {
        if (show) {
            mProfilePlaceholder.setVisibility(View.VISIBLE);
        } else {
            mProfilePlaceholder.setVisibility(View.GONE);
        }
    }

    /**
     * Метод блокировки/разблокировки {@link AppBarLayout}
     *
     * @param lock true: заблокировать {@link AppBarLayout} при редактировании параметров пользователя; false: разблокировать
     */
    private void lockToolbar(boolean lock) {
        if (lock) {
            mAppBarParams.setScrollFlags(0);
            mAppbarLayout.setExpanded(true, true);
        } else {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        }
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    /**
     * Формирование диалога выбора провайдера изображения (камера или галерея)
     */
    private void showImageProviderChooseDialog() {
        String[] selectItems = {getString(R.string.user_profile_gallery_dialog), getString(R.string.user_profile_camera_dialog), getString(R.string.user_profile_cancel_dialog)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_photo_placeholder));
        builder.setItems(selectItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int choiseItem) {
                switch (choiseItem) {
                    case 0:
                        loadPhotoFromGallery();
                        break;
                    case 1:
                        loadPhotoFromCamera();
                        break;
                    case 2:
                        dialogInterface.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.REQUEST_PERMISSION_CODE);
    }

    private void setOnFocusChangeListener() {
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(R.dimen.size_bigger_80);
                    view.setLayoutParams(lp);
                    Log.d("TAG", "222");
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        };
        mUserInfoList.get(PROFILE_ET_PHONE_POSITION).setOnFocusChangeListener(focusListener);
        //((TextInputLayout) findViewById(R.id.til_email)).setOnFocusChangeListener(focusListener);
    }

    private void setValidators () {
        final String PHONE_REGEXP = "^[\\d\\(\\)\\-+ ]{11,20}$";
        final String EMAIL_REGEXP = "^[A-Za-z0-9+_.-]{3,}+@([A-Za-z0-9+_.-]{2,})+\\.+[a-zA-Z]{2,}$";
        final String VK_REGEXP = "^vk\\.com\\/[\\w]{3,}+$";
        final String GITHUB_REGEXP = "^github\\.com\\/[\\w]{3,}+$";

        mUserInfoList.get(PROFILE_ET_PHONE_POSITION).addTextChangedListener(new ProfileDataTextWatcher(Pattern.compile(PHONE_REGEXP),mTilPhone));
        mUserInfoList.get(PROFILE_ET_EMAIL_POSITION).addTextChangedListener(new ProfileDataTextWatcher(Pattern.compile(EMAIL_REGEXP),mTilEmail));
        mUserInfoList.get(PROFILE_ET_VK_POSITION).addTextChangedListener(new ProfileDataTextWatcher(Pattern.compile(VK_REGEXP),mTilVk));
        mUserInfoList.get(PROFILE_ET_GITHUB_POSITION).addTextChangedListener(new ProfileDataTextWatcher(Pattern.compile(GITHUB_REGEXP),mTilGitHub));
    }

    /**
     * Метод обработки клика FloatingActionButton
     */
    @OnClick(R.id.fab)
    public void onFabClick() {
        mCurrentEditMode = !mCurrentEditMode;
        setEditMode(mCurrentEditMode);
        if (mCurrentEditMode) {
            mUserInfoList.get(PROFILE_ET_PHONE_POSITION).requestFocus();
            mUserInfoList.get(PROFILE_ET_PHONE_POSITION).setSelection(mUserInfoList.get(PROFILE_ET_PHONE_POSITION).getText().length());
        } else if (mPhotoIsChanged){
            mPhotoIsChanged = false;
            if (NetworkHelper.isNetworkAvailable(this)) {
                Call<UploadProfilePhotoRes> call = DataManager.getInstance().uploadPhoto(DataManager.getInstance().getPreferenceManager().getUserId(), mPhotoFile);
                call.enqueue(new Callback<UploadProfilePhotoRes>() {
                    @Override
                    public void onResponse(Call<UploadProfilePhotoRes> call, Response<UploadProfilePhotoRes> response) {
                        if (response.code() == 200) {
                            showSnackBar(getString(R.string.main_photo_uploaded));
                        } else if (response.code() == 401) {
                            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                            intent.putExtra(ConstantManager.USER_AUTORIZATION_FAILED, true);
                            startActivity(intent);
                            ActivityCompat.finishAfterTransition(MainActivity.this);
                        } else {
                            showSnackBar(getString(R.string.main_something_wrong));
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadProfilePhotoRes> call, Throwable t) {
                        showSnackBar(getString(R.string.main_upload_error));
                    }
                });
            } else {
                showSnackBar(getString(R.string.main_network_is_nor_available));
            }
        }
    }

    @OnClick(R.id.profile_placrholder)
    public void onPhotoChangeClick() {
        showImageProviderChooseDialog();
    }

    @OnClick(R.id.profile_call)
    public void onCallClick() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + mUserInfoList.get(PROFILE_ET_PHONE_POSITION).getText().toString()));
        if (deviceHaveIntentActivity(callIntent)) {
            startActivity(callIntent);
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.string_warning_does_not_match_activity, Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.profile_send_email)
    public void onSendEmailClick() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mUserInfoList.get(PROFILE_ET_EMAIL_POSITION).getText().toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello world");
        emailIntent.setType("application/octet-stream");
        if (deviceHaveIntentActivity(emailIntent)) {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.string_warning_does_not_match_activity, Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.profile_open_vk)
    public void onVkOpen() {
        final String VK_APP_PACKAGE_ID = "com.vkontakte.android";
        final String url = "https://" + mUserInfoList.get(PROFILE_ET_VK_POSITION).getText().toString();
        Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(vkIntent, 0);

        if (resInfo.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, R.string.string_warning_does_not_match_activity, Snackbar.LENGTH_LONG).show();
        } else {
            for (ResolveInfo info : resInfo) {
                if (VK_APP_PACKAGE_ID.equals(info.activityInfo.packageName)) {
                    vkIntent.setPackage(info.activityInfo.packageName);
                    break;
                }
            }
            startActivity(vkIntent);
        }
    }

    @OnClick(R.id.profile_open_github)
    public void onGitHubOpen() {
        final String url = "https://" + mUserInfoList.get(PROFILE_ET_GITHUB_POSITION).getText().toString();
        Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (deviceHaveIntentActivity(githubIntent)) {
            startActivity(githubIntent);
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.string_warning_does_not_match_activity, Snackbar.LENGTH_LONG).show();
        }
    }
}
