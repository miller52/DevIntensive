package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с {@link SharedPreferences}
 */
public class PreferencesManager {

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_EMAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GITHUB_KEY,
            ConstantManager.USER_ABOUT_KEY};
    private static final String[] USER_VALUES = {
            ConstantManager.USER_RAITING_VALUE,
            ConstantManager.USER_CODE_LINES_COUNT,
            ConstantManager.USER_PROJECT_VALUES};
    private SharedPreferences mSharedPreferences;

    public PreferencesManager() {
        mSharedPreferences = DevIntensiveApplication.getsSharedPreferences();
    }

    /**
     * Метод сохраняет информацию о пользователе в {@link SharedPreferences}
     *
     * @param data коллекция {@link List}, хранящая пользовательские данные в следующем порядке: phone, e-mail, vk, github, about
     */
    public void saveUserProfileData(List<String> data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < USER_FIELDS.length; i++) {
            editor.putString(USER_FIELDS[i], data.get(i));
        }
        editor.apply();
    }

    /**
     * Метод возвращает информацию о пользователе из {@link SharedPreferences}
     * данные возвращаются в виде коллекции,
     * хранящей пользовательские данные в следующем порядке: phone, e-mail, vk, github, about
     *
     * @return коллекция {@link List}, хранящая значения типа {@link String}
     */
    public List<String> loadUserProfileData() {
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY, DataManager.getInstance().getContext().getString(R.string.default_phone)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_EMAIL_KEY, DataManager.getInstance().getContext().getString(R.string.default_email)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_KEY, DataManager.getInstance().getContext().getString(R.string.default_vk)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GITHUB_KEY, DataManager.getInstance().getContext().getString(R.string.default_github)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_ABOUT_KEY, DataManager.getInstance().getContext().getString(R.string.default_about)));
        return userFields;
    }

    /**
     * Сохранение в {@link SharedPreferences} полученного для работы в текущей сессии токена
     *
     * @param authToken токен аутентификации
     */
    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN, authToken);
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} полученный для работы в текущей сессии токен
     *
     * @return токен аутентификации
     */
    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN, "null");
    }

    /**
     * Сохранение в {@link SharedPreferences} полученного с REST API идентификатора пользователя
     *
     * @param userId идентификатор пользователя, типа {@link String}
     */
    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    /**
     * Метод возвращает сохраненный в {@link SharedPreferences} идентификатор пользователя
     *
     * @return идентификатор пользователя, типа {@link String}
     */
    public String getUserId() {
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "null");
    }

    /**
     * Метод сохраняет в {@link SharedPreferences} статистические данные о пользователе
     *
     * @param userValues массив {@link Integer},
     *                   со статистическими данными в следующем порядке: рейтинг, количество строк кода, количество проектов
     */
    public void saveUserProfileValues(int[] userValues) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < USER_VALUES.length; i++) {
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} статистические данные о пользователе
     *
     * @return коллекция {@link List} типа {@link String}, хранящая статистические данные
     * в следующем порядке: рейтинг, количество строк кода, количество проектов
     */
    public List<String> loadUserProfileValues() {
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_RAITING_VALUE, "0"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_CODE_LINES_COUNT, "0"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PROJECT_VALUES, "0"));
        return userFields;
    }

    /**
     * Метод созраняет имя пользователя в {@link SharedPreferences}
     *
     * @param name имя пользователя
     */
    public void saveUserName(String name) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_FULL_NAME, name);
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} сохраненное имя пользователя
     *
     * @return имя пользователя
     */
    public String getUserName() {
        return mSharedPreferences.getString(ConstantManager.USER_FULL_NAME, "null");
    }

    /**
     * Метод созраняет в {@link SharedPreferences} фотографию профиля
     *
     * @param uri значение типа {@link Uri} однозначно указывающее на текущее фото профиля
     */
    public void saveUserPhoto(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} ссылку на текущее фото профиля
     *
     * @return ссылка на фото профиля типа {@link Uri}
     */
    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY, "android.resource://com.softdesign.devintensive/drawable/user_bg"));
    }

    /**
     * Метод созраняет в {@link SharedPreferences} аватар профиля
     *
     * @param uri значение типа {@link Uri} однозначно указывающее на аватар профиля
     */
    public void saveUserAvatar(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} ссылку на аватар профиля
     *
     * @return ссылка на аватар профиля типа {@link Uri}
     */
    public Uri getUserAvatar() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY, "android.resource://com.softdesign.devintensive/drawable/photo"));
    }

    /**
     * Метод "затирает" сохраненный в {@link SharedPreferences} логин пользователя
     */
    public void saveLogin() {
        saveLogin("");
    }

    /**
     * Метод сохраняет в {@link SharedPreferences} логин аутотентификации, если
     * пользователь желает это сделать
     *
     * @param login значение типа {@link String} - логин пользователя
     */
    public void saveLogin(String login) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_CURRENT_LOGIN, login);
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} сохраненный логин пользователя
     *
     * @return логин пользователя
     */
    public String getLogin() {
        return mSharedPreferences.getString(ConstantManager.AUTH_CURRENT_LOGIN, "");
    }

    /**
     * Метод сохраняет в {@link SharedPreferences} значение флага сохранять/не сохранять введеный логин аутотентификации
     *
     * @param autoLoginFlag значение типа {@link Boolean} - сохранять или нет логин аутотентификации
     */
    public void setLoginSavedFlag(boolean autoLoginFlag) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ConstantManager.AUTH_AUTO_LOGIN_FLAG, autoLoginFlag);
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} флаг сохранять/не сохранять введеный логин аутотентификации
     *
     * @return значение типа {@link Boolean} - логин сохранен или нет
     */
    public boolean isLoginSaved() {
        return mSharedPreferences.getBoolean(ConstantManager.AUTH_AUTO_LOGIN_FLAG, false);
    }

    /**
     * Метод сохраняет в {@link SharedPreferences} последнее время обновления фотографии на сервере
     * На данный момент сервер возвращает статичные данные
     *
     * @param date дата изменения фотографии профиля на сервере
     */
    public void savePhotoLastChangedTime(String date) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_DOWNLOAD_DATE_KEY, date);
        editor.apply();
    }

    /**
     * Метод возвращает из {@link SharedPreferences} время обновления фотографии профиля на сервере
     * На данный момент не используется - сервер возвращает статичные данные
     *
     * @return значение типа {@link String} - время модификации фото на сервере
     */
    public String getPhotoLastChangedTime() {
        return mSharedPreferences.getString(ConstantManager.USER_PHOTO_DOWNLOAD_DATE_KEY, "null");
    }
}
