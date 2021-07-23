package com.supermario.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.supermario.game.screens.GameEndScreen;
import com.supermario.game.screens.PlayScreen;

public class MarioBros extends Game {
	public SpriteBatch batch;
	public BitmapFont font;

	public AssetManager assetManager;

	public static final int V_HEIGHT = 208;
	public static final int V_WIDTH = 480;
	public static final float PPM = 100;

	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short BOTTOM_BIT = 1024;
	public static final short FIREBALL_BIT = 1024;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		assetManager = new AssetManager();
		assetManager.load("audio/music/music.ogg", Music.class);
		assetManager.load("audio/sound/breakblock.wav", Sound.class);
		assetManager.load("audio/sound/bump.wav", Sound.class);
		assetManager.load("audio/sound/coin.wav", Sound.class);
		assetManager.load("audio/sound/mariodie.wav", Sound.class);
		assetManager.load("audio/sound/powerdown.wav", Sound.class);
		assetManager.load("audio/sound/powerup.wav", Sound.class);
		assetManager.load("audio/sound/stomp.wav", Sound.class);
		assetManager.load("audio/sound/powerup_spawn.wav", Sound.class);
		assetManager.finishLoading();
		this.setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
