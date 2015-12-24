package com.ychstudio;

import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import com.ychstudio.gamesys.GM;
import com.ychstudio.screens.PlayScreen;

public class MissionPK extends Game {
	
	@Override
	public void create () {
	    VisUI.load();
		setScreen(new PlayScreen());
	}

	@Override
	public void render () {
	    super.render();

	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
    
    @Override
    public void dispose() {
        GM.getInstance().dispose();
        VisUI.dispose();
    }
}
