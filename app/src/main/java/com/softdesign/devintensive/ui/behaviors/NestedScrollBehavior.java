package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.UiHelper;

/**
 * Created by roman on 02.07.16.
 */
public class NestedScrollBehavior extends AppBarLayout.ScrollingViewBehavior {

    // Минимальная высота AppBar
    private final int mMinAppBarHeight;
    // Максимальная высота AppBar
    private final int mMaxAppBarHeight;
    // Минимальная высота плашки
    private final int mMinStatPanelHeight;
    // Максиммальный паддинг плашки
    private final int mMaxStatPanelPadding;

    /**
     * Конструктор бихейвера, чтобы можно было его использовать из xml разметки
     *
     * @param context      контекст
     * @param attributeSet набор аттрибутов
     */
    public NestedScrollBehavior(Context context, AttributeSet attributeSet) {
        mMinAppBarHeight = UiHelper.getActionBarHeight() + UiHelper.getStatusBarHeight();
        mMaxAppBarHeight = context.getResources().getDimensionPixelSize(R.dimen.size_profile_image);
        mMinStatPanelHeight = context.getResources().getDimensionPixelSize(R.dimen.spacing_large_56);
        mMaxStatPanelPadding = context.getResources().getDimensionPixelSize(R.dimen.padding_large_24);
    }

    /**
     * Метод указывает зависимость вью от родительской
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    /**
     * Метод вызывается каждый раз, когда изменяется вью (dependency), к которой привязан контролируемый (child),
     * а так же при прокрутке и появлении/исчезновении элементов
     *
     * @param parent     родительский {@link CoordinatorLayout }
     * @param child      контролируемое {@link View}, в нашем случае {@link LinearLayout}
     * @param dependency {@link View} от которого зависит контролируемое {@link View}, в нашем случае {@link android.support.v7.app.ActionBar}
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float currentFriction = UiHelper.currentFriction(mMinAppBarHeight, mMaxAppBarHeight, dependency.getBottom());
        int currentPadding = UiHelper.lerp(0, mMaxStatPanelPadding, currentFriction);
        int transY = mMinStatPanelHeight + (currentPadding * 2);
        CoordinatorLayout.LayoutParams layoutParams = ((CoordinatorLayout.LayoutParams) child.getLayoutParams());
        layoutParams.topMargin = transY;
        child.setLayoutParams(layoutParams);
        return super.onDependentViewChanged(parent, child, dependency);

        /*
        Старый код ... работоспособность 88% :-)
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        LinearLayout linearLayout;
        if (dependency instanceof LinearLayout) {
            linearLayout = (LinearLayout) dependency;
            if (lp.getAnchorId() != -1 && lp.getAnchorId() != linearLayout.getId()) {
                // The anchor ID doesn't match the dependency
                return false;
            }
        } else {
            return false;
        }
        child.setY(dependency.getBottom());
        return super.onDependentViewChanged(parent, child, dependency);
        */
    }
}
