package com.myGame.JmEGamePadExample;

import android.content.Intent;
import android.graphics.Color;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.myGame.JmeEffects.NitroState;
import com.myGame.R;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.gamePad.GameStickView;
import com.scrappers.superiorExtendedEngine.gamePad.Speedometer;
import com.scrappers.superiorExtendedEngine.vehicles.GullWing;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class JmeGame extends SimpleApplication {

    private BulletAppState bulletAppState;
    private VehicleControl vehicle;
    private final float brakeForce = 300f;
    private final Vector3f jumpForce = new Vector3f(0, 2000, 0);
    private final Vector3f rushForce = Vector3f.UNIT_XYZ;
    private Spatial chassis;
    private final AppCompatActivity appCompatActivity;
    private GameStickView gameStick;
    private ChaseCamera chaseCam;

    public JmeGame(AppCompatActivity appCompatActivity){
        this.appCompatActivity=appCompatActivity;
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        addSky();
        createPhysicsTestWorld(rootNode, getAssetManager(), bulletAppState.getPhysicsSpace());
        buildPlayer();
//        addEnvLightProbe();

        /*LIBRARY CODE*/
        /*run the gamePad attachments & listeners from the android activity UI thread */
        /* create an instance of a class extending gameStickView to easily handle the listeners */
        gameStick = appCompatActivity.findViewById(R.id.gameStickView);

        final GameStick gameStickController=new GameStick();
        gameStickController.setChaseCamera(chaseCam);
        gameStickController.setVehicleControl(vehicle);
        gameStick.setGameStickListeners(gameStickController);

        final GullWing drivingWheelView =appCompatActivity.findViewById(R.id.steeringWheel);
        drivingWheelView.initializeWheel();
        drivingWheelView.initializeTachometer(gameStick,this,vehicle,3f);
        drivingWheelView.setOnSteering(gameStickController);

        gameStick.initializeStickPath();
        gameStick.setMotionPathColor(Color.WHITE);
        gameStick.initializeGameStickHolder(ControlButtonsView.ButtonStyle.NOTHING_IMAGE.STYLE);
        gameStick.initializeGameStick(ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonStyle.STICK_DASHES.STYLE,180);

        final Speedometer speedometer = appCompatActivity.findViewById(R.id.speedometer);
        speedometer.initialize();
        speedometer.getSpeedometerDrawable().setStroke(3, ContextCompat.getColor(appCompatActivity,R.color.gold));
        gameStick.createSpeedometerLink(speedometer,JmeGame.this,vehicle,1f);

        final ControlButtonsView controlButtonsView = appCompatActivity.findViewById(R.id.gamePadbtns);

        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_X, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.X_BUTTON_ALPHA.ID)
        .setOnClickListener(v -> appCompatActivity.startActivity(new Intent(appCompatActivity.getApplicationContext(), LanLogic.class)));

        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_Y, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.Y_BUTTON_ALPHA.ID);

        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_A, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.A_BUTTON_ALPHA.ID)
        .setOnClickListener(v -> vehicle.applyCentralImpulse(jumpForce));

        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_B, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.B_BUTTON_ALPHA.ID)
        .setOnClickListener(v -> {
                vehicle.applyCentralImpulse(vehicle.getLinearVelocity().mult(150));
                Node nitroNode=((Node)((Node) chassis).getChild("nitro"));
                NitroState nitroState=new NitroState(assetManager,nitroNode,Vector3f.UNIT_Z.negate(),"nitroEffect",new ColorRGBA(0f, 1f, 1f, 0.8f),new ColorRGBA(251f / 255f, 130f / 255f, 0f, 0.1f));
                nitroState.setViewPort(viewPort);
                stateManager.attach(nitroState);
        });
    }

    @Override
    public void destroy() {
//        /* deInitializeSensor Service on app exit , to be used with other apps */
        if( gameStick.getSensorManager() !=null){
            gameStick.deInitializeSensors();
        }
    }

    private void addSky() {
        Geometry sky = (Geometry) SkyFactory.createSky(assetManager,assetManager.loadTexture("RocketLeauge/assets/Textures/sky.jpg"),Vector3f.UNIT_XYZ, SkyFactory.EnvMapType.EquirectMap);
        sky.getMaterial().getAdditionalRenderState().setDepthFunc(RenderState.TestFunction.LessOrEqual);
        getRootNode().attachChild(sky);
    }

    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

    /**
     * creates a simple physics test world with a floor, an obstacle and some test boxes
     *
     * @param rootNode where lights and geometries should be added
     * @param assetManager for loading assets
     * @param space where collision objects should be added
     */
    public  void createPhysicsTestWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        AmbientLight a=new AmbientLight();
        a.setColor(new ColorRGBA(0.6f, 0.7f, 0.7f, 0.2f).mult(2));

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        material.setTexture("ColorMap", assetManager.loadTexture("RocketLeauge/assets/Textures/soccerTex.jpg"));

        Material soccerPlayGround = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        soccerPlayGround.setFloat("ShadowIntensity",2f);
        soccerPlayGround.setVector3("LightDir",new Vector3f(1,-20,1));
        soccerPlayGround.setInt("FilterMode",4);
        soccerPlayGround.setFloat("PCFEdge",0.1f);
        soccerPlayGround.setFloat("ShadowMapSize",0.1f);
        soccerPlayGround.setTexture("EnvMap",assetManager.loadTexture("RocketLeauge/assets/Textures/sky.jpg"));
//        soccerPlayGround.selectTechnique("PostShadow",getRenderManager());

//        soccerPlayGround.setTexture("DiffuseMap", assetManager.loadTexture("RocketLeauge/assets/Textures/soccer.jpg"));
        soccerPlayGround.setBoolean("UseMaterialColors",true);

        soccerPlayGround.setColor("Ambient",ColorRGBA.LightGray);
        soccerPlayGround.setColor("Specular",ColorRGBA.LightGray);
        soccerPlayGround.setFloat("Shininess",1f);
        Spatial floorGeometry = assetManager.loadModel("RocketLeauge/assets/Scenes/town/main.scene");
//        floorGeometry.setMaterial(soccerPlayGround);
        DirectionalLight directionalLight=new DirectionalLight(new Vector3f(-3,-floorGeometry.getLocalScale().getY()*4,-3).normalize());
        directionalLight.setColor(ColorRGBA.White.mult(1f));
        floorGeometry.addLight(a);
        rootNode.addLight(directionalLight);

        floorGeometry.setLocalTranslation(0f,-10f,0f);
        floorGeometry.setLocalScale(10f);

        RigidBodyControl rigidBodyControl=new RigidBodyControl(CollisionShapeFactory.createMeshShape(floorGeometry),0);
        floorGeometry.setCullHint(Spatial.CullHint.Never);
        rigidBodyControl.setFriction(20f);
        floorGeometry.addControl(rigidBodyControl);
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);

        //ball sphere with mesh collision shape
        Sphere sphere = new Sphere(15, 15, 5f);

        Geometry sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(createMat(ColorRGBA.White,"RocketLeauge/assets/Textures/soccerTex.jpg", sphereGeometry));
        sphereGeometry.setLocalTranslation(0f, -5f, 0f);
        sphereGeometry.setShadowMode(RenderQueue.ShadowMode.Cast);

        RigidBodyControl ballControl=new RigidBodyControl(new SphereCollisionShape(5f), 0.5f);
        ballControl.setFriction(2f);
        ballControl.setLinearVelocity(new Vector3f(0.2f,0.2f,0.2f));
//        ballControl.setRollingFriction(1f);


        sphereGeometry.addControl(ballControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);

        DirectionalLightShadowRenderer dlsr=new DirectionalLightShadowRenderer(assetManager,512,1);
        dlsr.setLight(directionalLight);
        dlsr.setShadowIntensity(0.2f);
        dlsr.setLambda(0.55f);
        dlsr.setShadowCompareMode(CompareMode.Hardware);
        dlsr.setShadowZExtend(23f);
        dlsr.setShadowZFadeLength(8f);
        floorGeometry.setShadowMode(RenderQueue.ShadowMode.Receive);
        viewPort.addProcessor(dlsr);

        BloomFilter bloomFilter=new BloomFilter();
        bloomFilter.setDownSamplingFactor(2);
        bloomFilter.setExposurePower(3);
        bloomFilter.setBloomIntensity(1f);
        bloomFilter.setBlurScale(0.2f);
        FilterPostProcessor filterPostProcessor=new FilterPostProcessor(assetManager);
        filterPostProcessor.addFilter(bloomFilter);
        viewPort.addProcessor(filterPostProcessor);
    }

    private void addPointLight(Spatial node,Vector3f position,ColorRGBA colorRGBA){
        PointLight pointLight=new PointLight();
        pointLight.setColor(colorRGBA);
        pointLight.setPosition(position);
        pointLight.setRadius(2000);
        node.addLight(pointLight);
    }
    private void addEnvLightProbe(){
        EnvironmentCamera envCam=new EnvironmentCamera();
        stateManager.attach(envCam);
        envCam.initialize(stateManager,this);
        LightProbe lightProbe=LightProbeFactory.makeProbe(envCam,rootNode);
        lightProbe.getArea().setRadius(500);
        lightProbe.setPosition(new Vector3f(0,20,0));
        rootNode.addLight(lightProbe);
    }

    private void buildPlayer() {
        cam.setFrustumFar(500f);
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(false);
        mat.setColor("Color", ColorRGBA.Black);

        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(4.2f, 0.8f, 4.5f));

        compoundShape.addChildShape(box, new Vector3f(0, 2f, 0));

        chassis =assetManager.loadModel("RocketLeauge/assets/Models/ladaCar.j3o");
        chassis.setShadowMode(RenderQueue.ShadowMode.Cast);
        chassis.setLocalScale(2.2f,2.2f,2.2f);
        chassis.setLocalTranslation(new Vector3f(0, 1.2f, 0));
        //colors
        ((Node) chassis).getChild("glass").setMaterial(createMat(ColorRGBA.Black,"",null));
        ((Node) chassis).getChild("chassis").setMaterial(createMat(ColorRGBA.Cyan, "",chassis));
        ((Node) chassis).getChild("addOns").setMaterial(createMat(null, "RocketLeauge/assets/Textures/bronzeCopperTex.jpg",null));
        ((Node) chassis).getChild("nitro").setMaterial(createMat(new ColorRGBA(0f,0f,5f,1f), "RocketLeauge/assets/Textures/metalBareTex.jpg",null));

        ((Node) chassis).getChild("frontLight").setMaterial(createMat(ColorRGBA.White,"",null));
        ((Node) chassis).getChild("backLights").setMaterial(createMat(ColorRGBA.Red,"",null));
        ((Node) chassis).getChild("uTurns").setMaterial(createMat(ColorRGBA.Yellow,"",chassis));
        ((Node) chassis).getChild("mirrors").setMaterial(createMat(ColorRGBA.White,"",null));


        //create vehicle node
        Node vehicleNode=new Node("vehicleNode");
        vehicleNode.attachChild(chassis);
        vehicleNode.setShadowMode(RenderQueue.ShadowMode.Cast);
//        chassis.setMaterial(mat);
        vehicle = new VehicleControl(compoundShape, 600f);
        vehicleNode.addControl(vehicle);
        vehicle.setPhysicsLocation(new Vector3f(20f,5f,10f));
        //add a chaseCam tomove the cam with the object
        chaseCam = new ChaseCamera(cam, vehicleNode);
        chaseCam.setDefaultDistance(-25f);
        chaseCam.registerWithInput(inputManager);
        chaseCam.setDefaultVerticalRotation(-FastMath.PI/10);
        chaseCam.setDefaultHorizontalRotation(-(FastMath.PI + FastMath.HALF_PI));
        cam.setFrustumFar(2000);
//        chaseCam.registerWithInput(inputManager);
//        chaseCam.setDragToRotate(true);
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness =20.0f;//200=f1 car
        float compValue = 2f; //(should be lower than damp)
        float dampValue = 4f;
        //compression force of spring(Shock Producer)
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        //stretch force of spring(Shock Absorber)
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMaxSuspensionForce(FastMath.pow(2, 20));

        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0,-1F, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-6, 0, 0); // was -1, 0, 0
        float radius = 0.5f;
        float restLength = 0.1f;
        //the offset between the car tyre & the chassis (vertical axle)
        float yOff = radius;
        //-0.3f(localScale of the above wheel part) = the offset between 2 wheels in the same axle ( horizontal axle)
        float xOff = chassis.getLocalScale().getX()-0.3f;
        //the offset between the front axle & the rear axle (depth axle)
        float zOff = chassis.getLocalScale().getZ()+1;

        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.5f, true);

        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);
        Node node3;
        Node node4;
        node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);
        rootNode.attachChild(vehicleNode);

        getPhysicsSpace().add(vehicle);
        DirectionalLight directionalLight=new DirectionalLight(new Vector3f(2,2,2).mult(50).normalize());
        directionalLight.setColor(ColorRGBA.White);
        vehicleNode.addLight(directionalLight);
    }

    public Material createMat(ColorRGBA colorRGBA,String Tex,Spatial node){
        Material material=new Material(assetManager,"Common/MatDefs/Light/PBRLighting.j3md");
        /*metalness , max is 1*/
        material.setFloat("Metallic", 0.5f);
        /*Roughness , 1 is the max roughnesss*/
        material.setFloat("Roughness", 0.5f);
        material.setFloat("EmissivePower",1.0f);
        material.setFloat("EmissiveIntensity",2.0f);
        material.setBoolean("HorizonFade",true);
        material.setVector3("LightDir",new Vector3f(-0.5f,-0.5f,-0.5f).normalize());
        material.setBoolean("BackfaceShadows",true);

        if(colorRGBA !=null){
            /*Diffuse Color*/
            material.setColor("BaseColor", colorRGBA);
            /*Reflection color*/
            material.setColor("Specular", colorRGBA.mult(20f));
        }
        if(Tex.length() >1){
            Texture texture=assetManager.loadTexture(Tex);
            material.setTexture("BaseColorMap",texture);
        }
        material.setReceivesShadows(true);
        if(node !=null){
            addPointLight(node, new Vector3f(0, 30f, 30f), colorRGBA);
        }
        return material;
    }

    @Override
    public void simpleUpdate(float tpf) {

    }
//    private Spatial loadPlayground() {
//        Material material = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
//
//        Texture baseColorMap = assetManager.loadTexture("RocketLeauge/assets/Textures/Ground/Marble/marble_01_diff_2k.png");
//        baseColorMap.setWrap(Texture.WrapMode.Repeat);
//
//        Texture roughnessMap = assetManager.loadTexture("RocketLeauge/assets/Textures/metalBareTex.jpg");
//        roughnessMap.setWrap(Texture.WrapMode.Repeat);
//
//        Texture aoMap = assetManager.loadTexture("RocketLeauge/assets/Textures/Dirt_Bottom-3072.jpg");
//        aoMap.setWrap(Texture.WrapMode.Repeat);
//
//        //Texture dispMap = assetManager.loadTexture("Textures/Ground/Marble/marble_01_disp_2k.png");
//        //dispMap.setWrap(Texture.WrapMode.Repeat);
//        Texture normalMap = assetManager.loadTexture("RocketLeauge/assets/Textures/Ground/Marble/marble_01_nor_2k.png");
//        normalMap.setWrap(Texture.WrapMode.Repeat);
//
//        material.setTexture("BaseColorMap", baseColorMap);
//        material.setTexture("RoughnessMap", roughnessMap);
//        material.setTexture("LightMap", aoMap);
//        //material.setTexture("ParallaxMap", dispMap);
//        material.setTexture("NormalMap", normalMap);
//        material.setFloat("NormalType", 1.0f);
//
//        // material.setColor("BaseColor", ColorRGBA.LightGray);
//        // material.setFloat("Roughness", 0.75f);
//        material.setFloat("Metallic", 0.001f);
//
//        // material.setBoolean("UseFog", true);
//        // material.setColor("FogColor", new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));
//        // material.setFloat("ExpSqFog", 0.002f);
//        RenderState additional = material.getAdditionalRenderState();
//        additional.setFaceCullMode(RenderState.FaceCullMode.Off);
//
//        Spatial playground = assetManager.loadModel("RocketLeauge/assets/Scenes/vehicle-playground/vehicle-playground.j3o");
//        playground.setMaterial(material);
//
//        Node p = (Node) playground;
//        p.breadthFirstTraversal(spatial -> spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive));
//
//        playground.setName("playground");
//        return playground;
//    }
}