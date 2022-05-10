package com.szosen.dao12;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class MyData extends View {

    private List<Float> datas = new ArrayList<>();
    private Paint mPaint;
    private Path mPath;
    //心电图曲线的颜色
    private int color = Color.parseColor("#000000");
    //心电图的宽度
    private float line_width = 4f;
    private int view_width;
    private int view_height;
    private int baseLine;//基准线
    private int smallGridWith = 15;
    private int bigGridWidth = smallGridWith * 5;
    private int bigGridNum;
    //表示每个小格子放五个数据
    private int dataNumber = 5;

    //屏幕能够显示的所有的点的个数 注意这个值设置为int会导致这个数值不准确
    private float maxSize = 0;

    public MyData(Context context) {
        super(context);
        init();
        setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));
    }
    public MyData(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));
    }

    /**
     * 这个方法可以每隔格子放的数据数量
     * @param dataNumber
     */
    public void setDataNumber(int dataNumber) {
        this.dataNumber = dataNumber;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        view_width = w;
        view_height = h;
        bigGridNum = view_height / bigGridWidth;
        baseLine = bigGridNum /2 * bigGridWidth;
        maxSize =  view_width*(1.0f) / (smallGridWith / (dataNumber*1.0f));//125个数据占用为5个大格子，5个大格子有25个小格子，所以每个小格子放5个数据
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    private boolean isDrawFinish = false;
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //清除路径
        mPath.reset();
        if(datas != null && datas.size()>0){
            mPath.moveTo(0,change(datas.get(0)));
            //1 s更新125个数据，125个数据占用为5个大格(25个小格)
            //1个小格子为5个数据  1个数据为16/5小格 1小格的宽度为16 1个数据的宽度是16/5
            for (int i = 0;i<datas.size();i++){
                mPath.lineTo(i * smallGridWith /dataNumber,change(datas.get(i)));
            }
            canvas.drawPath(mPath,mPaint);
        }
        isDrawFinish = true;
    }
    /**
     * 添加数据
     * @param data
     */
    public void addData(Float data){
        if(datas.size() > maxSize){
            //如果这个集合大于maxSize（即表示屏幕上所能显示的点的个数）个点，那么就把第一个点移除
//            Log.d(TAG, "addData: "+maxSize);
            datas.remove(0);
        }
        datas.add(data);
        if(!isDrawFinish){
            return;
        }
        //这个方法会重新调用onDraw()方法 这个方法用在主线程
        invalidate();

    }
    /**
     * 把数据转化为对应的坐标  1大格表示的数据值为0.5毫伏，1毫伏= 200(数据) 1大格表示的数据 = 0.5 *200 1小格表示的数据 = 0.5*200/5 = 20
     * 1 小格的数据 表示为20 1小格的高度为16
     * @param data
     * @return
     */
    private float change(Float data){
        return (float) (-1.0) * data / 20 * smallGridWith + baseLine;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(line_width);
        mPath = new Path();
    }
}

