package com.myGame.JMESurfaceViewExampleActivity.compatTest;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class CompatGame extends SimpleApplication {
    @Override
    public void simpleInitApp() {
        Box box=new Box(50,50,10);
        Geometry geometry = new Geometry("box", box);
        geometry.setLocalScale(0.05f);
        Material material = new Material(assetManager.loadAsset(new AssetKey<>("Common/MatDefs/Misc/Unshaded.j3md")));
        material.setColor("Color", ColorRGBA.randomColor().mult(2f));
        geometry.setMaterial(material);
        getFlyByCamera().setEnabled(true);


        rootNode.attachChild(geometry);
    }
}
