package com.example.wx091.allfiles.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by wx091 on 2015/12/14.
 */
public class FullScreenVideoView extends VideoView {


    public FullScreenVideoView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
    }
    public FullScreenVideoView (Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }
    public FullScreenVideoView(Context context, AttributeSet attrs,int defStyle)
    {
        super(context,attrs,defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width , height);
    }


}