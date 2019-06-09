package com.minminaya.genaral.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import com.minminaya.R;
import com.minminaya.abs.GeneralRoundViewImpl;
import com.minminaya.abs.IRoundView;

/**
 * 自定义view增加圆角裁剪
 *
 * @author minminaya
 * @email minminaya@gmail.com
 * @time Created by 2019/6/8 18:11
 */
public class GeneralRoundImageView extends AppCompatImageView implements IRoundView {
    private GeneralRoundViewImpl generalRoundViewImpl;

    public GeneralRoundImageView(Context context) {
        this(context, null);
    }

    public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(this, context, attrs);
    }


    public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(this, context, attrs);
    }

    @Override
    public void setCornerRadius(float cornerRadius) {
        generalRoundViewImpl.setCornerRadius(cornerRadius);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        generalRoundViewImpl.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        generalRoundViewImpl.beforeDispatchDraw(canvas);
        super.dispatchDraw(canvas);
        generalRoundViewImpl.afterDispatchDraw(canvas);
    }

    private void init(GeneralRoundImageView view, Context context, AttributeSet attrs) {
        generalRoundViewImpl = new GeneralRoundViewImpl(view,
                context,
                attrs,
                R.styleable.GeneralRoundImageView,
                R.styleable.GeneralRoundImageView_corner_radius);
    }

}
