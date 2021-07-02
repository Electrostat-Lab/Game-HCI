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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

/**
 * A UiState Class that could hold multiple UiStates in a list/grid form.
 * The UiPager, UiState does extend #{@link GridLayout}, so you can use all the functionality of it as rows/columns.
 * @author pavl_g
 */
public class UiPager extends GridLayout {
    public static final int SEQUENTIAL_ADD = -1;
    public static final int A_Z = 200;
    public static final int Z_A = 300;
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
        stateIndex = 0;
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
     * Runs an anonymous asynchronous searching task function for some items inside a searchList based on a list of searchKeyWords.
     * @param searchList the search list you want to traverse through, it should be parallel to the UiStates you want to update.
     * @param searchKeyWords the keywords you want to look for inside this search list.
     * @param injector injects what you want to do when an item is returned by the search engine.
     * @return a list of the founded strings from the searchList based on the search keywords.
     * @throws Exception throws an exception if the search custom thread fails.
     * @apiNote <b> <T extends Object> synchronized(T)</b> marks a thread-safe function by the dead locking other threads synchronized on the same object scheduled or started by the thread factory.
     */
    public String[] search(String[] searchList, String[] searchKeyWords, ActionInjector injector) throws Exception {
        synchronized(this) {
            final String[][] resultList = {new String[0]};
            return Executors.callable(() -> {
                for (int pos = 0; pos < searchList.length; pos++) {
                    for (String keyword : searchKeyWords) {
                        if (searchList[pos].replaceAll(" ","").trim().toLowerCase().contains(keyword.replaceAll(" ","").trim().toLowerCase())) {
                            //dynamic array conception
                            if(pos >= resultList[0].length-1){
                                resultList[0] = Arrays.copyOf(resultList[0], resultList[0].length+1);
                            }
                            resultList[0][pos] = searchList[pos];
                            if(injector != null){
                                injector.execute(getChildUiStateByIndex(pos), pos);
                            }
                            break;
                        }
                    }
                }
            }, resultList[0]).call();
        }
    }

    /**
     * Revert the search results executed by the search function, to the full length of UiStates, this doesn't stop the searching thread though, it rather waits until it finishes searching.
     * @param actionInjector injects actions to execute accompanied by the reversion.
     * @throws Exception throws an exception if the revert custom thread fails.
     * @apiNote <b> <T extends Object> synchronized(T)</b> marks a thread-safe function by the dead locking other threads synchronized on the same object scheduled or started by the thread factory.
     */
    public void revertSearchEngine(@Nullable ActionInjector actionInjector) throws Exception {
        synchronized(this){
            //format the states
            removeAllViews();
            Executors.callable(() -> forEachUiState((UiStatesLooper.Modifiable.Looper) (currentView, position) -> {
                if (actionInjector != null) {
                    actionInjector.execute(currentView, position);
                }
                addView(currentView, position);
            })).call();
        }
    }

    /**
     * Sort a String list either by A_Z or Z_A swapping algorithm, the sort runs in an async task in the same called thread.
     * @param list the list to order sort for.
     * @param sortAlgorithm the sort algorithm either {@link UiPager#A_Z} or {@link UiPager#Z_A}.
     * @return the new sorted String in the shape of Collection List.
     * @throws Exception if process is interrupted or -1 is returned.
     * @apiNote you will need to loop over this list to provide the uiStates with new update.
     */
    public String[] sort(String[] list, int sortAlgorithm) throws Exception {
        synchronized(this) {
            //to apply the sort change as an external final change on a list copy (warning : ->Internal List change(positions or items count or items values) = Malicious Activity
            final String[] copy = Arrays.copyOf(list, list.length);
            return Executors.callable(() -> {
                String tempPointer = "";
                    //main String List looping
                    for (int i = 0; i < copy.length; i++) {
                        //looping over the String again to compare each one String member var with the sequence of the String member vars after that item
                        for(int j = i+1; j < copy.length; j++ ){
                                //sort from A-Z ascendingly
                                if(sortAlgorithm == A_Z){
                                    //compare 2 strings lexicographically based on their characters, if the (string object > the argument string) then compareTo returns 1
                                    if ( copy[i].toLowerCase().compareTo(copy[j].toLowerCase()) > 0 ){
                                            //format the pointer
                                            tempPointer = "";
                                            //then swap list[i] & list[j] because list[i] is after the list[k]
                                            //store the list[i] inside the tempPointer for later access
                                            tempPointer = copy[i];
                                            //get the list[i] after
                                            copy[i] = copy[j];
                                            //get the list[j] before
                                            copy[j] = tempPointer;
                                    }
                                }else if(sortAlgorithm == Z_A){
                                    //compare 2 strings lexicographically based on their characters, if the (string object < the argument string) then compareTo returns -1
                                    if (  copy[i].toLowerCase().compareTo(copy[j].toLowerCase()) < 0){
                                            //format the pointer
                                            tempPointer = "";
                                            //then swap list[i] & list[j] because list[i] is before the list[k]
                                            //store the list[j] inside the tempPointer for later access
                                            tempPointer = copy[j];
                                            //get the list[j] before
                                            copy[j] = copy[i];
                                            //get the list[i] after
                                            copy[i] = tempPointer;
                                    }
                                }
                        }
                    }
            }, copy).call();
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
