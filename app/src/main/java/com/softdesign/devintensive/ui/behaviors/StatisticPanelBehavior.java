package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.content.res.TypedArray;
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
public class StatisticPanelBehavior<V extends LinearLayout> extends AppBarLayout.ScrollingViewBehavior {

    // Максимально возможный паддинг панели статистики (24dp) в px
    private final int mMaxPanelPadding;
    // Высота на которую может максимально подняться панель статистики (высота статус бара и экшн бара)
    private final int mMinAppBarHeight;
    // Высота на которую может максимально опуститься панель статистики (размер картинки)
    private final int mMaxAppBarHeight;

    /**
     * Конструктор бихейвера, чтобы можно было его использовать из xml разметки
     *
     * @param context контекст
     * @param attrs   набор аттрибутов
     */
    public StatisticPanelBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatisticPanelBehavior);
        mMaxPanelPadding = a.getDimensionPixelSize(R.styleable.StatisticPanelBehavior_behaviour_max_padding, context.getResources().getDimensionPixelSize(R.dimen.padding_large_24));
        a.recycle();

        mMinAppBarHeight = UiHelper.getActionBarHeight() + UiHelper.getStatusBarHeight();
        mMaxAppBarHeight = context.getResources().getDimensionPixelSize(R.dimen.size_profile_image);
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
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, final View dependency) {

        // Степень сжатия AppBar
        float currentFriction = UiHelper.currentFriction(mMinAppBarHeight, mMaxAppBarHeight, dependency.getBottom());
        // Padding на оснвании степени сжатия AppBar'a
        int currentPadding = UiHelper.lerp(0, mMaxPanelPadding, currentFriction);
        child.setPadding(0, currentPadding, 0, currentPadding);
        return super.onDependentViewChanged(parent, child, dependency);

/*
        Старый рабочий код, но черт ногу сломит в нем разобраться :-)
        // Шаг высоты аппара, при прохождении которого необходимо на еденицу изменить паддинг
        float panelStep = (dependency.getHeight() - mMinAppBarHeiht) / mMaxPanelPadding;
        int currentTopAndBottomPadding;
        currentTopAndBottomPadding = (int) ((dependency.getBottom() - mMinAppBarHeiht) / panelStep);
        // В крайних положениях минимальный и максимальный паддинг устанавливаем, если попали в последний либо первый шаг
        // Для всех остальных случаев высчитываем
        if (dependency.getBottom() - mMinAppBarHeiht <= panelStep) {
            currentTopAndBottomPadding = 0;
        } else if (currentTopAndBottomPadding >= mMaxPanelPadding) {
            currentTopAndBottomPadding = mMaxPanelPadding;
        }
        child.setPadding(0, currentTopAndBottomPadding, 0, currentTopAndBottomPadding);
        return super.onDependentViewChanged(parent, child, dependency);
*/
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }
}
