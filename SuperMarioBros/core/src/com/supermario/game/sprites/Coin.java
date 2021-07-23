package com.supermario.game.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.supermario.game.MarioBros;
import com.supermario.game.sprites.item.ItemDef;
import com.supermario.game.sprites.item.Mushroom;
import com.supermario.game.screens.PlayScreen;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {

        super(screen, object);
        fixture.setUserData(this);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN){
            game.assetManager.get("audio/sound/bump.wav", Sound.class).play();
        }else{
            game.assetManager.get("audio/sound/coin.wav", Sound.class).play();
            screen.addScore(200);
            if(object.getProperties().containsKey("mushroom")){
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y+16/MarioBros.PPM),
                        Mushroom.class));
                game.assetManager.get("audio/sound/powerup_spawn.wav", Sound.class).play();
            }
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }
}
