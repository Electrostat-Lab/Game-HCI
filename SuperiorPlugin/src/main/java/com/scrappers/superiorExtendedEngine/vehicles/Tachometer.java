package com.scrappers.superiorExtendedEngine.vehicles;

import android.content.Context;
import android.util.AttributeSet;

import com.scrappers.GamePad.R;
import com.scrappers.superiorExtendedEngine.gamePad.Speedometer;

import androidx.core.content.ContextCompat;

public class Tachometer extends Speedometer {
    public Tachometer(Context context) {
        super(context);
    }

    public Tachometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Tachometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize() {
        super.initialize();
        setCustomizeSpeedometer((tachometer) -> {
            getSpeedImpostor().setBackground(ContextCompat.getDrawable(getContext(), R.drawable.driving_pads));
//            getSpeedImpostor().setProgressTintList(ColorStateList.valueOf());
        });
    }
}
