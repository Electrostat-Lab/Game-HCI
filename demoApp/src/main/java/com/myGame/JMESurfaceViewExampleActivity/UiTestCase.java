package com.myGame.JMESurfaceViewExampleActivity;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.myGame.R;
import com.scrappers.superiorExtendedEngine.menuStates.UiStateManager;
import com.scrappers.superiorExtendedEngine.menuStates.uiPager.UiPager;

public class UiTestCase {
    private final UiStateManager uistateManager;
    public UiTestCase(final UiStateManager uistateManager){
        this.uistateManager = uistateManager;
    }
    public void testPagerUiStates(){
        UiPager uiPager = new UiPager(uistateManager.getContext());
        uiPager.setId('J' + 'J');
//        uiPager.setScrollContainer(true);
        uiPager.setBackgroundColor(Color.BLACK);
        uiPager.setLayoutParams(uistateManager.getLayoutParams());
        uiPager.setColumnCount(3);
        uiPager.setRowCount(3);
        ScrollView scrollView = uiPager.initializeScrollableContainer();
        scrollView.setId('J' + 'J');
        uistateManager.attachUiState(scrollView);

        for(int pos = 0; pos < 10; pos++){
            ((Button)((LinearLayout)uiPager.attachUiState(uistateManager.fromXML(R.layout.test_uipager), UiPager.SEQUENTIAL_ADD))
                    .getChildAt(0)).setText("heeeeeeee");
            uiPager.attachUiState(uistateManager.fromXML(R.layout.dialog_exception), UiPager.SEQUENTIAL_ADD);

        }
        ((LinearLayout)uiPager.getChildUiStateByIndex(0)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Hey", Toast.LENGTH_LONG).show();
                uiPager.detachUiState(uiPager.getChildUiStateByIndex(1));
//                uistateManager.detachUiState(uistateManager.getChildUiStateById('J' + 'J'));
            }
        });
        uiPager.attachUiState(uistateManager.fromXML(R.layout.main_menu), 1);

    }
}
