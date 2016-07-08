package com.example.dllo.carouselimage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dllo on 16/7/7.
 */
public class CarouselAdapter extends PagerAdapter {
    //PagerAdapter 的Item 是View
    private List<ImageView> imageViews;

    public void setImageViews(int[] photos, Context context) {
        imageViews = new ArrayList<>();
        for (int photo : photos) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(photo);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViews.add(imageView);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        //设置成无上限的数量,可以无限的滑动
        return imageViews != null ? Integer.MAX_VALUE : 0;//返回Integer的最大值
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //取出指定位置的图片,对集合取余
        ImageView imageView = imageViews.get(position % imageViews.size());
        /*
        当图片少的时候,不会触发destroyItem
        这个时候去向container中addView会报错
        手动捕获这个异常
         */
        try {
            container.addView(imageView);
        } catch (IllegalStateException e) {
            container.removeView(imageView);//先移除
            container.addView(imageView);//然后添加进去
        }

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (container.getChildAt(position % imageViews.size()) == object) {
            //销毁指定位置的ImageView,回收内存
            container.removeViewAt(position % imageViews.size());
        }
    }
}
