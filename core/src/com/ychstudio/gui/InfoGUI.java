package com.ychstudio.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ychstudio.actors.Player;
import com.ychstudio.gamesys.GM;

public class InfoGUI implements Disposable {

	private final float screenWidth = Gdx.graphics.getWidth();
	private final float screenHeight = Gdx.graphics.getHeight();
	
	private SpriteBatch batch;
	
    private Stage stage;
    private FitViewport viewport;
    
    private BitmapFont font;
    
    private VisLabel fpsLabel;
    private VisLabel hpLabel;
    
    private Sprite bulletSprite;
    private Sprite grenadeSprite;
    private Sprite reloadSprite;
    
    private int playerHP;
    private int playerAmmo;
    private int playerMaxAmmo;
    private int playerGrenade;
    private int playerMaxGrenade;
    private float playerReloadTime;
    private float playerReloadTimeLeft;
    
    public InfoGUI() {
        VisUI.load();
        
        batch = new SpriteBatch();
        
        viewport = new FitViewport(screenWidth, screenHeight);
        stage = new Stage(viewport, batch);
        
        font = new BitmapFont(Gdx.files.internal("fonts/Monofonto_32.fnt"));
        LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
        
        fpsLabel = new VisLabel("FPS:");
        hpLabel = new VisLabel("HP:", labelStyle);
        hpLabel.setPosition(16f, screenHeight - 40f);;
        
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.bottom().left().padLeft(16f);
        table.add(fpsLabel);
        stage.addActor(hpLabel);
        stage.addActor(table);
        
        
        TextureAtlas textureAtlas = GM.getAssetManager().get("img/actors.pack", TextureAtlas.class);
        bulletSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Bullet"), 0, 0, 32, 32));
        bulletSprite.setBounds(0, 0, 32f, 32f);
        
        grenadeSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Grenade"), 0, 0, 32, 32));
        grenadeSprite.setBounds(0, 0, 32f, 32f);
        
        reloadSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Bar"), 0, 0, 32, 32));
    }
    
    public void update() {
    	fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    	hpLabel.setText("HP: " + playerHP);
    	
    	if (GM.getPlayer() != null) {
    		Player player = GM.getPlayer();
    		playerAmmo = player.getAmmo();
    		playerMaxAmmo = player.getMaxAmmo();
    		playerReloadTime = player.getReloadTime();
    		playerReloadTimeLeft = player.getReloadTimeLeft();
    		playerGrenade = player.getGrenade();
    		playerMaxGrenade = player.getMaxGrenade();
    		playerHP = player.getHP();
    	}
    	else {
    		playerAmmo = 0;
    		playerMaxAmmo = 0;
    		playerReloadTime = 0;
    		playerReloadTimeLeft = 0;
    		playerGrenade = 0;
    		playerMaxGrenade = 0;
    		playerHP = 0;
    	}
    }
    
    public void draw() {
    	update();
    	
    	batch.begin();
    	// draw ammo indication
    	for (int i = 0; i < playerMaxAmmo; i++) {
    		bulletSprite.setPosition(10f + 16f * i, screenHeight - 60f);
    		if (i >= playerAmmo) {
    			bulletSprite.setAlpha(0.3f);
    		}
    		else {
    			bulletSprite.setAlpha(1.0f);
    		}
    		bulletSprite.draw(batch);
    	}
    	
    	// draw reload indication
    	if (playerReloadTime != 0) {
    		reloadSprite.setBounds(18f, screenHeight - 48f, 128f * (1 - playerReloadTimeLeft / playerReloadTime), 8f);
    		reloadSprite.draw(batch);
    	}
    	
    	// draw grenade indication
    	for (int i = 0; i < playerMaxGrenade; i++) {
    	    grenadeSprite.setPosition(10f + 16f * i, screenHeight - 80f);
    	    if (i >= playerGrenade) {
    	        grenadeSprite.setAlpha(0.3f);
    	    }
    	    else {
    	        grenadeSprite.setAlpha(1f);
    	    }
    	    grenadeSprite.draw(batch);
    	}
    	
    	batch.end();
    	
        stage.draw();
    }
    
    @Override
    public void dispose() {
        font.dispose();
        stage.dispose();
        batch.dispose();
        VisUI.dispose();
    }

}
