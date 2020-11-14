package com.myGame;


import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Pavly
 */
public class MotionEffect extends BaseAppState {

    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private FilterPostProcessor fpp;
    private LightScatteringFilter light;
    private final float limitationTime=2f;
    private float counter=0.0f;
    public MotionEffect(AssetManager assetManager,ViewPort viewPort){
        this.assetManager=assetManager;
        this.viewPort=viewPort;

    }


    public void add(Vector3f directionOfEffect,float blurStart,float blurWidth,boolean state){


        fpp=new FilterPostProcessor(assetManager);
        light = new LightScatteringFilter(directionOfEffect);
        light.setBlurStart(blurStart);
        light.setBlurWidth(blurWidth);
        fpp.addFilter(light);

        viewPort.addProcessor(fpp);
        light.setEnabled(state);

    }
    public void run(Vector3f directionOfEffect,float lightDensity,boolean state){
        try{

            light.setLightPosition(directionOfEffect);
            light.setLightDensity(lightDensity);
            light.setEnabled(state);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void removeProcessorEffect(){
        try{
            light.setEnabled(false);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tpf) {
        counter+=tpf;
        if(counter>limitationTime){
            removeProcessorEffect();
            counter=0.0f;
        }
    }
}

