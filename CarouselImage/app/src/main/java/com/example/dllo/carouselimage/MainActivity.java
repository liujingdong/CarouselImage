package com.example.dllo.carouselimage;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewGroup viewGroup;//里面放与轮播图联动的线性布局,(小圆点)
    private ImageView[] imgCircles;//放小圆点的集合
    private boolean userTouch = false;//判断用户是否触摸
    private Handler handler;//刷新UI
    private boolean threadAlive = true;//用来销毁线程的
    private CarouselAdapter carouselAdapter;
    //轮播图中的图片来源,只是一个例子,也可以是从网络拉取的图片
    private int photos[] = {R.mipmap.page_one, R.mipmap.page_two, R.mipmap.page_three,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewGroup = (ViewGroup) findViewById(R.id.carousel_layout);
        viewPager = (ViewPager) findViewById(R.id.carousel_view_page);
        carouselAdapter = new CarouselAdapter();
        carouselAdapter.setImageViews(photos, this);
        viewPager.setAdapter(carouselAdapter);
        getCircle();

    }

    //添加联动的圆点
    public void getCircle() {
        final int size = photos.length;//应该添加的小圆点数量
        imgCircles = new ImageView[size];//初始化放小圆点的集合
        for (int i = 0; i < imgCircles.length; i++) {
            ImageView iv = new ImageView(this);
            //设置小圆点的大小
            iv.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            //设置进去
            imgCircles[i] = iv;
            //设置默认第一个圆点为蓝色,其他为黑色
            if (i == 0) {
                imgCircles[i].setImageResource(R.mipmap.blue_view);
            } else {
                imgCircles[i].setImageResource(R.mipmap.black_view);
            }
            //设置放小圆点的线性布局的大小
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            //圆点左右相距为10
            layoutparams.leftMargin = 10;
            layoutparams.rightMargin = 10;
            viewGroup.addView(iv, layoutparams);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /*
             onPageScrolled(int arg0,float arg1,int arg2)，当页面在滑动的时候会调用此方法，
             在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
             arg0 :当前页面，及你点击滑动的页面
             arg1:当前页面偏移的百分比
             arg2:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            /*
            onPageSelected(int position):此方法是页面跳转完后得到调用，
            position是你当前选中的页面的Position（位置编号）
             */
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < size; i++) {
                    if (i == position % size) {
                        imgCircles[i].setImageResource(R.mipmap.blue_view);
                    } else {
                        imgCircles[i].setImageResource(R.mipmap.black_view);
                    }
                }

            }

            /*
            onPageScrollStateChanged(int arg0)   ，此方法是在状态改变的时候调用，其中arg0这个参数

            有三种状态（0，1，2）。arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //当用户触摸轮播图的时候
                        userTouch = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        //用户手离开手机
                        userTouch = false;
                        break;
                }

                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (threadAlive) {
                    //每隔3秒自动滑动一次
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!userTouch) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        }).start();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //获取当前的位置,再将ViewPager刷新到下一页
                int current = viewPager.getCurrentItem();
                //刷新轮播
                viewPager.setCurrentItem(current + 1);
                return false;
            }
        });
    }

    //记得把线程杀死
    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadAlive = false;
    }
}
