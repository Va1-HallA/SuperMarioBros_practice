package com.supermario.game.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.supermario.game.MarioBros;
import com.supermario.game.scenes.Hud;
import com.supermario.game.screens.PlayScreen;

public class Brick extends InteractiveTileObject{
    public Brick(PlayScreen screen, MapObject object) {

        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig){
            game.assetManager.get("audio/sound/breakblock.wav", Sound.class).play();
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            screen.addScore(100);
        }else
            game.assetManager.get("audio/sound/bump.wav",Sound.class).play();
    }
}
