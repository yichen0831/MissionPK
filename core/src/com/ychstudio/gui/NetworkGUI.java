package com.ychstudio.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class NetworkGUI implements Disposable {
    
    private Stage stage;
    
    public NetworkGUI() {
        
        stage = new Stage();
        
        VisLabel label = new VisLabel("Test!!!");
        VisTextButton button = new VisTextButton("Close");
        
        VisWindow window = new VisWindow("Network");
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2f, (Gdx.graphics.getHeight() - window.getHeight()) / 2f);
        
        window.add(label);
        window.add(button);
        stage.addActor(window);
        
        Gdx.input.setInputProcessor(stage);
        
    }
    
    public void draw() {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        
    }
    
    

}
