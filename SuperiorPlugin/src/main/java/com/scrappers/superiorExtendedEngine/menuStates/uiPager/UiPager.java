package com.scrappers.superiorExtendedEngine.menuStates.uiPager;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;
import com.scrappers.superiorExtendedEngine.menuStates.UiStateManager;
import com.scrappers.superiorExtendedEngine.menuStates.UiStatesLooper;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import androidx.annotation.IdRes;

/**
 * A UiState Class that could hold multiple UiStates in a list/grid form.
 * The UiPager, UiState does extend #{@link GridLayout}, so you can use all the functionality of it as rows/columns.
 * @author pavl_g
 */
public class UiPager extends GridLayout {
    public static final int SEQUENTIAL_ADD = -1;
    private final HashMap<Integer, View> uiStates = new HashMap<>();
    private int stateIndex=0;

    /**
     * Create a UiPager UiState group, to hold some UiStates
     * @param context the current context wrapper
     */
    public UiPager(Context context) {
        super(context);
        DisplayMetrics displayMetrics =  new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        setLayoutParams(new ViewGroup.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    /**
     * Create a UiPager UiState group, to hold some UiStates, & add that UiPager UiState to another container UiState
     * @param viewGroup the viewGroup container to add on
     */
    public UiPager(ViewGroup viewGroup) {
        super(viewGroup.getContext());
        viewGroup.addView(this);
        DisplayMetrics displayMetrics =  new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        setLayoutParams(new ViewGroup.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    /**
     * Formulate the UiPager instance (view) inside a Scrollable Container
     * @return a scroll view instance representing a Scrollable Container
     */
    public ScrollView initializeScrollableContainer(){
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.setLayoutParams(getLayoutParams());
            scrollView.setSmoothScrollingEnabled(true);
            scrollView.setScrollContainer(true);
            setScrollContainer(true);
            scrollView.addView(this);
        return scrollView;
    }

    /**
     * Attach a UiState to the current UiPager, in order using #{@link UiPager#SEQUENTIAL_ADD}, or using a position
     * @param stateView the ui state to add
     * @param positionIndex the position you want to insert this UiState inside
     * @param <T> an UiState that is a type of Android View
     * @return an UiState of the same type you have just inserted
     */
    public <T extends View> T attachUiState(T stateView, int positionIndex){
        if(positionIndex == SEQUENTIAL_ADD){
            addView(stateView, stateIndex);
        }else {
            addView(stateView, positionIndex);
        }
        uiStates.put(stateIndex++, stateView);
        return stateView;
    }
    /**
     * get the child UI-state of the #{@link UiStateManager} by id.
     * @param resId the id of the child UI-State to get.
     * @return an android view representing the specified view for this id.
     */
    public <T extends View> T getChildUiStateById(@IdRes int resId){
        return findViewById(resId);
    }
    /**
     * get a child Ui-State of the #{@link UiStateManager} by index.
     * @param index the index of the UI-State by stacks convention.
     * @return an android view representing the specified view for this index,
     */
    public View getChildUiStateByIndex(int index){
        return uiStates.get(index);
    }

    /**
     * removes a Ui-State from the current Ui-Pager & returns it back
     * @param view the view to remove
     * @param <T> a type of View based Ui-Component
     * @return the proposed removed Ui-State
     */
    public <T extends View> T detachUiState(View view){
        removeView(view);
        if(uiStates.containsValue(view)){
            uiStates.values().remove(view);
        }
        return (T) view;
    }

    /**
     * Checks if the current Ui-Pager has got Ui-States
     * @return true if there are Ui-States inside
     */
    public boolean hasUiStates(){
        return uiStates.size() > 0;
    }

    /**
     * Checks for the existence of a current Ui-State by an id
     * @param resId the id of the proposed Ui-State to check for
     * @return true if the proposed Ui-State exists
     */
    public boolean hasUiStateById(@IdRes int resId){
        return getChildUiStateById(resId) != null;
    }

    /**
     * Checks for the existence of a current Ui-State by an index in the UiPager Stack
     * @param index the index of the proposed Ui-State to check for
     * @return true if the proposed Ui-State exists
     */
    public boolean hasUiStateByIndex(int index){
        return getChildUiStateByIndex(index) != null;
    }
    /**
     * Detaches all game Ui states from the UI-Pager.
     */
    public void detachAllUiStates(){
        if(hasUiStates()){
            removeAllViews();
        }
        if(!uiStates.values().isEmpty()){
            uiStates.clear();
        }
    }
    /**
     * Traverse over through UI-States & do things, w/o modifying #{@link UiPager#uiStates} stack size.
     * @param uiStatesLooper a non-modifiable annotated interface to include the piece of code , that would be executed for each UI-State.
     */
    @UiStatesLooper.NonModifiable
    public void forEachUiState(UiStatesLooper.NonModifiable.Looper uiStatesLooper){
        final int modCount=uiStates.size();
        for(int position=0; position < uiStates.size() && modCount == uiStates.size(); position++) {
            uiStatesLooper.applyUpdate(getChildUiStateByIndex(position), position);
        }
        if(modCount != uiStates.size()){
            throw new ConcurrentModificationException("Modification of UIStates Stack positions or size isn't allowed during looping over !");
        }
    }
    /**
     * Traverse over UI-States & do things, w/ or w/o modifying #{@link UiPager#uiStates} stack size.
     * @param uiStatesLooper a modifiable annotated interface to include the piece of code , that would be executed for each UI-State.
     */
    @UiStatesLooper.Modifiable
    public void forEachUiState(UiStatesLooper.Modifiable.Looper uiStatesLooper){
        for(int position=0; position < uiStates.size(); position++) {
            uiStatesLooper.applyUpdate(getChildUiStateByIndex(position), position);
        }
    }
    /**
     * gets the index of the Last UI-State attached to the UI-State-Manager.
     * @return the index of the last UI state.
     */
    public int getLastStateIndex() {
        return stateIndex;
    }
}
