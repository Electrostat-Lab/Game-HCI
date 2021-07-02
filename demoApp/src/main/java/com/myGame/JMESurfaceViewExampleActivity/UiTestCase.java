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

import java.util.Arrays;

/**
 * Test case for UiPager #{@link UiPager}.
 * @author pavl_g
 */
public class UiTestCase implements View.OnClickListener {
    private final UiStateManager uistateManager;
    private UiPager uiPager;
    private String[] sortedList;
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

        //Test Sorting before adding items
        sortedList = uiPager.sort(new String[]{"Search Test", "Revert Search", "Pavly", "Thomas","Fady", "Dismiss" }, UiPager.A_Z);
        //IDs
        final char[] iDs = new char[sortedList.length];
        for (int position = 0; position < sortedList.length; position++) {
            Button button = ((Button) ((LinearLayout) uistateManager.fromXML(R.layout.test_uipager)).getChildAt(0));
            //fill the iDs with the first 2 digits of each state
            iDs[position] = sortedList[position].charAt(0);
            button.setId(iDs[position]);
            button.setText(sortedList[position]);
            button.setOnClickListener(this);
            //attach the button
            uiPager.attachUiState(button, UiPager.SEQUENTIAL_ADD);
        }
//        System.out.println(Arrays.toString(uiPager.sort(new String[]{"199", "110", "990", "99", "222", "333", "4455",}, UiPager.A_Z)));
//        System.out.println(Arrays.toString(uiPager.sort(new String[]{ "0?Dogy", "=Baka", "9ggBi", "D_aD", "dad", "amam", "-lolo", "\"hi", "Come", "come", "C", "F", "I", "Z", "A"}, UiPager.A_Z)));

    }

    @Override
    public void onClick(View v) {
            if(v.getId() == 'S'){
                Toast.makeText(uiPager.getContext(), "Search Button Clicked", Toast.LENGTH_LONG).show();
                try {
                    uiPager.removeAllViews();
                    uiPager.search(sortedList, new String[]{"Revert Search", "PAvlY", "Thomas"}, (uiState, position, currentItem) -> {
                        uiPager.addView(uiState);
                        uiState.setBackgroundColor(Color.MAGENTA);
                        if(uiState.getId() == 'P'){
                            uiState.setBackgroundColor(Color.RED);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(v.getId() == 'R'){
                Toast.makeText(uiPager.getContext(), "Revert Search clicked", Toast.LENGTH_LONG).show();

            }else if(v.getId() == 'D'){
                uiPager.animate().scaleY(0).scaleX(0).rotationY(45).setDuration(800).withEndAction(()->{
                    uiPager.detachAllUiStates();
                    uistateManager.detachUiState(uiPager);
                }).start();
            }
        }
        private class DataModel {
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
        }
    }

