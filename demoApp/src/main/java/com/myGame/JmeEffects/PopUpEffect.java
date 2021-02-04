package com.myGame.JmeEffects;


import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class PopUpEffect extends BaseAppState {
    private final AssetManager assetManager;
    private final Node node;
    private final String effectName;
    private final Vector3f gravity;
    private final float limitationTime=0.5f;
    private float counter=0.0f;
    private final ColorRGBA colorRGBA1;
    private final ColorRGBA colorRGBA2;
    private final Node wheel2;
    public PopUpEffect(AssetManager assetManager, Node node, Node wheel2, Vector3f gravity, String effectName, ColorRGBA colorRGBA1, ColorRGBA colorRGBA2){
        this.assetManager=assetManager;
        this.node = node;
        this.wheel2=wheel2;
        this.gravity=gravity;
        this.effectName=effectName;
        this.colorRGBA1=colorRGBA1;
        this.colorRGBA2=colorRGBA2;
    }
    private ParticleEmitter advancedEffects(){
        ParticleEmitter rocksEffect = new ParticleEmitter(effectName, ParticleMesh.Type.Triangle, 5000);
        rocksEffect.setParticlesPerSec(FastMath.pow(5,5f));
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("RocketLeauge/assets/Textures/Fire.png"));
//        fireMat.setTexture("GlowMap", assetManager.loadTexture("RocketLeauge/assets/Textures/Fire.png"));
//        fireMat.selectTechnique("Glow",getApplication().getRenderManager());
        rocksEffect.setMaterial(fireMat);
        rocksEffect.getParticleInfluencer().setInitialVelocity(gravity.mult(3f));
        rocksEffect.getParticleInfluencer().setVelocityVariation(0.8f);
        rocksEffect.setImagesX(1);
        rocksEffect.setImagesY(1);
        rocksEffect.setStartColor(colorRGBA1);
        rocksEffect.setEndColor(colorRGBA2);
        rocksEffect.setStartSize(0.2f);
        rocksEffect.setEndSize(0.5f);
        rocksEffect.setGravity(gravity);
        rocksEffect.setLowLife(0.2f);
        rocksEffect.setHighLife(0.8f);
        rocksEffect.center();
        return rocksEffect;
    }


    @Override
    protected void initialize(Application app) {
        node.attachChild(advancedEffects());
        wheel2.attachChild(advancedEffects());
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
            node.detachChild(node.getChild(effectName));
            wheel2.detachChild(wheel2.getChild(effectName));
            getStateManager().detach(this);
            counter=0.0f;
        }
    }
}

