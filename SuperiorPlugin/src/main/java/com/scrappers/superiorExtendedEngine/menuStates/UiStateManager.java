package com.scrappers.superiorExtendedEngine.menuStates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ConcurrentModificationException;
import java.util.HashMap;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

/**
 * A UI-State-Manager that's attached to any container android view that is a subclass of the #{@link ViewGroup} hierarchy.
 * <ul>
 * <li>XML layout resources #{@link androidx.annotation.LayoutRes} can be fetched by their resourceId & parsed as a UI-State under the stack of this hierarchy.</li>
 * <li>UI-States can be fetched by their iDs or their indices.</li>
 * </ul>
 * @author Pavly Gerges aka pavl_g.
 */
@SuppressLint("ViewConstructor")
public class UiStateManager extends RelativeLayout {
    private ViewGroup context;
    private int stateIndex=0;
    private final HashMap<Integer, View> uiStates=new HashMap<>();
    /**
     * Creates a new UI-State-Manager that acts as a rootNode for the other game UI-States stacks.
     * @param context the context parent to place the UI-State-Manager on top of it.
     */
    public UiStateManager(ViewGroup context) {
        super(context.getContext());
        /*attach the parent View state*/
        setLayoutParams(new LayoutParams(context.getLayoutParams().width,context.getLayoutParams().height));
        context.addView(this);
        this.context=context;
    }
    public UiStateManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiStateManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UiStateManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
        /**
         * gets the map of the UiStates stack
         * @return a HashMap representing the UiStates stack ,
         * where the key is the index of the UiState & the value represents the UiState instance.
         */
    public HashMap<Integer, View> getUiStates() {
        return uiStates;
    }
    /**
     * gets the parent Context of which the UiStateManager is attached to.
     * @return a viewGroup instance representing that parent context.
     */
    public ViewGroup getUiStackParentContext() {
        return context;
    }
    /**
     * detaches the UI-Manager from the parent Context.
     * @return true if detached successfully , false otherwise.
     */
    public boolean detachUiManager(){
        try {
            context.removeView(this);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * gets a view from an xml-based attribute set.
     * @param resId layout resource id.
     * @return a view fetched from that layout resource xml file.
     * @see UiStateManager#attachUiState(View).
     */
    public View fromXML(@LayoutRes int resId, Context context){
        return LayoutInflater.from(context).inflate(resId, null);
    }
    /**
     * Attach new UIState to the UI-State Factory
     * @param uiState the UI state that extends #{@link View}.
     * @see UiStateManager#fromXML(int, Context).
     * @apiNote the uiStates are filling the UiStateContainer by default
     */
    public <T extends View> T attachUiState(T uiState){
        addView(uiState);
        uiStates.put(stateIndex++, uiState);
        uiState.setLayoutParams(getLayoutParams());
        uiState.setVisibility(VISIBLE);
        return uiState;
    }
    /**
     * gets the index of the Last UI-State attached to the UI-State-Manager.
     * @return the index of the last UI state.
     */
    public int getLastStateIndex() {
        return stateIndex;
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
    public View getChildUiStateByIndex(@UseIndex int index){
        return uiStates.get(index);
    }
    /**
     * Detaches all game Ui states from the UI-Manager.
     */
    public void detachAllUiStates(){
        removeAllViews();
        if(!uiStates.values().isEmpty()){
            uiStates.clear();
        }
        stateIndex = 0;
    }
    /**
     * detaches the specified Ui-State.
     * @param view the view instance of the Ui-State to detach.
     */
    public void detachUiState(View view){
        removeView(view);
        uiStates.values().remove(view);
    }
    /**
     * detaches the specified UI-State by index.
     * @param index the index of the UI-State by stacks convention.
     * @return the removed view
     */
    @UseIndex
    public <T extends View> T detachUiStateByIndex(@UseIndex int index){
        View uiState = getChildUiStateByIndex(index);
        if(hasUiStateByIndex(index)){
            removeView(uiState);
        }
        if(uiStates.containsValue(uiState)){
            uiStates.values().remove(uiState);
        }
        return (T) uiState;
    }
    /**
     * detach a UI-State from the State-Manager by id.
     * @param id the UI-State id.
     * @return the removed view
     */
    public <T extends View> T detachUiStateById(@IdRes int id){
        View uiState = getChildUiStateById(id);
        if(hasUiStateById(id)){
            removeView(uiState);
        }
        if(uiStates.containsValue(uiState)){
            uiStates.values().remove(uiState);
        }
        return (T) uiState;
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
     * Loop over UI-States & do things , w/o modifying #{@link UiStateManager#uiStates} stack size.
     * @param uiStatesLooper a non-modifiable annotated interface to include the piece of code , that would be executed for each UI-State.
     */
    @UiStatesLooper.NonModifiable
    public void forEachUiState(UiStatesLooper.NonModifiable.Looper uiStatesLooper){
        final int modCount=uiStates.size();
        for(int position=0;position<uiStates.size() && modCount==uiStates.size(); position++) {
             uiStatesLooper.applyUpdate(getChildUiStateByIndex(position),position);
        }
        if(modCount!=uiStates.size()){
            throw new ConcurrentModificationException("Modification of UIStates Stack positions or size isn't allowed during looping over !");
        }
    }
    /**
     * Loop over UI-States & do things , w/ or w/o modifying #{@link UiStateManager#uiStates} stack size.
     * @param uiStatesLooper a modifiable annotated interface to include the piece of code , that would be executed for each UI-State.
     */
    @UiStatesLooper.Modifiable
    public void forEachUiState(UiStatesLooper.Modifiable.Looper uiStatesLooper){
        for(int position=0;position<uiStates.size(); position++) {
            uiStatesLooper.applyUpdate(getChildUiStateByIndex(position),position);
        }
    }
}
