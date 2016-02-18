package com.example.wx091.allfiles.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wx091.allfiles.R;
import com.example.wx091.allfiles.Utils.FileUtil;
import com.example.wx091.allfiles.beans.IConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.channels.FileLockInterruptionException;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PhotoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Context mContext;
    public int position,width, height=0;
    File[] files;
    TextView titleTv;
    Button leftBtn,rightBtn;
    //final private DisplayImageOptions options =imageLoader.d
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_photo);
        mContext=this;
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;     // 屏幕宽度（像素）
        height = metric.heightPixels;
        //载入图片资源ID
        initView();



    }

    private void initView() {
        Intent intent = getIntent();
        position = intent.getIntExtra("childPosition",-1);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        titleTv= (TextView) findViewById(R.id.show_photo_title);
        leftBtn= (Button) findViewById(R.id.show_photo_back);
        rightBtn= (Button) findViewById(R.id.show_photo_next);
        //read all files in foldpath[0], same with mainactivity
        File file = FileUtil.makeDir(IConstant.FoldPath[1]);
        files = file.listFiles();

        viewPager.setAdapter(new MyAdapter());
        viewPager.setOffscreenPageLimit(1);
        //viewPager.setOffscreenPageLimit(2);
        titleTv.setText(files[position].getName());
        viewPager.setCurrentItem(position);
//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (position == 0) {
//                    if(positionOffsetPixels==0){
//                        Toast.makeText(PhotoActivity.this,"已经是第一页了",Toast.LENGTH_SHORT).show();
//                    }
//                } else if (position==files.length-1){
//                    Toast.makeText(PhotoActivity.this,"已经是最后一页了",Toast.LENGTH_SHORT).show();
//                }
//                //super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.arrowScroll(1);
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.arrowScroll(2);//右翻页
            }
        });
    }


    /**
     *
     * @author xiaanming
     *
     */
    public class MyAdapter extends PagerAdapter {

        ImageView iv;
        Bitmap bitmap;
        @Override
        public int getCount() {
            return files.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (View)arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            iv = new ImageView(mContext);
            try {
                //bitmap=GetBitmap(files[position].getAbsolutePath());
                bitmap=decodeSampledBitmapFromResource(files[position].getAbsolutePath(),width,height);
                iv.setImageBitmap(bitmap);
            } catch (OutOfMemoryError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            titleTv.setText(files[position].getName());
            container.addView(iv, 0);
            return iv;
        }


    }


    public static Bitmap GetBitmap(String filePath) {
        FileInputStream fs = null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            // opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            fs = new FileInputStream(filePath);
            return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, opt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int width,int height ) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options,width ,height);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
