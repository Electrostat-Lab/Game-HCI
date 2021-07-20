package com.myGame.JMESurfaceViewExampleActivity;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.myGame.R;
import com.scrappers.superiorExtendedEngine.menuStates.UiStateManager;
import com.scrappers.superiorExtendedEngine.menuStates.uiPager.PageDataModel;
import com.scrappers.superiorExtendedEngine.menuStates.uiPager.UiPager;

import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * Test case for UiPager #{@link UiPager}.
 * @author pavl_g
 */
public class UiTestCase implements View.OnClickListener {
    private final UiStateManager uistateManager;
    private UiPager uiPager;
    private DataModel[] unSortedList = new DataModel[5];
    private final String[] texts = new String[]{"Search", "Revert", "paul", "Dismiss", "Matt"};
    public UiTestCase(final UiStateManager uistateManager){
        this.uistateManager = uistateManager;
    }

    /**
     * Test UiPager features
     * @throws Exception if processes failed.
     */
    public void testPagerUiStates() throws Exception {
        uiPager = new UiPager(uistateManager.getContext());
        uiPager.setId('J' + 'J');
        uiPager.setBackgroundColor(Color.BLACK);
        uiPager.setColumnCount(3);
        uiPager.setRowCount(3);
        ScrollView scrollView = uiPager.initializeScrollableContainer();
        scrollView.setId('J' + 'J');
        uistateManager.attachUiState(scrollView);
        for(int i = 0; i <unSortedList.length; i++){
            unSortedList[i] = new DataModel();
            unSortedList[i].setText(texts[i]);
        }
        unSortedList = uiPager.sort(unSortedList, UiPager.A_Z);
        //IDs
        final char[] iDs = new char[unSortedList.length];
        for (int position = 0; position < unSortedList.length; position++) {
            Button button = ((Button) ((LinearLayout) uistateManager.fromXML(R.layout.test_uipager, uiPager.getContext())).getChildAt(0));
            //fill the iDs with the first 2 digits of each state
            iDs[position] = unSortedList[position].getComparingData().charAt(0);
            button.setId(iDs[position]);
            button.setText(unSortedList[position].getSortingData());
            button.setOnClickListener(this);
            //attach the button
            uiPager.attachUiState(button, UiPager.SEQUENTIAL_ADD);
        }
    }

    @Override
    public void onClick(View v) {
            if(v.getId() == 'S'){
                Toast.makeText(uiPager.getContext(), "Search Button Clicked", Toast.LENGTH_LONG).show();
                uiPager.removeAllViews();
                try {
                    Executors.callable(()->{
                        System.out.println(Arrays.toString(uiPager.search(unSortedList, new String[]{"Revert","Dismiss"}, (uiState, position, currentItem) -> {
                            uiPager.addView(uiState);
                            uiState.setBackgroundColor(Color.MAGENTA);
                            if ( uiState.getId() == 'P' ){
                                uiState.setBackgroundColor(Color.RED);
                            }
                        })));
                    }).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(v.getId() == 'R'){
                Toast.makeText(uiPager.getContext(), "Revert Search clicked", Toast.LENGTH_LONG).show();

            }else if(v.getId() == 'D'){
                uiPager.animate().scaleY(0).scaleX(0).rotationY(45).setDuration(800).withEndAction(()->{
                    uiPager.detachAllUiStates();
                    uistateManager.detachUiState((ScrollView) uiPager.getParent());
                }).start();
            }
        }
        private static class DataModel extends PageDataModel {
            private int id;
            private String text;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            @Override
            public String getComparingData() {
                return getText();
            }

            @Override
            public String getSortingData() {
                return getText();
            }
        }
    }

