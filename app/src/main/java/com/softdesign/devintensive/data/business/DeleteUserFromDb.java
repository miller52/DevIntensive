package com.softdesign.devintensive.data.business;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.model.User;

/**
 * Created by miller52 on 21.07.2016.
 */
public class DeleteUserFromDb extends ChronosOperation<String> {

    User mUser;

    public DeleteUserFromDb(User user) {
        mUser = user;
    }

    @Nullable
    @Override
    public String run() {
        String name = mUser.getFullName();
        DataManager.getInstance().deleteUser(mUser);
        return name;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return Result.class;
    }

    public static final class Result extends ChronosOperationResult<String> {

    }
}
