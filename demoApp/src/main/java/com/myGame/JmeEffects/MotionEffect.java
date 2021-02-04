package com.myGame.JmeEffects;


import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Pavly
 */
public class MotionEffect extends BaseAppState {

    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private FilterPostProcessor fpp;
    private BloomFilter bloomFilter;
    private final float limitationTime=2f;
    private float counter=0.0f;
    public MotionEffect(AssetManager assetManager,ViewPort viewPort){
        this.assetManager=assetManager;
        this.viewPort=viewPort;

    }


    public void add(){
        fpp=new FilterPostProcessor(assetManager);
        bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloomFilter.setBlurScale(0.1f);
        bloomFilter.setBloomIntensity(0.2f);
        bloomFilter.setDownSamplingFactor(1);
        bloomFilter.setExposurePower(1);

        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        fpp.addFilter(bloomFilter);
        viewPort.addProcessor(fpp);
        fpp.addFilter(bloomFilter);


        viewPort.addProcessor(fpp);
        bloomFilter.setEnabled(false);

    }
    public void run(){
        try{

            bloomFilter.setEnabled(true);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void removeProcessorEffect(){
        try{
            bloomFilter.setEnabled(false);
            fpp.removeAllFilters();
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

