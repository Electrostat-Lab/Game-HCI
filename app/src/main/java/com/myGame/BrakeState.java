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

public class BrakeState extends BaseAppState {
    private final AssetManager assetManager;
    private final Node wheelNode;
    private final String effectName;
    private final float limitationTime=3f;
    private float counter=0.0f;
    public BrakeState(AssetManager assetManager, Node wheelNode, String effectName){
        this.assetManager=assetManager;
        this.wheelNode = wheelNode;
        this.effectName=effectName;
    }
    private ParticleEmitter advancedEffects(){
        ParticleEmitter rocksEffect = new ParticleEmitter("effect", ParticleMesh.Type.Triangle, 5000);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
//        fireMat.setTexture("Texture", assetManager.loadTexture("RocketLeauge/assets/Textures/Fire.png"));
        rocksEffect.setMaterial(fireMat);
        rocksEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0f, -1f));
        rocksEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        rocksEffect.setImagesX(1);
        rocksEffect.setImagesY(1);
        rocksEffect.setStartColor(ColorRGBA.Cyan);
        rocksEffect.setEndColor(ColorRGBA.Blue);
        rocksEffect.setStartSize(0.2f);
        rocksEffect.setEndSize(0.1f);
        rocksEffect.setGravity(0f,0f,-1f);
        rocksEffect.setLowLife(0.5f);
        rocksEffect.setHighLife(1f);
        rocksEffect.center();
        return rocksEffect;
    }


    @Override
    protected void initialize(Application app) {

        wheelNode.attachChild(advancedEffects());
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
            wheelNode.detachChild(wheelNode.getChild(effectName));
            getStateManager().detach(this);
            counter=0.0f;
        }
    }
}
