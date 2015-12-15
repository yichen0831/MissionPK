package com.ychstudio.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class InfoGUI implements Disposable {

    private Stage stage;
    private FitViewport viewport;
    
    private BitmapFont font;
    
    private VisLabel fpsLabel;
    
    public InfoGUI() {
        VisUI.load();
        
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        
        font = new BitmapFont(Gdx.files.internal("fonts/Monofonto_32.fnt"));
        LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
        
        fpsLabel = new VisLabel("FPS:", labelStyle);
        
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.top().left().padLeft(6f);
        table.add(fpsLabel);
        
        stage.addActor(table);
        
    }
    
    public void draw() {
        fpsLabel.setText("FPS:" + Gdx.graphics.getFramesPerSecond());
        stage.draw();
    }
    
    @Override
    public void dispose() {
        font.dispose();
        stage.dispose();
        
    }

}
