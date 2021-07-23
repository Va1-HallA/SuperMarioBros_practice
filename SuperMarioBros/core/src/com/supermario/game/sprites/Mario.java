package com.supermario.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMRepeatingLeaf;
import com.supermario.game.MarioBros;
import com.supermario.game.screens.PlayScreen;
import com.supermario.game.sprites.Enemy;
import com.supermario.game.sprites.FireBall;
import com.supermario.game.sprites.Turtle;

import org.graalvm.compiler.phases.common.NodeCounterPhase;

public class Mario extends Sprite {
    public enum State{FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD}
    private MarioBros game;

    private PlayScreen screen;

    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;
    private TextureRegion marioStanding;
    private TextureRegion bigMarioStanding;
    private TextureRegion bigMarioJumping;
    private TextureRegion marioDead;
    private Animation bigMarioRun;
    private Animation marioGrow;

    private Animation marioRun;
    private Animation marioJump;

    private boolean runningForward;
    boolean isBig;
    private boolean isDead;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean runGrowAnimation;
    private float stateTimer;

    public Mario(PlayScreen screen) {
        this.screen =screen;
        this.world = screen.getWorld();
        this.game = screen.getGame();
        this.timeToDefineBigMario = false;
        this.timeToRedefineMario = false;
        defineMario();
        marioStanding = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStanding = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        bigMarioJumping = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStanding);

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningForward = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 4; i < 6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioJump = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0,0,16,32));
        marioGrow = new Animation(0.2f, frames);
    }

    public void update(float deltaTime){
        updateState();
        if(currentState == State.DEAD){b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);}
        if(isBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
        else setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(deltaTime));
        if(timeToDefineBigMario) defineBigMario();
        if(timeToRedefineMario) redefineMario();
    }

    public float getStateTimer(){return this.stateTimer;}

    public TextureRegion getFrame(float deltaTime){
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) marioGrow.getKeyFrame(stateTimer);
                if(marioGrow.isAnimationFinished(stateTimer))runGrowAnimation = false;
                break;
            case JUMPING:
                region = isBig ? bigMarioJumping : (TextureRegion) marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = isBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true) : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = isBig ? bigMarioStanding : marioStanding;
        }

        //handling flip
        if((b2body.getLinearVelocity().x<0 || !runningForward) && !region.isFlipX()){
            region.flip(true, false);
            runningForward = false;
        }else if((b2body.getLinearVelocity().x>0 || runningForward) && region.isFlipX()){
            region.flip(true,false);
            runningForward = true;
        }

        //handling stateTimer
        stateTimer = currentState == previousState ? stateTimer + deltaTime: 0;
        return region;
    }

    public void handleInput(float deltaTime){
        if(currentState != State.DEAD){
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && (currentState != State.JUMPING)){
                b2body.applyLinearImpulse(new Vector2(0,4f), b2body.getWorldCenter(),true);
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                fire();
            }
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && b2body.getLinearVelocity().x >= -2){
                b2body.applyLinearImpulse(new Vector2(-0.15f, 0), b2body.getWorldCenter(), true);
            }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && b2body.getLinearVelocity().x <= 2){
                b2body.applyLinearImpulse(new Vector2(0.15f, 0), b2body.getWorldCenter(), true);
            }
        }
    }

    public void grow(){
        runGrowAnimation = true;
        isBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(),getY(),getWidth(),getHeight() * 2);
        this.game.assetManager.get("audio/sound/powerup.wav", Sound.class).play();
    }


    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.STANDING_SHELL){
            ((Turtle)enemy).kick(this.getX() <= enemy.getX() ? Turtle.kickRightSpeed : Turtle.kickLeftSpeed);
        }else{
            if(isBig) {
                isBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                game.assetManager.get("audio/sound/powerdown.wav", Sound.class).play();
            }else{
                game.assetManager.get("audio/music/music.ogg", Music.class).stop();
                game.assetManager.get("audio/sound/mariodie.wav", Sound.class).play();
                isDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for(Fixture fixture: b2body.getFixtureList()) fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }

    }

    public void hit(){
        game.assetManager.get("audio/music/music.ogg", Music.class).stop();
        game.assetManager.get("audio/sound/mariodie.wav", Sound.class).play();
        isDead = true;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;
        for(Fixture fixture: b2body.getFixtureList()) fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }

    public void fire(){
        screen.fireBalls.add(new FireBall(screen, getX(), getY(), runningForward));
        Gdx.app.log("fireball forward", String.valueOf(runningForward));
    }

    public void updateState(){
        previousState = currentState;
        if(isDead){
            currentState = State.DEAD;
        }
        else if(runGrowAnimation){
            currentState = State.GROWING;
        }
        else if(b2body.getLinearVelocity().y>0 || (b2body.getLinearVelocity().y<0 && previousState == State.JUMPING)) {
            currentState = State.JUMPING;
        }
        else if(b2body.getLinearVelocity().y<0){
            currentState = State.FALLING;
        }
        else if(b2body.getLinearVelocity().x!=0){
            currentState = State.RUNNING;
        }
        else{
            currentState = State.STANDING;
        }
    }

    public void defineMario(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        fdef.friction = 1f;
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT
                | MarioBros.BOTTOM_BIT
                | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.ENEMY_HEAD_BIT
                | MarioBros.ITEM_BIT;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits =
                MarioBros.BRICK_BIT
                | MarioBros.COIN_BIT;
        b2body.createFixture(fdef).setUserData(this); // gives this fixture an identity of head
    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        fdef.friction = 1f;
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT
                | MarioBros.BOTTOM_BIT
                | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.ENEMY_HEAD_BIT
                | MarioBros.ITEM_BIT;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits =
                MarioBros.BRICK_BIT
                        | MarioBros.COIN_BIT;
        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(position));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        fdef.friction = 1f;
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT
                | MarioBros.BOTTOM_BIT
                | MarioBros.COIN_BIT
                | MarioBros.BRICK_BIT
                | MarioBros.ENEMY_BIT
                | MarioBros.OBJECT_BIT
                | MarioBros.ENEMY_HEAD_BIT
                | MarioBros.ITEM_BIT;
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.filter.maskBits =
                MarioBros.BRICK_BIT
                        | MarioBros.COIN_BIT;
        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;
    }
}
