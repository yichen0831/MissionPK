package com.ychstudio.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ychstudio.screens.PlayScreen;

public class OptionGUI implements Disposable {
    
    private final PlayScreen _playScreen;
    
    private Stage stage;
    private FitViewport viewport;
    
    public OptionGUI(PlayScreen playerScreen) {
        this._playScreen = playerScreen;
        
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        
        VisTextButton restartButton = new VisTextButton("Restart");
        restartButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                _playScreen.restart();
            }
            
        });
        
        VisTextButton goToMainMenuButton = new VisTextButton("Go to MainMenu");
        goToMainMenuButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                _playScreen.backToMainMenu();
            }
            
        });
        
        VisTable table = new VisTable();
        table.top().center();
        table.add(restartButton).padBottom(12f);
        table.row();
        table.add(goToMainMenuButton);
        
        VisWindow window = new VisWindow("Option");
        window.add(table);
        window.setSize(320f, 240f);
        
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2f, (Gdx.graphics.getHeight() - window.getHeight()) / 2f);
        
        stage.addActor(window);
        
        Gdx.input.setInputProcessor(stage);
        
    }
    
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    public void draw() {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        
    }
    
    

}
