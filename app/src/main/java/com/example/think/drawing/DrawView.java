package com.example.think.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;


public class DrawView extends View {
    private float preX;
    private float preY;
    private Path path;//路径
    private Paint paint;//画笔
    private Paint mBitmapPaint;
    private Path eraserPath;
    private Paint eraserPaint;
    Bitmap cacheBitmap=null;//定义一个内存中的图片，该图片将作为缓冲区
    Canvas cacheCanvas=null;//定义cacheBitmap上的Canvas对象

    private LinkedList<PathBean> undoList;
    private LinkedList<PathBean> redoList;
    private boolean isEraserModel;
    public DrawView(Context context) {
        super(context);
    }
    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
    }
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(Color.WHITE);
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        post(new Runnable() {//拿到控件的宽和高
                 @Override
                 public void run() {
                     cacheBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Config.ARGB_4444);
                     cacheCanvas = new Canvas();
                     mBitmapPaint = new Paint(Paint.DITHER_FLAG);
                     cacheCanvas.setBitmap(cacheBitmap);
                     cacheCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                     cacheCanvas.drawColor(Color.WHITE);
                 }
             });
        path = new Path();
        paint=new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setDither(true);


        undoList = new LinkedList<>();
        redoList = new LinkedList<>();
    }

    /*
     * 功能：重写onDraw方法
     * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cacheBitmap != null) {
            canvas.drawBitmap(cacheBitmap, 0, 0, mBitmapPaint);
            if (!isEraserModel) {
                if (null != path) {
                    canvas.drawPath(path, paint);
                }
            } else {
                if (null != eraserPath) {
                    canvas.drawPath(eraserPath, eraserPaint);
                }
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEraserModel) {
            commonTouchEvent(event);
        } else {
            eraserTouchEvent(event);
        }
        invalidate();
        return true;
    }
    private void eraserTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eraserPath = new Path();
                preX = x;
                preY = y;
                eraserPath.moveTo(preX, preY);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - preX);
                float dy = Math.abs(y - preY);
                if (dx >= 3 || dy >= 3) {
                    eraserPath.quadTo(preX, preY, (preX + x) / 2, (preY + y) / 2);
                }
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(eraserPath, eraserPaint);
                Path mpath = new Path(eraserPath);
                Paint mpaint = new Paint(eraserPaint);
                PathBean pb = new PathBean(mpath, mpaint);
                undoList.add(pb);
                eraserPath.reset();
                eraserPath = null;
                break;
        }
    }

    public void commonTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                preX=x;
                preY=y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx=Math.abs(x-preX);
                float dy=Math.abs(y-preY);
                if(dx >= 3 || dy >= 3){
                    path.quadTo(preX, preY, (x+preX)/2, (y+preY)/2);
                    preX=x;
                    preY=y;
                }
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(path, paint);
                Path mpath = new Path(path);
                Paint mpaint = new Paint(paint);
                PathBean pb = new PathBean(mpath, mpaint);
                undoList.add(pb);
                path.reset();
                break;
        }
    }
    public void undo() {
        if (!undoList.isEmpty()) {
            clearPaint();
            PathBean lastPb = undoList.removeLast();
            redoList.add(lastPb);
            for (PathBean pb : undoList) {
                cacheCanvas.drawPath(pb.path, pb.paint);
            }
            invalidate();
        }
    }
    private void clearPaint() {
        cacheCanvas.drawColor(Color.WHITE);
        invalidate();
    }
    public void redo() {
        if (!redoList.isEmpty()) {
            PathBean pathBean = redoList.removeLast();
            cacheCanvas.drawPath(pathBean.path, pathBean.paint);
            invalidate();
            undoList.add(pathBean);
        }
    }

    public void setPaintColor(@ColorInt int color) {
        paint.setColor(color);
        setPaintModel();
    }
    public void setStrokeWidth(float width){
        paint.setStrokeWidth(width);
        setPaintModel();
    }
    public void setAlpha(int alpha){
        paint.setAlpha(alpha);
    }
    public void setPaintModel(){this.isEraserModel=false;}
    public void clearAll() {
        clearPaint();
        preY = 0f;
        //清空 撤销 ，恢复 操作列表
        redoList.clear();
        undoList.clear();
    }
    //橡皮擦
    public void setEraserModel(boolean isEraserModel) {
        this.isEraserModel = isEraserModel;
        if (eraserPaint == null) {
            eraserPaint = new Paint(paint);
            eraserPaint.setStrokeWidth(40f);
            eraserPaint.setColor(Color.WHITE);
            //eraserPaint.setColor(Color.TRANSPARENT);
            //eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    public void save(){
        try{
            saveBitmap("myPaint");
        }catch(IOException e){
            e.printStackTrace();
        }

    }
    private void saveBitmap(String fileName) throws IOException {
        File file=new File(getSDPath()+fileName+".png");
        file.createNewFile();
        FileOutputStream fileOS=new FileOutputStream(file);
        //将绘图内容压缩为PNG格式输出到输出流对象中
        cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
        fileOS.flush();//将缓冲区中的数据全部写出到输出流中
        fileOS.close();//关闭文件输出流对象
    }


    //获得SD卡的根目录
    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();

    }

    class PathBean {
        Path path;
        Paint paint;

        PathBean(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }
}
