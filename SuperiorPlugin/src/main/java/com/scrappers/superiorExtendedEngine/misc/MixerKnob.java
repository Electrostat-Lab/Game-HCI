package com.scrappers.superiorExtendedEngine.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scrappers.GamePad.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class MixerKnob extends RelativeLayout {
    /*declaration of the movable part*/
    private CardView baseHolder;
    private float xOrigin;
    public MixerKnob(@NonNull Context appCompatActivity) {
        super(appCompatActivity);
    }

    public MixerKnob(@NonNull Context appCompatActivity, @Nullable AttributeSet attrs) {
        super(appCompatActivity, attrs);
    }

    public MixerKnob(@NonNull Context appCompatActivity, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(appCompatActivity, attrs, defStyleAttr);
    }
    public void initializeMixerKnob(int knobDrawable,int backgroundDrawable){
        this.setBackground(ContextCompat.getDrawable(this.getContext(),backgroundDrawable));
        /*initialization of mixer baseHolder part aka movable part*/
        baseHolder=new CardView(this.getContext());
        baseHolder.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_dharmachakra));
        ViewGroup.LayoutParams baseHolderParams=new RelativeLayout.LayoutParams(this.getLayoutParams().width,this.getLayoutParams().height);
        baseHolder.setLayoutParams(baseHolderParams);
        baseHolder.setX((float) this.getLayoutParams().width/2-(float) baseHolderParams.width/2);
        baseHolder.setY((float) this.getLayoutParams().height/2-(float)baseHolderParams.height/2);
//        baseHolder.setRadius((float) baseHolderParams.width/2);
        this.addView(baseHolder);
        /*Mixer Knob*/
        CardView knob=new CardView(baseHolder.getContext());
        knob.setBackground(ContextCompat.getDrawable(this.getContext(),R.drawable.knob_circle));
        knob.setLayoutParams(new RelativeLayout.LayoutParams(baseHolderParams.width/2,baseHolderParams.height/2));
        knob.setX((float) baseHolder.getLayoutParams().width/2-(float)knob.getLayoutParams().width/2);
        knob.setY((float) baseHolder.getLayoutParams().height/2-(float)knob.getLayoutParams().height/2);
        knob.setRadius((float)knob.getLayoutParams().width/2);
        baseHolder.addView(knob);
        /*knob image*/
        ImageView knobImage=new ImageView(knob.getContext());
        knobImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(),R.drawable.ic_baseline_blur_on_24));
        knobImage.setLayoutParams(new RelativeLayout.LayoutParams(knob.getLayoutParams().width,knob.getLayoutParams().height));
        knobImage.setX((float) knob.getLayoutParams().width/2-(float)knobImage.getLayoutParams().width/2);
        knobImage.setY((float) knob.getLayoutParams().height/2-(float)knobImage.getLayoutParams().height/2);
        knob.addView(knobImage);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*Tolerated Motion*/
        float xTolerance = 150f;
        float yTolerance=  150f;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getX() < xOrigin-xTolerance || event.getX()<0){
                    /*Left*/
                    baseHolder.setRotation(-Math.abs(event.getX()));
                }else if(event.getX() > xOrigin+xTolerance ){
                    /*Right*/
                   baseHolder.setRotation(Math.abs(event.getX()));
                }

                invalidate();

                break;

        }
        return true;
    }
}
