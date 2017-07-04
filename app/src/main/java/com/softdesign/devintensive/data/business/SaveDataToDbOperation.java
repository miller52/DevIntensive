package com.softdesign.devintensive.data.business;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.model.Repository;
import com.softdesign.devintensive.data.storage.model.RepositoryDao;
import com.softdesign.devintensive.data.storage.model.User;
import com.softdesign.devintensive.data.storage.model.UserDao;

import java.util.List;

/**
 * Created by miller52 on 20.07.2016.
 */
public class SaveDataToDbOperation extends ChronosOperation<String> {

    private List<Repository> mRepositories;
    private List<User> mUsers;

    public SaveDataToDbOperation(List<Repository> repositories, List<User> users) {
        mRepositories = repositories;
        mUsers = users;
    }

    @Nullable
    @Override
    public String run() {
        UserDao mUserDao = DataManager.getInstance().getDaoSession().getUserDao();
        RepositoryDao mRepositoryDao = DataManager.getInstance().getDaoSession().getRepositoryDao();
        mRepositoryDao.insertOrReplaceInTx(mRepositories);
        mUserDao.insertOrReplaceInTx(mUsers);
        //throw new RuntimeException();
        return null;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<String> {

    }
}
