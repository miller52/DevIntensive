package com.softdesign.devintensive.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.List;

/**
 * Created by roman on 27.06.16.
 */
public class BaseActivity extends AppCompatActivity {
    final String TAG = ConstantManager.TAG_PREFIX + BaseActivity.class.getSimpleName();
    protected ProgressDialog mProgressDialog;

    /**
     * Метод показывает ошибку в виде всплывающего сообщения {@link Toast}
     *
     * @param message передаваемое пользователю сообщение
     * @param error   ошибка
     */
    public void showError(String message, Exception error) {
        showToast(message);
        error.printStackTrace();
    }

    /**
     * Метод показывает сообщение пользователю в виде всплываюшео сообщения {@link Toast}
     *
     * @param message сообщение
     */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public boolean deviceHaveIntentActivity(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activitiesList = packageManager.queryIntentActivities(intent, 0);
        return activitiesList.size() > 0;
    }

    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.custom_dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        } else {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        }
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }
}
