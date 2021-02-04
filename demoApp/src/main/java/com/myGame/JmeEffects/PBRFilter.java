package com.myGame.JmeEffects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public class PBRFilter extends Filter {
    private Material pbrShaderMaterial;
    private AssetManager assetManager;
    public PBRFilter(){

    }
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        pbrShaderMaterial = new Material(manager, "Common/MatDefs/Light/PBRLighting.j3md");
        this.assetManager=manager;
    }

    @Override
    protected Material getMaterial() {
//        pbrShaderMaterial.setTexture("Texture",assetManager.loadTexture("RocketLeauge/assets/Textures/bronzeCopperTex.jpg"));
        pbrShaderMaterial.setFloat("Metallic", 0.2f);
        /*Roughness , 1 is the max roughnesss*/
        pbrShaderMaterial.setFloat("Roughness", 0.3f);
        pbrShaderMaterial.setFloat("EmissivePower",1.0f);
        pbrShaderMaterial.setVector3("LightDir",new Vector3f(-2,-2,-2).mult(250).normalize());
        return pbrShaderMaterial;
    }
}
