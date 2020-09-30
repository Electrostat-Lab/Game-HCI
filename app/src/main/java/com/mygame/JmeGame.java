package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class JmeGame extends SimpleApplication {

    public JmeGame() {
        // super(new AppState[0]);
    }

    @Override
    public void simpleInitApp() {

        // 0, 0, 0 reference
        Box box = new Box(1, 1, 1);
        Geometry geom = new Geometry("box", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);

        geom.setMaterial(mat);

        rootNode.attachChild(geom);
    }

}