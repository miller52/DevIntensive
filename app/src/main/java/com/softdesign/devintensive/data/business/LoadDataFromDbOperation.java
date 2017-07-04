package com.softdesign.devintensive.data.business;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.model.User;

import java.util.List;

/**
 * Created by miller52 on 20.07.2016.
 */
public class LoadDataFromDbOperation extends ChronosOperation<List<User>> {

    String subString;

    public LoadDataFromDbOperation() {
        this("");
    }

    public LoadDataFromDbOperation(String matchSubstring) {
        subString = matchSubstring;
    }

    @Nullable
    @Override
    public List<User> run() {
        //throw new RuntimeException();
        return DataManager.getInstance().getUserListByName(subString);
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<List<User>>> getResultClass() {
        return Result.class;
    }

    public static final class Result extends ChronosOperationResult<List<User>> {

    }
}
