package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UploadProfilePhotoRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.model.DaoSession;
import com.softdesign.devintensive.data.storage.model.User;
import com.softdesign.devintensive.data.storage.model.UserDao;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Класс-синглтон для работы с менеджерами данных
 * Синглтон реализован через вложенный nested-класс чтобы можно было работать с синглтоном из
 * не UI-потоков и, как бонус, ленивая инициализация класса при первом обращении
 */
public class DataManager {

    private Context mContext;
    private PreferencesManager mPreferenceManager;
    private RestService mRestService;
    private Picasso mPicasso;

    private DaoSession mDaoSession;

    private DataManager() {
        mPreferenceManager = new PreferencesManager();
        mContext = DevIntensiveApplication.getAppContext();
        mRestService = ServiceGenerator.createService(RestService.class);
        mPicasso = new PicassoCache(mContext).getPicassoInstance();
        mDaoSession = DevIntensiveApplication.getDaoSession();
    }

    public static DataManager getInstance() {
        return DataManagerHolder.INSTANCE;
    }

    public PreferencesManager getPreferenceManager() {
        return mPreferenceManager;
    }

    public Context getContext() {
        return mContext;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    /**
     * Метод аутентификации
     *
     * @param userLoginReq POJO класс {@link UserLoginReq}, хранящий параметры для аутотентификации (логин и пароль)
     * @return модельный класс типа {@link UserModelRes} хранящий токен авторизации
     * и информацию о зарегистрированном пользователе
     */
    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    // region ============== NETWORK ==============

    /**
     * Метод загрузки фотографии профиля на сервер
     *
     * @param userId    идентификатор пользователя, типа {@link String}, для которого загружается фото профиля
     * @param photoFile параметр типа {@link File} - новая фотография пользователя
     * @return модельный класс типа {@link UploadProfilePhotoRes}
     */
    public Call<UploadProfilePhotoRes> uploadPhoto(String userId, File photoFile) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", photoFile.getName(), requestFile);
        return mRestService.uploadPhoto(userId, body);
    }

    /**
     * Метод получения списка пользователей с сервера
     *
     * @return модельный класс типа {@link UserListRes}, хранящий информацию о зарегистрированных пользователях
     */
    public Call<UserListRes> getUsersListFromNetwork() {
        return mRestService.getUserList();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    // endregion

    // region ============== DATABASE ==============

    public List<User> getUsersListFromDb() {
        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public List<User> getUserListByName(String query) {
        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0), UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.Rating)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public void deleteUser(User user) {
        mDaoSession.delete(user);
    }

    private static class DataManagerHolder {
        private final static DataManager INSTANCE = new DataManager();
    }

    // endregion
}
