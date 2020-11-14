package com.myGame;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class NitroState extends BaseAppState {
    private final AssetManager assetManager;
    private final Node nitroNode;
    private final String effectName;
    private final Vector3f gravity;
    private final float limitationTime=3f;
    private float counter=0.0f;
    private final ColorRGBA colorRGBA1;
    private final ColorRGBA colorRGBA2;
    public NitroState(AssetManager assetManager, Node nitroNode,Vector3f gravity, String effectName,ColorRGBA colorRGBA1,ColorRGBA colorRGBA2){
        this.assetManager=assetManager;
        this.nitroNode=nitroNode;
        this.gravity=gravity;
        this.effectName=effectName;
        this.colorRGBA1=colorRGBA1;
        this.colorRGBA2=colorRGBA2;
    }
    private ParticleEmitter advancedEffects(){
        ParticleEmitter rocksEffect = new ParticleEmitter(effectName, ParticleMesh.Type.Triangle, 5000);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
//        fireMat.setTexture("Texture", assetManager.loadTexture("RocketLeauge/assets/Textures/Fire.png"));
        rocksEffect.setMaterial(fireMat);
        rocksEffect.getParticleInfluencer().setInitialVelocity(gravity);
        rocksEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        rocksEffect.setImagesX(1);
        rocksEffect.setImagesY(1);
        rocksEffect.setStartColor(colorRGBA1);
        rocksEffect.setEndColor(colorRGBA2);
        rocksEffect.setStartSize(0.2f);
        rocksEffect.setEndSize(0.1f);
        rocksEffect.setGravity(gravity);
        rocksEffect.setLowLife(0.5f);
        rocksEffect.setHighLife(1f);
        rocksEffect.center();
        return rocksEffect;
    }


    @Override
    protected void initialize(Application app) {

        nitroNode.attachChild(advancedEffects());
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
            nitroNode.detachChild(nitroNode.getChild(effectName));
            getStateManager().detach(this);
            counter=0.0f;
        }
    }
}
