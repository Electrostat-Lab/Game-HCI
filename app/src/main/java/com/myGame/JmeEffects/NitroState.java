package com.myGame.JmeEffects;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
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
    private ViewPort viewPort;
    private MotionEffect motionEffect;
    public NitroState(AssetManager assetManager, Node nitroNode,Vector3f gravity, String effectName,ColorRGBA colorRGBA1,ColorRGBA colorRGBA2){
        this.assetManager=assetManager;
        this.nitroNode=nitroNode;
        this.gravity=gravity;
        this.effectName=effectName;
        this.colorRGBA1=colorRGBA1;
        this.colorRGBA2=colorRGBA2;
    }

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
//        motionEffect=new MotionEffect(assetManager,viewPort);
//        motionEffect.add();
    }

    private ParticleEmitter advancedEffects(){
        ParticleEmitter rocksEffect = new ParticleEmitter(effectName, ParticleMesh.Type.Triangle, 5000);
        rocksEffect.setParticlesPerSec(FastMath.pow(5,5));
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("RocketLeauge/assets/Textures/Fire.png"));
        fireMat.setTexture("GlowMap", assetManager.loadTexture("RocketLeauge/assets/Textures/Fire.png"));
//        fireMat.selectTechnique("Glow",getApplication().getRenderManager());
//        fireMat.setColor("GlowColor",ColorRGBA.Blue);
        rocksEffect.setMaterial(fireMat);
        rocksEffect.getParticleInfluencer().setInitialVelocity(gravity.mult(3.5f));
        rocksEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        rocksEffect.setImagesX(1);
        rocksEffect.setImagesY(1);
        rocksEffect.setStartColor(colorRGBA1);
        rocksEffect.setEndColor(colorRGBA2);
        rocksEffect.setStartSize(0.25f);
        rocksEffect.setEndSize(0.05f);
        rocksEffect.setGravity(gravity);
        rocksEffect.setLowLife(0.5f);
        rocksEffect.setHighLife(1f);
        rocksEffect.center();
        return rocksEffect;
    }


    @Override
    protected void initialize(Application app) {

        nitroNode.attachChild(advancedEffects());
//        motionEffect.run();
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
//            motionEffect.removeProcessorEffect();
            counter=0.0f;
        }
    }
}
