package com.scrappers.superiorExtendedEngine.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.scrappers.GamePad.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

/**
 * @author pavl_g
 */
public class UTurnView extends CardView {
    private Paint paint;
    private Path path;
    private float xOrigin=0.0f;
    private float yOrigin=0.0f;
    private float xOffset=0.0f;
    private float yOffset=0.0f;
    private final HashMap<Integer,ArrayList<Float>> cartesianPoints=new HashMap<>();

    public UTurnView(@NonNull Context context) {
        super(context);
    }

    public UTurnView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UTurnView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public synchronized void initialize(){
        setRadius(40f);
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(getLayoutParams().width/5f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(null);
        paint.setAlpha(0xff);

        path=new Path();
        path.setFillType(Path.FillType.WINDING);


        xOrigin=getLayoutParams().width/2f;
        yOrigin=getLayoutParams().height/2f;
        xOffset=getLayoutParams().width/5f;
        yOffset=getLayoutParams().height/5f;

        cartesianPoints.put(0,addPoint(xOffset,yOffset));

        cartesianPoints.put(1,addPoint(interpolateLinearly(xOffset,getLayoutParams().width,0.25f),
                yOffset));

        cartesianPoints.put(2,addPoint(interpolateLinearly(xOffset,getLayoutParams().width,0.75f),
                interpolateLinearly(0f,getLayoutParams().height,0.5f)));

        cartesianPoints.put(3,addPoint(interpolateLinearly(xOffset,getLayoutParams().width,0.25f),
                interpolateLinearly(yOffset,getLayoutParams().height,0.75f)));

        cartesianPoints.put(4,addPoint(xOffset,getLayoutParams().height-yOffset));
    }

    private float interpolateLinearly(float point0,float point1,float factor){
        return point0+factor*(point1-point0);//where x1-x0 is the length of the line segment.
    }
    private ArrayList<Float> addPoint(float x,float y){
        ArrayList<Float> cartesianPoint=new ArrayList<>();
        cartesianPoint.add(x);
        cartesianPoint.add(y);
        return cartesianPoint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw a path
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.transparent));
        for(int index=0;index<cartesianPoints.size();index++){
            if(index!=0){
                path.moveTo(Objects.requireNonNull(cartesianPoints.get(index - 1)).get(0),
                        Objects.requireNonNull(cartesianPoints.get(index - 1)).get(1));
            }else{
                path.moveTo(xOffset,yOffset);
            }
            path.lineTo(Objects.requireNonNull(cartesianPoints.get(index)).get(0),
                    Objects.requireNonNull(cartesianPoints.get(index)).get(1));
        }
        canvas.drawPath(path,paint);
    }

}
