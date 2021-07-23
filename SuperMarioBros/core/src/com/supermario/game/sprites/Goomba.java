package com.supermario.game.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.supermario.game.MarioBros;
import com.supermario.game.screens.PlayScreen;

import sun.security.krb5.internal.crypto.Des;

public class Goomba extends Enemy{

    private float stateTimer;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean Destroyed;

    public Goomba( PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        setToDestroy = false;
        Destroyed = false;
        for(int i=0; i<2; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i*16, 0, 16, 16));
        }
        walkAnimation = new Animation(0.4f, frames);
        stateTimer = 0;
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
    }

    @Override
    public void hitByFireBall() {
        setToDestroy = true;
        screen.getGame().assetManager.get("audio/sound/stomp.wav", Sound.class).play();
    }

    public void update(float deltaTime){
        stateTimer += deltaTime;
        if(setToDestroy && !Destroyed){
            stateTimer = 0;
            world.destroyBody(this.b2body);
            Destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
        }else if(!Destroyed){
            velocity.y = b2body.getLinearVelocity().y;
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTimer, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        fdef.friction = 0f;
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT
                | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.MARIO_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.FIREBALL_BIT;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //create head
        PolygonShape head = new PolygonShape();
        Vector2[] Vertice = new Vector2[4];
        Vertice[0] = new Vector2(-5, 9).scl(1 / MarioBros.PPM);
        Vertice[1] = new Vector2(5, 9).scl(1 / MarioBros.PPM);
        Vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        Vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(Vertice);

        fdef.shape = head;
        fdef.restitution = 0.6f; // after collision fixA will bounce to the direction of collision
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);

    }

    public void draw(Batch batch){
        if(!Destroyed || stateTimer < 1)super.draw(batch);
        else{

        }
    }

    public void hitOnHead(Mario mario){
        setToDestroy = true;
        screen.getGame().assetManager.get("audio/sound/stomp.wav", Sound.class).play();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle)enemy).currentState == Turtle.State.MOVING_SHELL){
            setToDestroy = true;
            screen.getGame().assetManager.get("audio/sound/stomp.wav", Sound.class).play();
        }else{
            reverseVelocity(true, false);
        }
    }
}
