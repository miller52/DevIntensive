package com.softdesign.devintensive.utils;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;

import java.util.regex.Pattern;

/**
 * Класс реализует интерфейс {@link TextWatcher}
 * Используется для сверки введенных в поля информации о пользователе данных с RegExp паттерном
 */
public class ProfileDataTextWatcher implements TextWatcher {

    final private Pattern mPattern;
    final private TextInputLayout mTil;

    public ProfileDataTextWatcher(Pattern p, TextInputLayout textInputLayout) {
        mPattern = p;
        mTil = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().trim().isEmpty()) {
            mTil.setError(DataManager.getInstance().getContext().getString(R.string.error_no_data));
        } else if (!mPattern.matcher(charSequence.toString().trim()).matches()) {
            mTil.setError(DataManager.getInstance().getContext().getString(R.string.error_uncorrect_data));
        } else {
            mTil.setError(null);
            mTil.setErrorEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
