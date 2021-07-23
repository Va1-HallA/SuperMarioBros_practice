package com.supermario.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.supermario.game.MarioBros;
import com.supermario.game.screens.PlayScreen;

import sun.security.krb5.internal.crypto.Des;

public class Turtle extends Enemy{
    public static final int kickLeftSpeed = -2;
    public static final int kickRightSpeed = 2;
    public enum State{WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTimer;
    private Animation walkAnimation;
    private float deadRotationDegrees;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean setToDestroy;
    private boolean Destroyed;

    public Turtle(PlayScreen screen, float x, float y){
        super(screen, x, y);
        frames = new Array<>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation(0.2f, frames);
        deadRotationDegrees = 0;
        currentState = State.WALKING;
        previousState = State.WALKING;

        setBounds(getX(), getY(), 16 / MarioBros.PPM, 24 / MarioBros.PPM);
    }

    @Override
    public void hitByFireBall() {
        killed();
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
        fdef.restitution = 1.5f; // after collision fixA will bounce to the direction of collision
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL){
            screen.getGame().assetManager.get("audio/sound/stomp.wav", Sound.class).play();
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }else{
            kick(mario.getX() <= this.getX() ? kickRightSpeed : kickLeftSpeed);
        }
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle){
            if(((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }else if(currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING){
                return;
            }else if(currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.STANDING_SHELL){
                enemy.velocity.x = getX() <= enemy.getX() ? 2f : -2f;
                ((Turtle) enemy).currentState = State.MOVING_SHELL;
            } else{
                reverseVelocity(true, false);
            }
        }
        else if(currentState != State.MOVING_SHELL){
            reverseVelocity(true, false);
        }
    }

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        if(currentState == State.STANDING_SHELL && stateTimer > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / MarioBros.PPM);
        if(currentState == State.DEAD){
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if(stateTimer > 5 && !Destroyed){
                screen.creator.removeTurtle(this);
                world.destroyBody(b2body);
                Destroyed = true;
            }
        }else{
            b2body.setLinearVelocity(velocity);
        }

    }

    public TextureRegion getFrame(float deltaTime){
        TextureRegion region;

        switch (currentState){
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = (TextureRegion) walkAnimation.getKeyFrame(stateTimer, true);
                break;
        }
        //handling flipping
        if(velocity.x > 0 && region.isFlipX() == false){
            region.flip(true, false);
        }
        if(velocity.x < 0 && region.isFlipX() == true){
            region.flip(true, false);
        }
        //handling stateTimer
        stateTimer = currentState == previousState ? stateTimer + deltaTime: 0;
        previousState = currentState;
        return region;
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;

        for(Fixture fixture: b2body.getFixtureList()){
            fixture.setFilterData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }
}
