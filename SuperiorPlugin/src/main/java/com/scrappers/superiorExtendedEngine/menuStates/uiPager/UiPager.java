package com.scrappers.superiorExtendedEngine.menuStates.uiPager;

import android.content.Context;
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
    }

    /**
     * Create a UiPager UiState group, to hold some UiStates, & add that UiPager UiState to another container UiState
     * @param viewGroup the viewGroup container to add on
     */
    public UiPager(ViewGroup viewGroup) {
        super(viewGroup.getContext());
        viewGroup.addView(this);
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

    public <T extends View> T detachUiState(View view){
        removeView(view);
        return (T) view;
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

}
