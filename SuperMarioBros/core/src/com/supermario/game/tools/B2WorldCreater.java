package com.supermario.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.supermario.game.MarioBros;
import com.supermario.game.screens.PlayScreen;
import com.supermario.game.sprites.Brick;
import com.supermario.game.sprites.Coin;
import com.supermario.game.sprites.Enemy;
import com.supermario.game.sprites.Goomba;
import com.supermario.game.sprites.Turtle;

public class B2WorldCreater {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    public B2WorldCreater(PlayScreen screen, MarioBros game){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        TiledMap map = screen.getMap();
        World world = screen.getWorld();

        // create ground bodies / fixtures
        for(MapObject object: map.getLayers().get(2).getObjects()){

            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM, (rect.getHeight() / 2) / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.GROUND_BIT;
            body.createFixture(fdef);

        }
        // create pipes bodies / fixtures
        for(MapObject object: map.getLayers().get(3).getObjects()){

            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM, (rect.getHeight() / 2) / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);

        }
        // create bricks bodies / fixtures
        for(MapObject object: map.getLayers().get(5).getObjects()){

            new Brick(screen, object);

        }
        // create coins bodies / fixtures
        for(MapObject object: map.getLayers().get(4).getObjects()){

            new Coin(screen, object);

        }

        //create goombas
        goombas = new Array<>();
        for(MapObject object: map.getLayers().get(7).getObjects()){
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX()/ MarioBros.PPM, rect.getY()/ MarioBros.PPM));
        }

        //create turtles
        turtles = new Array<>();
        for(MapObject object: map.getLayers().get(9).getObjects()){
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX()/ MarioBros.PPM, rect.getY()/ MarioBros.PPM));
        }

        //create bottom of the world
        for(MapObject object: map.getLayers().get(8).getObjects()){
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM, (rect.getHeight() / 2) / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.BOTTOM_BIT;
            fdef.filter.maskBits = MarioBros.MARIO_BIT;
            fdef.isSensor = true;
            body.createFixture(fdef);
        }
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }

    public void removeTurtle(Turtle turtle){
        this.turtles.removeValue(turtle, true);
    }
}
