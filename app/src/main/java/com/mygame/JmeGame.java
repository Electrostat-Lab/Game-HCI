package com.mygame;

import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.mygame.basic_android_template.R;

public class JmeGame extends SimpleApplication {

    private BulletAppState bulletAppState;
    private VehicleControl vehicle;
    private final float accelerationForce = FastMath.pow(5, 3.5f);
    private final float brakeForce = 300f;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private final Vector3f jumpForce = new Vector3f(0, 3000, 0);


    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);
        createPhysicsTestWorld(rootNode, getAssetManager(), bulletAppState.getPhysicsSpace());
        buildPlayer();


        /*run the gamePad Attachments & listeners from the android activity UI thread */
        JmeHarness.jmeHarness.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /* create an instance of a class extending gameStickView to easily handle the listeners */
                GameStick gameStick = new GameStick(JmeHarness.jmeHarness,JmeGame.this);
                /* set the vehicle Control */
                gameStick.setVehicleControl(vehicle);
                gameStick.accelerate(200f);
                /* create a gamePadView instance of cardView/FrameLayout to display gamePad Component */
                GamePadView gamePadView=new GamePadView( JmeHarness.jmeHarness,gameStick);
                /* Initialize GamePad Parts*/
                gamePadView.initializeGamePad(R.drawable.gamepad_domain,GamePadView.HALF_SCREEN)
                         .initializeGameStickHolder(R.drawable.moving_stick_domain)
                         .initializeGameStick(GamePadView.CRYSTAL_BUTTONS,R.drawable.ic_baseline_gamepad_24,200);
                /*initialize the gameStick track */
                gamePadView.setMotionPathColor(Color.BLACK);
                gamePadView.setMotionPathStrokeWidth(10);
                gamePadView.setStickPathEnabled(true);
                /* initialize pad buttons & listeners A,B,X,Y */
                gamePadView.addControlButton("BUTTON A",GamePadView.GAMEPAD_BUTTON_A ,GamePadView.CRYSTAL_BUTTONS, R.drawable.ic_baseline_gamepad_24,new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle.accelerate(accelerationForce);
                    }
                },null);
                gamePadView.addControlButton("BUTTON B",GamePadView.GAMEPAD_BUTTON_B , GamePadView.CRYSTAL_BUTTONS, R.drawable.ic_baseline_gamepad_24,new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle.brake(brakeForce);
                    }
                },null);
                gamePadView.addControlButton("BUTTON X",GamePadView.GAMEPAD_BUTTON_X , GamePadView.CRYSTAL_BUTTONS, R.drawable.ic_baseline_gamepad_24,new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle.brake(brakeForce);
                    }
                },null);
                gamePadView.addControlButton("BUTTON Y",GamePadView.GAMEPAD_BUTTON_Y , GamePadView.CRYSTAL_BUTTONS, R.drawable.ic_baseline_gamepad_24,new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vehicle.brake(brakeForce);
                    }
                },null);

            }
        });
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
    public static void createPhysicsTestWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);


        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("mars.jpg"));

        Box floorBox = new Box(200f, 0.5f, 200f);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material);
        floorGeometry.setLocalTranslation(0, -5, 0);
        Plane plane = new Plane();
        plane.setOriginNormal(new Vector3f(0, 0.25f, 0), Vector3f.UNIT_Y);
        floorGeometry.addControl(new RigidBodyControl(new PlaneCollisionShape(plane), 0));
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);

        //movable boxes
        for (int i = 0; i < 12; i++) {
            Box box = new Box(0.25f, 0.25f, 0.25f);
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(material);
            boxGeometry.setLocalTranslation(i, 5, -3);
            //RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
            boxGeometry.addControl(new RigidBodyControl(2));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }

        //immovable sphere with mesh collision shape
        Sphere sphere = new Sphere(8, 8, 1);
        Geometry sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(material);
        sphereGeometry.setLocalTranslation(4, -4, 2);
        sphereGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(sphere), 0));
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);

    }



    private void buildPlayer() {
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(false);
        mat.setColor("Color", ColorRGBA.Black);

        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(2f, 0.5f, 2.5f));

        compoundShape.addChildShape(box, new Vector3f(0, 1.5f, 0));

        Spatial spatial=assetManager.loadModel("Mars.j3o");
        spatial.setLocalScale(0.01f,0.01f,0.01f);
        spatial.setLocalTranslation(new Vector3f(0, 1.5f, 0));

        //create vehicle node
        Node vehicleNode=new Node("vehicleNfode");
        vehicleNode.attachChild(spatial);

//        chassis.setMaterial(mat);
//        vehicleNode.attachChild(chassis);
        vehicle = new VehicleControl(compoundShape, 600f);
        vehicleNode.addControl(vehicle);
        //add a chaseCam tomove the cam with the object

        ChaseCamera chaseCam=new ChaseCamera(cam, vehicleNode);
        chaseCam.setDefaultDistance(-20f);
        chaseCam.registerWithInput(inputManager);
        chaseCam.setDragToRotate(true);
        chaseCam.registerWithInput(inputManager);
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness =30.0f;//200=f1 car
        float compValue = 0.3f; //(should be lower than damp)
        float dampValue = 2f;
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
        float yOff = radius;
        float xOff = 2*radius;
        float zOff = 4*radius;

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

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
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
    }

    @Override
    public void simpleUpdate(float tpf) {
        cam.lookAt(vehicle.getPhysicsLocation(), Vector3f.UNIT_Y);
        System.out.println();
    }

}