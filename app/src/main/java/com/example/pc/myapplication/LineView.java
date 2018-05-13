package com.example.pc.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

public class LineView extends View {

    Context context;
    Paint paint;
    Path newPath;
    float my_x,my_y,my_x2,my_y2;


    public LineView(Context cont, AttributeSet attributes) {
        super(cont, attributes);

        context = cont;
        newPath = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);

    }

    public void setParameters(float xx, float yy, float xEnd, float yEnd){
        my_x = xx;
        my_y = yy;
        my_x2 = xEnd;
        my_y2 = yEnd;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(newPath, paint);
    }

    void drawMyLine(float x, float y, float x2, float y2){
        startTouch(x, y);
        invalidate();
        moveTouch(x2, y2);
        upTouch();
    }

    private void startTouch(float x, float y) {
        newPath.moveTo(x, y);
        my_x = x;
        my_y = y;
    }

    private void moveTouch(float x, float y) {
            my_x = x;
            my_y = y;
    }

    private void upTouch() {
        newPath.lineTo(my_x, my_y);
    }

}
